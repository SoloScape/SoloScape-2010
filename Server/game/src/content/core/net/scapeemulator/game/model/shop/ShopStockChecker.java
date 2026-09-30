package net.scapeemulator.game.model.shop;

import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.InventoryListener;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;

public class ShopStockChecker implements InventoryListener {
	private final Inventory stock;

	// TODO: This should be the appearance listener for shops to minimize calls
	public ShopStockChecker(Inventory stock) {
		this.stock = stock;
	}

	@Override
	public void itemChanged(Player player, Inventory inventory, int slot, Item item) {
		if (item != null) {
			if (player != null) {
				if (item.getAmount() == 0) {
					player.getStatus().setVarC(946 + slot, -1);
				}
				if (item.getId() == player.getStatus().getVarC(741)) {
					Shop shop = Shop.getShop(player.getStatus().getCurrentVar(118));
					if (shop != null) {
						player.getStatus().setVarC(744, shop.getBuyPrice(slot));
					}
				}
			}
		}
		// Eh?
		if (stock != null && stock.get(slot) == null && item.getAmount() == 0) {
			inventory.set(slot, null);
		}
	}

	@Override
	public void itemsChanged(Player player, Inventory inventory) {

	}

	@Override
	public void capacityExceeded(Player player, Inventory inventory) {
		// woo
	}
}
