package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.Packet;

public final class ClientPreferencesMessageDecoder extends MessageDecoder<ClientPreferencesMessage> {
	private static final ClientPreferencesMessage MESSAGE = new ClientPreferencesMessage();

	public ClientPreferencesMessageDecoder() {
		super(32);
	}

	@Override
	public ClientPreferencesMessage decode(Packet packet) {
		// The 592 client sends its versioned preferences as a variable-byte packet.
		// These local graphics/audio settings are currently unused by the server.
		packet.getPayload().skipBytes(packet.getPayload().readableBytes());
		return MESSAGE;
	}
}
