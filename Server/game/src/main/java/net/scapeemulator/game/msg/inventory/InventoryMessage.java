package net.scapeemulator.game.msg.inventory;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.game.model.inventory.Item;

public final class InventoryMessage implements Message {

	private final int id, slot, type;
	private final Item[] items;

	public InventoryMessage(int id, int slot, int type, Item[] items) {
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

	public Item[] getItems() {
		return items;
	}

}
