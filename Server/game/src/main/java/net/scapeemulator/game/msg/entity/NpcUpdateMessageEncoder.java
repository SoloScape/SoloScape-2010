package net.scapeemulator.game.msg.entity;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.update.npc.NpcDescriptor;

import java.io.IOException;

public final class NpcUpdateMessageEncoder extends MessageEncoder<NpcUpdateMessage> {

	public NpcUpdateMessageEncoder() {
		super(NpcUpdateMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, NpcUpdateMessage message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 77, Packet.Type.VARIABLE_SHORT);
		PacketBuilder blockBuilder = new PacketBuilder(alloc);
		builder.switchToBitAccess();

		builder.putBits(8, message.getLocalNpcCount());

		for (NpcDescriptor descriptor : message.getDescriptors())
			descriptor.encode(message, builder, blockBuilder);

		if (blockBuilder.getLength() > 0) {
			builder.putBits(15, 32767);
			builder.switchToByteAccess();
			builder.putRawBuilder(blockBuilder);
		} else {
			builder.switchToByteAccess();
		}

		return builder.toPacket();
	}

}
