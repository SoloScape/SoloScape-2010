package net.scapeemulator.game.msg.entity;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.update.Descriptor;

import java.io.IOException;

public final class PlayerUpdateMessageEncoder extends MessageEncoder<PlayerUpdateMessage> {

	public PlayerUpdateMessageEncoder() {
		super(PlayerUpdateMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, PlayerUpdateMessage message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 97, Packet.Type.VARIABLE_SHORT);
		PacketBuilder blockBuilder = new PacketBuilder(alloc);
		for (Descriptor<PlayerUpdateMessage> descriptor : message.getDescriptors())
			descriptor.encode(message, builder, blockBuilder);
		builder.putRawBuilder(blockBuilder);
		return builder.toPacket();
	}
}
