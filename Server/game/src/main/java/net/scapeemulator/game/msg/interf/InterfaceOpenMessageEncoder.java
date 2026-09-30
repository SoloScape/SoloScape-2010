package net.scapeemulator.game.msg.interf;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.*;

public final class InterfaceOpenMessageEncoder extends MessageEncoder<InterfaceOpenMessage> {

	public InterfaceOpenMessageEncoder() {
		super(InterfaceOpenMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, InterfaceOpenMessage message) {
		PacketBuilder builder = new PacketBuilder(alloc, 22);
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, message.getInter().getPersistence().ordinal());
		builder.put(DataType.SHORT, DataTransformation.ADD, message.getSequenceNumber());
		builder.put(DataType.INT, message.getPointer());
		builder.put(DataType.SHORT, DataOrder.LITTLE, message.getInter().getId());
		return builder.toPacket();
	}
}
