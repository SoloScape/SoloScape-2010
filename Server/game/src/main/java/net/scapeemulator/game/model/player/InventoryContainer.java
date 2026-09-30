package net.scapeemulator.game.model.player;

import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.InventoryInfo;

public class InventoryContainer {
	private final Map<Integer, Inventory> inventories = new HashMap<>();

	public InventoryContainer(Player player) {
		for (InventoryInfo info : Inventory.getDetails()) {
			register(player, info);
		}
	}

	public Inventory get(int inventoryId) {
		return inventories.get(inventoryId);
	}

	private void register(Player player, InventoryInfo info) {
		inventories.put(info.getInventoryId(), new Inventory(player, info));
	}

	public void addInventory(int id, Inventory inventory) {
		inventories.put(id, inventory);
	}

	public void removeInventory(int id) {
		inventories.remove(id);
	}
}
