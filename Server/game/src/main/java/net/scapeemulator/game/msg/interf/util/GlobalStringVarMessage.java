package net.scapeemulator.game.msg.interf.util;

import net.scapeemulator.game.msg.CachedMessage;

public final class GlobalStringVarMessage extends CachedMessage {
	private final int id;
	private final String value;

	public GlobalStringVarMessage(int id, String value) {
		this.id = id;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

}
