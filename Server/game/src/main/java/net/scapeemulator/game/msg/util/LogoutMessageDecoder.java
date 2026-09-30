package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.Packet;

import java.io.IOException;

public final class LogoutMessageDecoder extends MessageDecoder<LogoutMessage> {
	private static final LogoutMessage IDLE_LOGOUT_MESSAGE = new LogoutMessage();

	public LogoutMessageDecoder() {
		super(245);
	}

	@Override
	public LogoutMessage decode(Packet frame) throws IOException {
		return IDLE_LOGOUT_MESSAGE;
	}
}
