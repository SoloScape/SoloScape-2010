package net.scapeemulator.game.msg.audio;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

public class SoundEffectEncoder extends MessageEncoder<SoundEffect> {
	public SoundEffectEncoder() {
		super(SoundEffect.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, SoundEffect message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 19);
		builder.put(DataType.SHORT, message.getId());
		builder.put(DataType.BYTE, message.getRepeats());
		builder.put(DataType.SHORT, message.getDelay());
		builder.put(DataType.BYTE, message.getVolume());
		return builder.toPacket();
	}
}
