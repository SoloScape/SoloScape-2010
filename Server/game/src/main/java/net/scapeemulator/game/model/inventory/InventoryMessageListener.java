package net.scapeemulator.game.model.inventory;

import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.inventory.InventoryClearMessage;
import net.scapeemulator.game.msg.inventory.InventoryMessage;
import net.scapeemulator.game.msg.inventory.InventorySlottedItemMessage;

public final class InventoryMessageListener implements InventoryListener {
	private final int id, child, type;

	public InventoryMessageListener(int id, int slot, int type) {
		this.id = id;
		this.child = slot;
		this.type = type;
	}

	@Override
	public void itemChanged(Player player, Inventory inventory, int slot, Item item) {
		if (player != null) {
			SlottedItem[] items = new SlottedItem[] { new SlottedItem(slot, item) };
			player.send(new InventorySlottedItemMessage(id, child, type, items));
		}
	}

	@Override
	public void itemsChanged(Player player, Inventory inventory) {
		if (player != null) {
			if (inventory.isEmpty()) {
				player.send(new InventoryClearMessage(id, child, type));
			} else {
				Item[] items = inventory.toArray();
				player.send(new InventoryMessage(id, child, type, items));
			}
		}
	}

	@Override
	public void capacityExceeded(Player player, Inventory inventory) {
		/* empty */
	}
}
