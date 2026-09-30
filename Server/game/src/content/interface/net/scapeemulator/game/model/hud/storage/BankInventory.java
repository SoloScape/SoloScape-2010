package net.scapeemulator.game.model.hud.storage;

import net.scapeemulator.game.model.hud.interf.BlockingTab;
import net.scapeemulator.game.model.hud.interf.action.InventoryActionHandler;
import net.scapeemulator.game.model.hud.interf.action.impl.ExamineAction;
import net.scapeemulator.game.model.hud.interf.child.InventoryChild;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;

public class BankInventory extends BlockingTab {

	public BankInventory() {
		super(763, InterfacePersistence.TRANSIENT);
		addChild(new BankInventoryContainer());
	}

	private static final class BankInventoryContainer extends InventoryChild {

		public BankInventoryContainer() {
			super(0, Inventory.getDetails(93));
			addAction(1, new BankInventoryAction(1));
			addAction(2, new BankInventoryAction(5));
			addAction(3, new BankInventoryAction(10));
			addAction(4, new BankInventoryAction(BankInventoryAction.PREV_X));
			addAction(5, new BankInventoryAction(BankInventoryAction.X));
			addAction(6, new BankInventoryAction(BankInventoryAction.ALL));
			addAction(10, new ExamineAction());// Examine action, finnish design
			setSettings(763);
		}
	}

	public static final class BankInventoryAction implements InventoryActionHandler {
		public static final int PREV_X = -3;
		public static final int X = -2;
		public static final int ALL = -1;
		private int amount;

		public BankInventoryAction(int amount) {
			this.amount = amount;
		}

		@Override
		public void handle(Player player, int slot, Item item, Inventory source) {
			Inventory target = player.getInventories().get(Inventory.BANK);
			int amount = 0;
			switch (this.amount) {
			case PREV_X:
				amount = (int) player.getStatus().getStatus("bank_x");
				break;
			case X:
				// TODO: RequestInput
				return;
			case ALL:
				amount = Integer.MAX_VALUE;
				break;
			default:
				amount = this.amount;
				break;
			}
			Item removed = source.remove(new Item(item.getId(), amount));
			Item remaining = target.add(removed);
			if (remaining != null)
				source.add(remaining);

		}
	}
}
