package net.scapeemulator.game.msg.inventory;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.game.model.inventory.SlottedItem;

public final class InventorySlottedItemMessage implements Message {

	private final int id, slot, type;
	private final SlottedItem[] items;

	public InventorySlottedItemMessage(int id, int slot, int type, SlottedItem[] items) {
		this.id = id;
		this.slot = slot;
		this.type = type;
		this.items = items;
	}

	public int getId() {
		return id;
	}

	public int getSlot() {
		return slot;
	}

	public int getType() {
		return type;
	}

	public SlottedItem[] getItems() {
		return items;
	}

}
