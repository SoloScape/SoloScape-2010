package net.scapeemulator.game.model.hud;

import java.util.Arrays;

import net.scapeemulator.game.cache.GameConstants;
import net.scapeemulator.game.model.constants.GenericValue;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.interf.action.InventoryActionHandler;
import net.scapeemulator.game.model.hud.interf.action.impl.ExamineAction;
import net.scapeemulator.game.model.hud.interf.child.InventoryChild;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Equipment;
import net.scapeemulator.game.model.player.Player;

public class InventoryTab extends Tab {
	public static final Interface INSTANCE = new InventoryTab();

	public InventoryTab() {
		super(149, Tab.INVENTORY);
		addChild(new Backpack());
	}

	public static final class Backpack extends InventoryChild {

		public Backpack() {
			super(0, Inventory.getDetails(Inventory.BACKPACK));
			for (int i = 1; i < 10; i++) {
				addAction(i, new OptionAction(i));
			}
			addAction(8, new DropAction());
			addAction(10, ExamineAction.ACTION);
			setSettings(invDetails.getInterId());
		}

		public static final class DebugHandler implements InventoryActionHandler {
			private int opt;

			public DebugHandler(int opt) {
				this.opt = opt;
			}

			@Override
			public void handle(Player player, int slot, Item item, Inventory source) {
				player.sendMessage("[Item click, Option " + opt + "] " + item.getDefinition().getName() + ".", 99);
			}
		}

		public static final class OptionAction implements InventoryActionHandler {
			private int opt;

			public OptionAction(int opt) {
				this.opt = opt;
			}

			@Override
			public void handle(Player player, int slot, Item item, Inventory source) {
				if (opt == 2) {// TODO: Generize this
					String option = item.getDefinition().getInventoryOptions()[1];
					if (option != null) {
						if ((option.equalsIgnoreCase("wear") || option.equalsIgnoreCase("wield"))) {
							if (meetConditions(player, item)) {
								Equipment.equip(player, slot);
							}
						} else {
							player.sendMessage(
									"[Non-equip Option 2: " + item + "] " + item.getDefinition().getName() + ".", 99);
						}
					} else {
						player.sendMessage(Arrays.toString(item.getDefinition().getInventoryOptions()));
					}
				} else {
					player.sendMessage("[Option " + opt + ": " + item + "] " + item.getDefinition().getName() + ".",
							99);
				}
			}

			private boolean meetConditions(Player player, Item item) {
				// TODO: More than 2 requirements? Doubt it but is there?
				boolean meet = true;
				String requirement = "You need a ", previous = "";
				int ix = 0;
				for (; ix < GenericValue.EQUIPMENT_SKILL_REQUIREMENTS.length; ix++) {
					int reqId = GenericValue.EQUIPMENT_SKILL_REQUIREMENTS[ix];
					int skillId = item.getDefinition().getGeneric(reqId, -1);
					int level = item.getDefinition().getGeneric(reqId + 1, 1);
					if (skillId == -1) {
						break;
					}
					requirement += previous;
					previous = GameConstants.getSkillName(skillId) + " level of " + level;
					if (ix > 0)
						requirement += ", ";
					if (player.getSkillSet().getMaximumLevel(skillId) < level) {
						meet = false;
					}
				}
				if (meet)
					return true;
				if (ix > 1) {
					requirement += " and a ";
				}
				requirement += previous;
				requirement += " to " + item.getDefinition().getInventoryOptions()[1] + " this item.";
				player.sendMessage(requirement);
				return false;
			}
		}

		public static final class DropAction implements InventoryActionHandler {

			@Override
			public void handle(Player player, int slot, Item item, Inventory source) {
				player.sendMessage("[Drop " + item + "] " + item.getDefinition().getName() + ".", 99);
			}
		}
	}
}
