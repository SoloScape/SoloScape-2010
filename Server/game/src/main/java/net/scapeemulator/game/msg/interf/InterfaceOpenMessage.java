package net.scapeemulator.game.msg.interf;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.msg.CachedMessage;

public final class InterfaceOpenMessage extends CachedMessage {
	private final int pointer;
	private final Interface inter;

	public InterfaceOpenMessage(int pointer, Interface inter) {
		this.pointer = pointer;
		this.inter = inter;
	}

	public int getPointer() {
		return pointer;
	}

	public Interface getInter() {
		return inter;
	}
}
