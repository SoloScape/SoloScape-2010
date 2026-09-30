package net.scapeemulator.game.msg.audio;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.def.TrackDefinition;

public class MusicTrackEncoder extends MessageEncoder<TrackDefinition> {

	public MusicTrackEncoder() {
		super(TrackDefinition.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, TrackDefinition message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 63);
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, 100);
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, 0);
		builder.put(DataType.SHORT, DataTransformation.ADD, message.getFileId());
		return builder.toPacket();
	}
}
