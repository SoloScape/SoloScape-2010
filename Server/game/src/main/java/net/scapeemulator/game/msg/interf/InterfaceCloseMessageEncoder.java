package net.scapeemulator.game.msg.interf;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

public final class InterfaceCloseMessageEncoder extends MessageEncoder<InterfaceCloseMessage> {

	public InterfaceCloseMessageEncoder() {
		super(InterfaceCloseMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, InterfaceCloseMessage message) {
		PacketBuilder builder = new PacketBuilder(alloc, 44);
		builder.put(DataType.SHORT, message.getSequenceNumber());
		builder.put(DataType.INT, message.getPointer());
		return builder.toPacket();
	}
}
