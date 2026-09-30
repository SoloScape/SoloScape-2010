package net.scapeemulator.game.msg.audio;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

public class JingleEncoder extends MessageEncoder<Jingle> {
	public JingleEncoder() {
		super(Jingle.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, Jingle message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 21);
		builder.put(DataType.TRI_BYTE, message.getVar1());
		builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, message.getId());
		builder.put(DataType.BYTE, message.getVolume());
		return builder.toPacket();
	}
}
