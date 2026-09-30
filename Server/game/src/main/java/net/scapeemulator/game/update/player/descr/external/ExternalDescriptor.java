package net.scapeemulator.game.update.player.descr.external;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.Descriptor;

public abstract class ExternalDescriptor extends Descriptor<PlayerUpdateMessage> {

	public static ExternalDescriptor create(Player player) {
		if (player == null)
			return null;
		Position start = player.getStartStep();
		Position pos = player.getPosition();
		if (!player.isListed()) {
			pos = new Position(0, 0, 0);
		}
		boolean heightChanged = false;
		if (start.getHeight() != pos.getHeight()) {
			heightChanged = true;
		}
		int xDiff = pos.getAreaX() - start.getAreaX();
		int yDiff = pos.getAreaY() - start.getAreaY();
		if (!heightChanged && xDiff == 0 && yDiff == 0)
			return null;
		else {
			if (xDiff == 0 && yDiff == 0) {
				return new HeightChangeDescriptor(player);
			} else {
				boolean xMovement = true;
				boolean yMovement = true;
				if (xDiff < -1 || xDiff > 1) {
					xMovement = false;
				}
				if (yDiff < -1 || yDiff > 1) {
					xMovement = false;
				}
				if (xMovement && yMovement) {
					return new ExternalMovementDescriptor(player, xDiff, yDiff);
				} else {
					return new ExternalTeleportDescriptor(start, pos);
				}
			}
		}
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBit(true);// Update
		encodeDescriptor(message, builder, blockBuilder);
	}

	public abstract void encodeDescriptor(PlayerUpdateMessage message, PacketBuilder builder,
			PacketBuilder blockBuilder);
}
