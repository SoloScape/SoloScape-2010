package net.scapeemulator.game.update.player.descr;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.Descriptor;
import net.scapeemulator.game.update.player.descr.external.ExternalDescriptor;

public final class RemovePlayerDescriptor extends Descriptor<PlayerUpdateMessage> {
	private ExternalDescriptor descriptor;

	public RemovePlayerDescriptor(ExternalDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBit(true);
		builder.putBit(false);
		builder.putBits(2, 0);
		builder.putBit(descriptor != null);
		if (descriptor != null) {
			descriptor.encodeDescriptor(message, builder, blockBuilder);
		}
	}
}
