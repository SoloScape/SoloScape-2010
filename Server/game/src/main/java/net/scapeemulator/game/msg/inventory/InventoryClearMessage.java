package net.scapeemulator.game.msg.inventory;

import net.scapeemulator.api.message.Message;

public final class InventoryClearMessage implements Message {
	private final int id, slot, type;

	public InventoryClearMessage(int id, int slot, int type) {
		this.id = id;
		this.slot = slot;
		this.type = type;
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
}
