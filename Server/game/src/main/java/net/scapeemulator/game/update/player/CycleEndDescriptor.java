package net.scapeemulator.game.update.player;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.Descriptor;

public class CycleEndDescriptor extends Descriptor<PlayerUpdateMessage> {

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.switchToByteAccess();
	}
}
