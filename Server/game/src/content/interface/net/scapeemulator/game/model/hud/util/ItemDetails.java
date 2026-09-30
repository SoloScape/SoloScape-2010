package net.scapeemulator.game.model.hud.util;

import net.scapeemulator.game.model.hud.interf.BlockingTab;
import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.shop.Shop;
import net.scapeemulator.game.model.shop.ShopInventory;

public class ItemDetails extends BlockingTab {
	public static final Interface INSTANCE = new ItemDetails();

	public ItemDetails() {
		super(449, InterfacePersistence.TRANSIENT);
		addChild(new CloseButton());
		addChild(new TakeButton());
	}

	public static class TakeButton extends ChildInterface implements ClickActionHandler {

		public TakeButton() {
			super(21);
			addOption(1, this, false);
			addOption(2, this, false);
			addOption(3, this, false);
			addOption(4, this, false);
			// addOption 1 1
			// addOption 2 5
			// addOption 3 10
			// addOption 4 50
		}

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			int option = action.getOption();
			int count = 1;
			int itemId = player.getStatus().getVarC(741);
			int currency = player.getStatus().getVarC(743);
			int price = player.getStatus().getVarC(744);
			switch (option) {
			case 2:
				count = 5;
				break;
			case 3:
				count = 10;
				break;
			case 4:
				count = 50;
				break;
			}
			if (price == -1 && currency == 0) {
				int shopInv = player.getStatus().getWaitingVar(1496);
				if (shopInv != -1) {
					Inventory freebieInventory = player.getInventories().get(shopInv);
					if (freebieInventory == null)
						return;
					int itemSlot = freebieInventory.slotOf(itemId);
					if (itemSlot != -1) {
						Item item = freebieInventory.get(itemSlot);
						if(item.getAmount() == 0) {
							//message
							return;
						}
						Inventory pInv = player.getInventories().get(Inventory.BACKPACK);
						int amount = count;
						if (amount > item.getAmount()) {
							amount = item.getAmount();
						}
						Item result = pInv.add(new Item(item.getId(), amount));
						int remove = result == null ? 0 : result.getAmount();
						result = new Item(item.getId(), amount - remove);
						freebieInventory.remove(result);
						if (freebieInventory.get(itemSlot).getAmount() == 0) {
							// ?? Message that you have claimed all free items?
						}
						player.sendMessage("Claim item(" + item + ") from Inventory #" + shopInv + ": " + amount);
					}
				}
			} else {
				if (price == 0) {
					// Item is not currently available.
					// N/A, message?
				} else {
					// pay mf
					int shopId = player.getStatus().getWaitingVar(118);
					Shop shop = Shop.getShop(shopId);
					if (shop != null) {
						int slot = shop.getInventory().slotOf(itemId);
						if (slot != -1) {
							shop.buy(player, slot, count);
						}
					}
				}
			}
			// int itemSlot = action.getSlot() / 6;
			// int shopInv = player.getStatus().getWaitingVar(invVarId);
			// Inventory inventory = player.getInventories().get(shopInv);
			// if (inventory != null) {
			// Item item = inventory.get(itemSlot);
			// if (item != null && item.getAmount() != 0) {
			// shop.buy(player, amount, invVarId);
			// }
			// }
		}
	}

	public static class CloseButton extends ChildInterface implements ClickActionHandler {

		public CloseButton() {
			super(1);
			addOption(1, this, false);
		}

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			player.getDisplay().close(INSTANCE);
			int shopId = player.getStatus().getWaitingVar(118);
			if (shopId != -1) {
				Shop shop = Shop.getShop(shopId);
				if (shop != null)
					player.getDisplay().open(new ShopInventory(shop), true);
			}
		}
	}

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case CLOSE_INTER:
			player.getStatus().setVarC(741, -1);
			player.getStatus().setVarC(743, -1);
			player.getStatus().setVarC(744, 0);
			break;
		case OPEN_INTER:// If not shopping, setVarC currency and cost to none
			break;
		default:
			break;
		}
	}
}
