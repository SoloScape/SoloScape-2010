package net.scapeemulator.game.msg.interf;

import net.scapeemulator.game.msg.CachedMessage;

public final class InterfaceRootMessage extends CachedMessage {
	private final int id;
	private final boolean reset;

	public InterfaceRootMessage(int id) {
		this.id = id;
		reset = true;
	}

	public InterfaceRootMessage(int id, boolean reset) {
		this.id = id;
		this.reset = reset;
	}

	public int getId() {
		return id;
	}

	public boolean reset() {
		return reset;
	}
}
