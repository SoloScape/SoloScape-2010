package net.scapeemulator.game.msg.chunk;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.Packet.Type;
import net.scapeemulator.game.model.map.ChunkUpdateMessage;
import net.scapeemulator.api.net.packet.PacketBuilder;

public class ChunkUpdateMessageEncoder extends MessageEncoder<ChunkUpdateMessage> {

	public ChunkUpdateMessageEncoder() {
		super(ChunkUpdateMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, ChunkUpdateMessage message) throws IOException {
		PacketBuilder pb = new PacketBuilder(alloc, 8, Type.VARIABLE_SHORT);
		pb.put(DataType.BYTE, DataTransformation.SUBTRACT, message.getX());
		pb.put(DataType.BYTE, message.getY());
		pb.put(DataType.BYTE, DataTransformation.NEGATE, message.getHeight());
		for(ChunkMessage c : message.getPackets()) {
			c.encode(pb);
		}
		return pb.toPacket();
	}
}
