package net.scapeemulator.game.msg.interf;

import net.scapeemulator.game.msg.CachedMessage;

public class InterfaceMoveMessage extends CachedMessage {
	private final int source, target;

	public InterfaceMoveMessage(int source, int target) {
		this.source = source;
		this.target = target;
	}

	public int getSource() {
		return source;
	}

	public int getTarget() {
		return target;
	}
}
