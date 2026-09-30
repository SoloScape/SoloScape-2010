package net.scapeemulator.game.update.player.descr.external;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerUpdater;

public class HeightChangeDescriptor extends ExternalDescriptor {
	private final int difference;

	public HeightChangeDescriptor(Player player) {
		difference = PlayerUpdater.translate(player.getStartStep().getHeight(), player.getPosition().getHeight(),
				4);
	}

	@Override
	public void encodeDescriptor(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBits(2, 1);
		builder.putBits(2, difference);
	}
}
