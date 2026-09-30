package net.scapeemulator.game.update.player.descr.external;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerUpdater;

public class ExternalTeleportDescriptor extends ExternalDescriptor {
	private final int toEncode;

	public ExternalTeleportDescriptor(Position start, Position pos) {
		int heightDiff = PlayerUpdater.translate(start.getHeight(), pos.getHeight(), 4);
		int xDiff = PlayerUpdater.translate(start.getAreaX(), pos.getAreaX(), 256);
		int yDiff = PlayerUpdater.translate(start.getAreaY(), pos.getAreaY(), 256);
		toEncode = heightDiff << 16 | xDiff << 8 | yDiff;
	}

	@Override
	public void encodeDescriptor(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBits(2, 3);
		builder.putBits(18, toEncode);
	}
}
