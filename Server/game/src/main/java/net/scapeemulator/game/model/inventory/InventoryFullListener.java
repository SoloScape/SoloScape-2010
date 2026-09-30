package net.scapeemulator.game.model.inventory;

import net.scapeemulator.game.model.player.Player;

public final class InventoryFullListener implements InventoryListener {
	private final String name;

	public InventoryFullListener(String name) {
		this.name = name;
	}

	@Override
	public void itemChanged(Player player, Inventory inventory, int slot, Item item) {
		/* ignore */
	}

	@Override
	public void itemsChanged(Player player, Inventory inventory) {
		/* ignore */
	}

	@Override
	public void capacityExceeded(Player player, Inventory inventory) {
		if (player != null) {
			player.sendMessage("Not enough " + name + " space.");
		}
	}
}
