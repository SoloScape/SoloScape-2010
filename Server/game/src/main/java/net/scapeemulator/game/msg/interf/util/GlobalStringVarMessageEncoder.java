package net.scapeemulator.game.msg.interf.util;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.*;
import net.scapeemulator.api.net.packet.Packet.Type;

import java.io.IOException;

public final class GlobalStringVarMessageEncoder extends MessageEncoder<GlobalStringVarMessage> {

	public GlobalStringVarMessageEncoder() {
		super(GlobalStringVarMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, GlobalStringVarMessage message) throws IOException {
		PacketBuilder builder;
		// The 'difference' between those two packets is the size graham.
		if (message.getValue().length() >= 100) {
			builder = new PacketBuilder(alloc, 54, Type.VARIABLE_SHORT);
			builder.put(DataType.SHORT, DataTransformation.ADD, message.getId());
			builder.putString(message.getValue());
			builder.put(DataType.SHORT, message.getSequenceNumber());
		} else {
			builder = new PacketBuilder(alloc, 53, Type.VARIABLE_BYTE);
			builder.put(DataType.SHORT, DataTransformation.ADD, message.getSequenceNumber());
			builder.putString(message.getValue());
			builder.put(DataType.SHORT, DataTransformation.ADD, message.getId());
		}
		return builder.toPacket();
	}
}
