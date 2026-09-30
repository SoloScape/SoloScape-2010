package net.scapeemulator.game.update.player.descr.external;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerUpdater;

public class ExternalMovementDescriptor extends ExternalDescriptor {
	private int toEncode;

	public ExternalMovementDescriptor(Player player, int xDiff, int yDiff) {
		int heightDiff = PlayerUpdater.translate(player.getStartStep().getHeight(),
				player.getPosition().getHeight(), 4);
		int direction = 0;
		if (yDiff == -1) {
			direction = 1 + xDiff;
		} else if (yDiff == 0) {
			direction = xDiff == -1 ? 3 : 4;
		} else if (yDiff == 1) {
			direction = 6 + xDiff;
		}
		toEncode = (heightDiff << 3) | direction;
	}

	@Override
	public void encodeDescriptor(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBits(2, 2);
		builder.putBits(5, toEncode);
	}
}
