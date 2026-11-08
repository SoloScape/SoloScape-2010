package net.scapeemulator.game.model.hud.combat;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.var.Status;

public class PrayerTab extends Tab {
	private static final ClickActionHandler PRAYER_HANDLER = new PrayerHandler();

	public static final int PRAYER = 1395;
	public static final int QUICK_PRAYER = 1397;

	public static final int SELECTING_QUICK_PRAYER = 181;
	public static final int QUICK_PRAYER_ENABLED = 182;

	public PrayerTab() {
		super(271, Tab.PRAYER);
		addChild(new QuickPrayer());
		addChild(new Prayer());
		addChild(new FinishSelection());
		/**
		 * TODO: Prayer trigger -> prayer drain
		 */
	}

	@Override
	public void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case OPEN_INTER:
			player.getStatus().setVarC(SELECTING_QUICK_PRAYER, 0);
			player.getStatus().setVarC(QUICK_PRAYER_ENABLED, 0);
			break;
		default:
			break;
		}
	}

	public static final class FinishSelection extends ChildInterface {

		public FinishSelection() {
			super(43);
			addOption(1, PRAYER_HANDLER, false);
		}
	}

	public static final class Prayer extends ChildInterface {

		public Prayer() {
			super(8);
			addOption(1, PRAYER_HANDLER, false);
			setSettings(271);
		}

		@Override
		public int size() {
			return PRAYER_COUNT;
		}

	}

	public static final class QuickPrayer extends ChildInterface {

		public QuickPrayer() {
			super(42);
			addOption(1, PRAYER_HANDLER, false);
			setSettings(271);
		}

		@Override
		public int size() {
			return PRAYER_COUNT;
		}
	}

	public static final class PrayerHandler implements ClickActionHandler {

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			//TODO: F2P/P2P, Activation sounds, prayer left
			switch (action.getChild()) {
			case 8:// Prayer
			case 42:// Quick Prayer
				toggle(player, action.getSlot(), action.getChild() == 8 ? PRAYER : QUICK_PRAYER);
				break;
			case 43:// Finish quick prayer selection
				player.getStatus().setVarC(SELECTING_QUICK_PRAYER, 0);
				break;
			default:
				break;
			}
		}
	}

	private static final int PRAYER_COUNT = 27;
	private static final int[] EXCLUDES = new int[PRAYER_COUNT];
	public static final int[] PRAYER_LOOKUP_TABLE = new int[PRAYER_COUNT];

	private static void toggle(Player player, int slot, int varId) {
		slot = PRAYER_LOOKUP_TABLE[slot];
		Status status = player.getStatus();
		int var = status.getWaitingVar(varId);
		if ((var & (1 << slot)) != 0) {
			status.setVar(varId, var & ~(1 << slot));
		} else {
			var &= ~EXCLUDES[slot];
			status.setVar(varId, var | (1 << slot));
		}
	}

	public static final int THICK_SKIN = 0, ROCK_SKIN = 3, STEEL_SKIN = 9;
	public static final int RAPID_RESTORE = 6, RAPID_HEAL = 7;
	public static final int PROTECT_ITEM = 8;
	public static final int BURST_OF_STRENGTH = 1, SUPERHUMAN_STRENGTH = 4, ULTIMATE_STRENGTH = 10;
	public static final int CLARITY_OF_THOUGHT = 2, IMPROVED_REFLEXES = 5, INCREDIBLE_REFLEXES = 11;
	public static final int PROTECT_FROM_MAGIC = 12, PROTECT_FROM_RANGED = 13, PROTECT_FROM_MELEE = 14;
	public static final int RETRIBUTION = 15;
	public static final int REDEMPTION = 16;
	public static final int SMITE = 17;
	public static final int SHARP_EYE = 18;
	public static final int MYSTIC_WILL = 19;
	public static final int HAWK_EYE = 20;
	public static final int MYSTIC_LORE = 21;
	public static final int EAGLE_EYE = 22;
	public static final int MYSTIC_MIGHT = 23;
	public static final int PROTECT_FROM_SUMMONING = 24;
	public static final int CHIVALRY = 25;
	public static final int PIETY = 26;

	public static final PrayerTab INSTANCE = new PrayerTab();

	static {
		for (int i = 0; i < PRAYER_LOOKUP_TABLE.length; i++) {
			PRAYER_LOOKUP_TABLE[i] = i;
		}
		PRAYER_LOOKUP_TABLE[5] = ROCK_SKIN;
		PRAYER_LOOKUP_TABLE[6] = SUPERHUMAN_STRENGTH;
		PRAYER_LOOKUP_TABLE[7] = IMPROVED_REFLEXES;
		PRAYER_LOOKUP_TABLE[8] = 6;
		PRAYER_LOOKUP_TABLE[9] = 7;
		PRAYER_LOOKUP_TABLE[10] = 8;
		PRAYER_LOOKUP_TABLE[13] = STEEL_SKIN;
		PRAYER_LOOKUP_TABLE[14] = ULTIMATE_STRENGTH;
		PRAYER_LOOKUP_TABLE[15] = INCREDIBLE_REFLEXES;
		PRAYER_LOOKUP_TABLE[17] = PROTECT_FROM_MAGIC;
		PRAYER_LOOKUP_TABLE[18] = PROTECT_FROM_RANGED;
		PRAYER_LOOKUP_TABLE[19] = PROTECT_FROM_MELEE;
		PRAYER_LOOKUP_TABLE[22] = RETRIBUTION;
		PRAYER_LOOKUP_TABLE[23] = REDEMPTION;
		PRAYER_LOOKUP_TABLE[24] = SMITE;
		PRAYER_LOOKUP_TABLE[3] = SHARP_EYE;
		PRAYER_LOOKUP_TABLE[4] = MYSTIC_WILL;
		PRAYER_LOOKUP_TABLE[11] = HAWK_EYE;
		PRAYER_LOOKUP_TABLE[12] = MYSTIC_LORE;
		PRAYER_LOOKUP_TABLE[20] = EAGLE_EYE;
		PRAYER_LOOKUP_TABLE[21] = MYSTIC_MIGHT;
		PRAYER_LOOKUP_TABLE[16] = PROTECT_FROM_SUMMONING;

		int protPray = 1 << PROTECT_FROM_MAGIC | 1 << PROTECT_FROM_MELEE | 1 << PROTECT_FROM_RANGED
				| 1 << PROTECT_FROM_SUMMONING;
		int retrib = 1 << RETRIBUTION;
		int redempt = 1 << REDEMPTION;
		int smite = 1 << SMITE;
		int ohp = retrib | redempt | smite;

		int sEye = 1 << SHARP_EYE, hEye = 1 << HAWK_EYE, eEye = 1 << EAGLE_EYE;
		int ranged_prayers = sEye | hEye | eEye;

		int mLore = 1 << MYSTIC_LORE, mMight = 1 << MYSTIC_MIGHT, mWill = 1 << MYSTIC_WILL;
		int magic_prayers = mMight | mWill | mLore;

		int rSkin = 1 << ROCK_SKIN, tSkin = 1 << THICK_SKIN, sSkin = 1 << STEEL_SKIN;
		@SuppressWarnings("unused")
		int defPray = rSkin | tSkin | sSkin;

		int bStr = 1 << BURST_OF_STRENGTH, sStr = 1 << SUPERHUMAN_STRENGTH, uStr = 1 << ULTIMATE_STRENGTH;
		int str_prayers = bStr | sStr | uStr;

		int cThought = 1 << CLARITY_OF_THOUGHT, improvedReflexes = 1 << IMPROVED_REFLEXES,
				incredibleReflexes = 1 << INCREDIBLE_REFLEXES;
		int attPrayers = cThought | improvedReflexes | incredibleReflexes;

		EXCLUDES[THICK_SKIN] = rSkin | sSkin;
		EXCLUDES[ROCK_SKIN] = tSkin | sSkin;
		EXCLUDES[STEEL_SKIN] = rSkin | tSkin;

		EXCLUDES[CLARITY_OF_THOUGHT] = improvedReflexes | incredibleReflexes | magic_prayers | ranged_prayers;
		EXCLUDES[IMPROVED_REFLEXES] = cThought | incredibleReflexes | magic_prayers | ranged_prayers;
		EXCLUDES[INCREDIBLE_REFLEXES] = cThought | improvedReflexes | magic_prayers | ranged_prayers;

		EXCLUDES[BURST_OF_STRENGTH] = uStr | sStr | magic_prayers | ranged_prayers;
		EXCLUDES[SUPERHUMAN_STRENGTH] = bStr | uStr | magic_prayers | ranged_prayers;
		EXCLUDES[ULTIMATE_STRENGTH] = bStr | sStr | magic_prayers | ranged_prayers;

		EXCLUDES[SHARP_EYE] = hEye | eEye | magic_prayers | str_prayers | attPrayers;
		EXCLUDES[HAWK_EYE] = sEye | eEye | magic_prayers | str_prayers | attPrayers;
		EXCLUDES[EAGLE_EYE] = hEye | sEye | magic_prayers | str_prayers | attPrayers;

		EXCLUDES[MYSTIC_LORE] = mMight | mWill | ranged_prayers | str_prayers | attPrayers;
		EXCLUDES[MYSTIC_MIGHT] = mLore | mWill | ranged_prayers | str_prayers | attPrayers;
		EXCLUDES[MYSTIC_WILL] = mLore | mMight | ranged_prayers | str_prayers | attPrayers;

		EXCLUDES[PROTECT_FROM_MAGIC] = 1 << PROTECT_FROM_MELEE | 1 << PROTECT_FROM_RANGED | ohp;
		EXCLUDES[PROTECT_FROM_MELEE] = 1 << PROTECT_FROM_MAGIC | 1 << PROTECT_FROM_RANGED | ohp;
		EXCLUDES[PROTECT_FROM_RANGED] = 1 << PROTECT_FROM_MELEE | 1 << PROTECT_FROM_MAGIC | ohp;

		EXCLUDES[SMITE] = protPray | retrib | redempt;
		EXCLUDES[RETRIBUTION] = protPray | smite | redempt;
		EXCLUDES[REDEMPTION] = protPray | smite | retrib;

		EXCLUDES[CHIVALRY] = 1 << PIETY;
		EXCLUDES[PIETY] = 1 << CHIVALRY;
	}
}
