package net.scapeemulator.game.model.hud.interf.child;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.type.ChildInterfaceType;

public class SpriteChild extends ChildInterface {
	private final int spriteId;

	public SpriteChild(int childId, int spriteId) {
		super(childId);
		this.spriteId = spriteId;
	}

	@Override
	public ChildInterfaceType getType() {
		return ChildInterfaceType.SPRITE;
	}

	@Override
	public String toString() {
		return "SpriteChild [spriteId=" + spriteId + "]";
	}

	public int getSpriteId() {
		return spriteId;
	}
}
