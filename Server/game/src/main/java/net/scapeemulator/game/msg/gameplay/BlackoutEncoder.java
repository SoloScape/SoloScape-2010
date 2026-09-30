package net.scapeemulator.game.msg.gameplay;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

public class BlackoutEncoder extends MessageEncoder<Blackout> {

	public BlackoutEncoder() {
		super(Blackout.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, Blackout blackout) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 38);
		builder.put(DataType.BYTE, blackout.ordinal());
		return builder.toPacket();
	}
}
