package net.scapeemulator.game.msg.interf;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.game.model.entity.Position;

public final class WorldMapClickMessage implements Message {
	private final int packedPosition;

	public WorldMapClickMessage(int packedPosition) {
		this.packedPosition = packedPosition;
	}

	public boolean isValid() {
		return (packedPosition & 0xC0000000) == 0;
	}

	public Position getPosition() {
		return new Position((packedPosition >>> 14) & 0x3FFF, packedPosition & 0x3FFF,
				(packedPosition >>> 28) & 3);
	}
}
