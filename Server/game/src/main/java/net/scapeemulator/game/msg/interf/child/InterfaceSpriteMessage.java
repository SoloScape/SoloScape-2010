package net.scapeemulator.game.msg.interf.child;

import net.scapeemulator.game.msg.CachedMessage;

public class InterfaceSpriteMessage extends CachedMessage {
	private final int spriteId, pointer;

	public InterfaceSpriteMessage(int interfaceId, int childId) {
		this(-1, interfaceId, childId);
	}

	public InterfaceSpriteMessage(int spriteId, int interfaceId, int childId) {
		this.spriteId = spriteId;
		this.pointer = interfaceId << 16 | childId;
	}

	public int getPointer() {
		return pointer;
	}

	public int getSpriteId() {
		return spriteId;
	}
}
