package net.scapeemulator.game.model.inventory;

import net.scapeemulator.game.model.player.Player;

public final class InventoryAppearanceListener implements InventoryListener {

	@Override
	public void itemChanged(Player player, Inventory inventory, int slot, Item item) {
		player.sendMessage("item " + slot + " " + item);
		player.refreshAppearance();
	}

	@Override
	public void itemsChanged(Player player, Inventory inventory) {
		player.refreshAppearance();
	}

	@Override
	public void capacityExceeded(Player player, Inventory inventory) {
		/* empty */
	}
}
