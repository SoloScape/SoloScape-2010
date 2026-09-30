package net.scapeemulator.game.msg.area;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

import java.io.IOException;

public final class ResetMinimapFlagMessageEncoder extends MessageEncoder<ResetMinimapFlagMessage> {

	public ResetMinimapFlagMessageEncoder() {
		super(ResetMinimapFlagMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, ResetMinimapFlagMessage message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 153);
		return builder.toPacket();
	}

}
