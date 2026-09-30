package net.scapeemulator.game.msg.interf.util;

import net.scapeemulator.game.msg.CachedMessage;

public final class GlobalIntVarMessage extends CachedMessage {

	private final int id, value;

	public GlobalIntVarMessage(int id, int value) {
		this.id = id;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public int getValue() {
		return value;
	}

}
