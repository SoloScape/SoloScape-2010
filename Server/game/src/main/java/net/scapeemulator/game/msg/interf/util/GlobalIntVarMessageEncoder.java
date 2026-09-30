package net.scapeemulator.game.msg.interf.util;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.*;

import java.io.IOException;

public final class GlobalIntVarMessageEncoder extends MessageEncoder<GlobalIntVarMessage> {

	public GlobalIntVarMessageEncoder() {
		super(GlobalIntVarMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, GlobalIntVarMessage message) throws IOException {
		int id = message.getId();
		int value = message.getValue();

		if (value >= -128 && value <= 127) {
			PacketBuilder builder = new PacketBuilder(alloc, 12);
			builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, message.getSequenceNumber());
			builder.put(DataType.SHORT, id);
			builder.put(DataType.BYTE, DataTransformation.SUBTRACT, value);
			return builder.toPacket();
		} else {
			PacketBuilder builder = new PacketBuilder(alloc, 52);
			builder.put(DataType.SHORT, DataTransformation.ADD, message.getSequenceNumber());
			builder.put(DataType.SHORT, DataOrder.LITTLE, id);
			builder.put(DataType.INT, DataOrder.LITTLE, value);
			return builder.toPacket();
		}
	}
}
