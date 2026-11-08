package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.cache.def.GameConstantContainer;
import net.scapeemulator.game.cache.GameConstants;
import net.scapeemulator.game.cache.StructDefinition;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Appearance;
import net.scapeemulator.game.model.player.Gender;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.child.ChildInterfaceSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Build-592 character setup, using the same enums and dynamic slots as scripts 2731-2751. */
public final class CharacterCreation extends Interface {
	public static final CharacterCreation INSTANCE = new CharacterCreation();
	private static final Logger logger = LoggerFactory.getLogger(CharacterCreation.class);
	private static final int[][] STYLE_ENUMS = {
		{2338, 703, 690, 711, 749, 1586, 1136},
		{2341, -1, 1591, 693, 751, 1607, 1137}
	};
	private static final int[] COLOUR_ENUMS = {2345, 2349, 2351, 1589, 748};
	private static final int[] CATEGORY_STYLES = {-1, 0, 1, 2, 3, 4, 5, 6};
	private static final int[] CATEGORY_COLOURS = {4, 0, -1, 1, -1, -1, 2, 3};

	private CharacterCreation() {
		super(900, InterfacePersistence.PERSISTENT);
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return DisplayMode.FULLSCREEN_INTERFACE;
	}

	public static void openIfRequired(Player player) {
		if (!player.isCharacterCreationRequired()
				|| player.getDisplay().getInterface(INSTANCE.getId()) != null)
			return;
		player.stopAction();
		player.getMovement().reset();
		player.setCharacterCreationCategory(0);
		INSTANCE.normalize(player, player.getAppearance().getGender(), false);
		player.getDisplay().openRootInterface(INSTANCE, true);
	}

	@Override
	public void dispatch(Player player, InterfaceAction action) {
		if (!player.isCharacterCreationRequired())
			return;
		if (action instanceof ChildInterfaceClickAction) {
			click(player, (ChildInterfaceClickAction) action);
		} else if (action.getType() == InterfaceAction.ActionType.OPEN_INTER) {
			// Static buttons already send plain clicks; only generated grids need masks.
			enable(player, 66, 0, 130);
			enable(player, 73, 0, 255);
			synchronizeGender(player);
			synchronizePreview(player);
		}
	}

	private void enable(Player player, int child, int start, int end) {
		ChildInterfaceSettings settings = new ChildInterfaceSettings(getId(), child);
		settings.setAffectingSlots(start, end);
		settings.enableOptions(1);
		player.send(settings);
	}

	private void click(Player player, ChildInterfaceClickAction action) {
		// Scripted buttons use a plain click (option 0); dynamic choices use option 1.
		if ((action.getOption() != 0 && action.getOption() != 1)
				|| action.getInterfaceId() != getId())
			return;
		int child = action.getChild();
		if (child == 15 || child == 20) {
			player.setCharacterCreationCategory(0);
			normalize(player, child == 15 ? Gender.MALE : Gender.FEMALE, false);
			synchronizeGender(player);
			synchronizePreview(player);
		} else if (child == 27) {
			// A preset supplies a complete, valid look from this cache's creation options.
			normalize(player, player.getAppearance().getGender(), true);
			synchronizePreview(player);
		} else if (child >= 33 && child <= 61 && (child - 33) % 4 == 0) {
			int category = (child - 33) / 4;
			if (category == 2 && player.getAppearance().getGender() == Gender.FEMALE)
				return;
			// The client owns its active tab; sending varc 1020 resets it to Skin.
			player.setCharacterCreationCategory(category);
		} else if ((child == 66 || child == 73) && action.getOption() == 1) {
			select(player, child, action.getSlot());
		} else if (child == 81) {
			confirm(player);
		}
	}

	private void select(Player player, int child, int slot) {
		int category = player.getCharacterCreationCategory();
		Appearance current = player.getAppearance();
		int[] styles = styles(current), colours = colours(current);
		int varc, selected;
		if (child == 66) {
			// Script 2740: three children per option, clickable overlay at index * 3 + 2.
			if (slot < 2 || slot % 3 != 2)
				return;
			int style = CATEGORY_STYLES[category];
			if (style == -1)
				return;
			Integer value = styleOption(current.getGender(), style, slot / 3);
			if (value == null)
				return;
			styles[style] = value;
			varc = 1008 + style;
			selected = value;
		} else {
			// Script 2744: two children per option, clickable overlay at index * 2 + 1.
			if (slot < 1 || slot % 2 != 1)
				return;
			int colour = CATEGORY_COLOURS[category];
			if (colour == -1)
				return;
			Integer value = option(COLOUR_ENUMS[colour], slot / 2);
			if (value == null)
				return;
			// Footwear enum 1589 contains IDs offset by 3680, not grid indices.
			selected = colour == 3 ? value - 3680 : value;
			if (selected < 0 || selected > 255)
				return;
			colours[colour] = selected;
			varc = 1015 + colour;
		}
		// CS2 scripts 2743/2745 already update the preview via 403/404.
		// Echoing a varc or full appearance block causes script 2731 to
		// rebuild it and may recolour unrelated parts (skin vs hair).
		// Store both values on the server without triggering client scripts.
		player.setCharacterCreationAppearance(new Appearance(current.getGender(), styles, colours));
		player.getStatus().setVarCLocal(varc, selected);
	}

	private void normalize(Player player, Gender gender, boolean preset) {
		Appearance old = player.getAppearance();
		int[] styles = styles(old), colours = colours(old);
		for (int i = 0; i < styles.length; i++) {
			int enumId = STYLE_ENUMS[gender.ordinal()][i];
			if (enumId == -1) {
				// Female models use this as a secondary torso slot, not a beard.
				styles[i] = 1000;
				continue;
			}
			Map<Integer, Integer> options = values(enumId);
			boolean valid = i == 0 ? validHair(options, styles[i]) : options.containsValue(styles[i]);
			if (preset || !valid) {
				int index = preset && !options.isEmpty() ? ThreadLocalRandom.current().nextInt(options.size()) : 0;
				Integer selected = styleOption(gender, i, index);
				if (selected != null)
					styles[i] = selected;
				else if (i == 0)
					styles[i] = gender == Gender.MALE ? 8 : 223;
			}
		}
		for (int i = 0; i < colours.length; i++) {
			Map<Integer, Integer> options = values(COLOUR_ENUMS[i]);
			boolean valid = options.containsValue(i == 3 ? 3680 + colours[i] : colours[i]);
			if (preset || !valid) {
				int index = preset && !options.isEmpty() ? ThreadLocalRandom.current().nextInt(options.size()) : 0;
				Integer value = options.get(index);
				if (value != null) {
					int colour = i == 3 ? value - 3680 : value;
					if (colour >= 0 && colour <= 255)
						colours[i] = colour;
				}
			}
		}
		player.setAppearance(new Appearance(gender, styles, colours));
	}

	private static boolean validHair(Map<Integer, Integer> options, int hair) {
		for (int structId : options.values()) {
			Integer kit = StructDefinition.getIntParam(structId, 788);
			if (kit != null && kit == hair)
				return true;
		}
		return false;
	}

	private static Integer styleOption(Gender gender, int style, int index) {
		Integer value = option(STYLE_ENUMS[gender.ordinal()][style], index);
		if (value == null)
			return null;
		// Hair enums 2338/2341 map to structs; parameter 788 is the kit ID.
		return style == 0 ? StructDefinition.getIntParam(value, 788) : value;
	}

	private void synchronizeGender(Player player) {
		player.getStatus().setVarBit(6501, player.getAppearance().getGender().ordinal());
	}

	private void synchronizePreview(Player player) {
		Appearance appearance = player.getAppearance();
		for (int i = 0; i < 7; i++)
			player.getStatus().setVarC(1008 + i, appearance.getStyle(i));
		for (int i = 0; i < 5; i++)
			player.getStatus().setVarC(1015 + i, appearance.getColor(i));
	}

	private void confirm(Player player) {
		if (player.getSession() == null) {
			player.sendMessage("Your appearance could not be saved. Please try again.");
			return;
		}
		player.setCharacterCreationRequired(false);
		player.setControlsTutorialPending(true);
		try {
			player.getSession().getService().getLoginService().savePlayer(player);
		} catch (RuntimeException ex) {
			player.setControlsTutorialPending(false);
			player.setCharacterCreationRequired(true);
			logger.error("Could not save character creation for {}.", player.getUsername(), ex);
			player.sendMessage("Your appearance could not be saved. Please try confirming again.");
			return;
		}
		// Reapply the accepted design to the normal player-update stream only
		// after confirming, never while the client owns the preview.
		player.refreshAppearance();
		// Replacing a fullscreen root sends the new root; no invalid close(-1) packet.
		player.getDisplay().close(this, false);
		// PlayerUpdater opens the prompt after the newly restored HUD has been
		// delivered on separate ticks, rather than in this same network flush.
		player.deferControlsTutorial(2);
	}

	private static Map<Integer, Integer> values(int enumId) {
		GameConstantContainer<Integer> constants = GameConstants.getIntegerConstants(enumId);
		return constants.getValues();
	}

	private static Integer option(int enumId, int index) {
		return enumId == -1 ? null : values(enumId).get(index);
	}

	private static int[] styles(Appearance appearance) {
		int[] result = new int[7];
		for (int i = 0; i < result.length; i++) result[i] = appearance.getStyle(i);
		return result;
	}

	private static int[] colours(Appearance appearance) {
		int[] result = new int[5];
		for (int i = 0; i < result.length; i++) result[i] = appearance.getColor(i);
		return result;
	}
}
