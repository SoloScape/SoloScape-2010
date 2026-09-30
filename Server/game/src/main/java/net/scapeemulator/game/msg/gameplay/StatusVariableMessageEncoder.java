package net.scapeemulator.game.msg.gameplay;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

import java.io.IOException;

public final class StatusVariableMessageEncoder extends MessageEncoder<StatusVariableMessage> {

	public StatusVariableMessageEncoder() {
		super(StatusVariableMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, StatusVariableMessage message) throws IOException {
		int id = message.getId();
		int value = message.getValue();

		if (value >= -128 && value <= 127) {
			PacketBuilder builder = new PacketBuilder(alloc, 40);
			builder.put(DataType.SHORT, id);
			builder.put(DataType.BYTE, DataTransformation.NEGATE, value);
			return builder.toPacket();
		} else {
			PacketBuilder builder = new PacketBuilder(alloc, 79);
			builder.put(DataType.INT, DataOrder.MIDDLE, value);
			builder.put(DataType.SHORT, DataOrder.LITTLE, id);
			return builder.toPacket();
		}
	}
}
