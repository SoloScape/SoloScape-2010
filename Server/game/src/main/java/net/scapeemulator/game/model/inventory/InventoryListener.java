package net.scapeemulator.game.model.inventory;

import net.scapeemulator.game.model.player.Player;

public interface InventoryListener {

	public void itemChanged(Player player, Inventory inventory, int slot, Item item);

	public void itemsChanged(Player player, Inventory inventory);

	public void capacityExceeded(Player player, Inventory inventory);

}
