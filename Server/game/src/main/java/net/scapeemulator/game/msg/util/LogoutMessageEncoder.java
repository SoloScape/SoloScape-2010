package net.scapeemulator.game.msg.util;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

public final class LogoutMessageEncoder extends MessageEncoder<LogoutMessage> {

	public LogoutMessageEncoder() {
		super(LogoutMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, LogoutMessage message) {
		PacketBuilder builder = new PacketBuilder(alloc, 33);
		return builder.toPacket();
	}
}
