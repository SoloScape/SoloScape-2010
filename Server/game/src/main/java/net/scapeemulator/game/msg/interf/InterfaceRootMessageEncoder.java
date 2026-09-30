package net.scapeemulator.game.msg.interf;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.*;
import net.scapeemulator.api.net.packet.Packet.Type;

public final class InterfaceRootMessageEncoder extends MessageEncoder<InterfaceRootMessage> {

	public InterfaceRootMessageEncoder() {
		super(InterfaceRootMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, InterfaceRootMessage message) {
		PacketBuilder builder = new PacketBuilder(alloc, 102, Type.FIXED);
		builder.put(DataType.BYTE, message.reset() ? 2 : 0);
		builder.put(DataType.SHORT, DataOrder.LITTLE, message.getSequenceNumber());
		builder.put(DataType.SHORT, DataOrder.LITTLE, message.getId());
		return builder.toPacket();
	}
}
