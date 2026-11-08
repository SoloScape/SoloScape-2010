package net.scapeemulator.game.command;

import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.shop.Shop;
import net.scapeemulator.game.model.shop.ShopInterface;
import net.scapeemulator.game.model.shop.ShopInventory;

public class ShopCommand extends CommandHandler {

	public ShopCommand() {
		super("shop");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		try {
			switch (arguments[0]) {
			case "open":
				int shopId = Integer.parseInt(arguments[1]);
				Shop shop = Shop.getShop(shopId);
				if (shop != null) {
					player.getStatus().setVar(1496, shop.getConfiguration().getFreebieInventoryId());
					player.getStatus().setVar(118, shop.getId());

					// TODO these belong in trigger for VarP 118!!
					player.getDisplay().open(new ShopInterface(shop), false);
					player.getDisplay().open(new ShopInventory(shop), true);
				}
				break;
			case "add":
			case "remove":
				int id, amount;
				id = Integer.parseInt(arguments[1]);
				amount = Integer.parseInt(arguments[2]);
				shopId = player.getStatus().getWaitingVar(118);
				shop = Shop.getShop(shopId);
				Item item = new Item(id, amount);
				if (shop != null) {
					Inventory inventory = player.getInventories().get(shopId);
					if (arguments[0].equalsIgnoreCase("add")) {
						inventory.add(item);
					} else {
						inventory.remove(item);
					}
				}
				break;
			default:
				break;
			}
			// TODO: Trigger for shopId
			// Stock
			//

		} catch (Exception e) {

		}
	}
}
