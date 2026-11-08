package net.scapeemulator.game.model.shop;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.action.InventoryActionHandler;
import net.scapeemulator.game.model.hud.interf.action.impl.ExamineAction;
import net.scapeemulator.game.model.hud.interf.child.InventoryChild;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.util.ScriptMessage;

public class ShopInventory extends Interface {
	private Shop shop;

	public ShopInventory(Shop shop) {
		super(621, InterfacePersistence.TRANSIENT);
		this.shop = shop;
		addChild(new ShopInventoryChild(this));
	}

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case OPEN_INTER:
			player.send(new ScriptMessage(150, "IviiiIsssssssss", 621 << 16, 93, 4, 7, 0, -1, "Value", "Sell 1",
					"Sell 5", "Sell 10", "Sell 50", "Sell 500", "Sell X", "", ""));
			break;
		default:
			break;
		}
	}

	public static final class ShopInventoryChild extends InventoryChild {

		public ShopInventoryChild(ShopInventory shopInventory) {
			super(0, Inventory.getDetails(Inventory.BACKPACK));
			for (int option = 1; option < 8; option++)
				addAction(option, new ActionHandler(option, shopInventory));
			addAction(10, new ExamineAction());
			setSettings(621);
		}
	}

	public static final class ActionHandler implements InventoryActionHandler {
		private final int option;
		private final Shop shop;

		public ActionHandler(int option, ShopInventory shopInventory) {
			this.option = option;
			this.shop = shopInventory.shop;
		}

		@Override
		public void handle(Player player, int slot, Item item, Inventory source) {
			int amount = 1;
			switch (option) {
			case 1:
				// value
				amount = 0;
				break;
			case 3:
				amount = 5;
				break;
			case 4:
				amount = 10;
				break;
			case 5:
				amount = 50;
				break;
			case 6:
				amount = 500;
				break;
			case 7:
				// TODO: RequestInputX
				return;
			default:
				break;
			}
			shop.sell(player, slot, amount);
		}
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return mode.getBlockingTabRoot();
	}
}
