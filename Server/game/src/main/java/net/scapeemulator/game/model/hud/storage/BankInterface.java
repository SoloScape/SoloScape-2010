package net.scapeemulator.game.model.hud.storage;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.action.InventoryActionHandler;
import net.scapeemulator.game.model.hud.interf.action.impl.ExamineAction;
import net.scapeemulator.game.model.hud.interf.child.InventoryChild;
import net.scapeemulator.game.model.hud.interf.child.SettingChild;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;

public class BankInterface extends Interface {
	public static final Interface INSTANCE = new BankInterface();
	public static final Interface INVENTORY = new BankInventory();
	// 27 inv
	// 29 eq
	// 31 bob

	public BankInterface() {
		super(762, InterfacePersistence.TRANSIENT);
		addChild(new SettingChild(15, "bank_insert"));
		addChild(new SettingChild(19, "bank_withdraw_note"));
		addChild(new BankContainer());
		addChild(new DepositInventoryChild(27, Inventory.BACKPACK));
		addChild(new DepositInventoryChild(29, Inventory.EQUIPMENT));
		/* addChild(new DepositChild(31, 93)); */ // TODO: Find bob invId
	}

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case OPEN_INTER:
			player.getInventories().get(Inventory.BANK).refresh();
			player.getDisplay().open(INVENTORY, true);
			// TODO: varP, varC
			break;
		default:
			break;
		}
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return mode.getCenterRoot();// ???
	}

	private static final class BankContainer extends InventoryChild {

		// TODO: Dragallowance external childs

		public BankContainer() {
			super(87, Inventory.getDetails(Inventory.BANK));
			addAction(1, new BankAction(1));
			addAction(2, new BankAction(5));
			addAction(3, new BankAction(10));
			addAction(4, new BankAction(BankAction.PREV_X));
			addAction(5, new BankAction(BankAction.X));
			addAction(6, new BankAction(BankAction.ALL));
			addAction(7, new BankAction(BankAction.ALL_BUT_ONE));
			addAction(10, new ExamineAction());// Examine action, design
			setSettings(762);
		}

		public boolean insert(Player player) {
			return (boolean) player.getStatus().getStatus("bank_insert");
		}
	}

	private static class BankAction implements InventoryActionHandler {
		public static final int PREV_X = -1;
		public static final int X = -2;
		public static final int ALL = -3;
		public static final int ALL_BUT_ONE = -4;
		private final int amount;

		public BankAction(int amount) {
			this.amount = amount;
		}

		@Override
		public void handle(Player player, int slot, Item item, Inventory source) {
			Inventory target = player.getInventories().get(Inventory.BACKPACK);
			int amount = 0;
			switch (this.amount) {
			case PREV_X:
				amount = (int) player.getStatus().getStatus("bank_x");
				break;
			case X:
				break;
			case ALL:
				amount = item.getAmount();
				break;
			case ALL_BUT_ONE:
				amount = item.getAmount() - 1;
				break;
			default:
				amount = this.amount;
				break;
			}
			player.sendMessage("[dank] removing " + amount + " of " + item.getId());
			// TODO: Withdraw note
			Item removed = source.remove(new Item(item.getId(), amount));
			Item remaining = target.add(removed);
			if (remaining != null)
				source.add(remaining);
			source.shift();
		}
	}
}
