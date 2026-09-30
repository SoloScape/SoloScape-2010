package net.scapeemulator.game.msg.gameplay;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

public class VarBitMessageEncoder extends MessageEncoder<VarBitMessage> {

	public VarBitMessageEncoder() {
		super(VarBitMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, VarBitMessage message) throws IOException {
		int value = message.getValue();
		if (value >= -128 && value <= 127) {
			PacketBuilder builder = new PacketBuilder(alloc, 14);
			builder.put(DataType.SHORT, message.getVarBitId());
			builder.put(DataType.BYTE, value);
			return builder.toPacket();
		} else {
			PacketBuilder builder = new PacketBuilder(alloc, 85);
			builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, message.getVarBitId());
			builder.put(DataType.INT, DataOrder.LITTLE, value);
			return builder.toPacket();
		}
	}
}
