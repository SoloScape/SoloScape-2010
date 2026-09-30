package net.scapeemulator.game.msg.interf;

import net.scapeemulator.game.msg.CachedMessage;

public final class InterfaceCloseMessage extends CachedMessage {
	private final int pointer;

	public InterfaceCloseMessage(int pointer) {
		this.pointer = pointer;
	}

	public int getPointer() {
		return pointer;
	}
}
