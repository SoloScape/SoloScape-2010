package net.scapeemulator.game.msg.area;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.Packet;

import java.io.IOException;

public final class AreaChangeMessageDecoder extends MessageDecoder<AreaChangeMessage> {

	private static final AreaChangeMessage REGION_CHANGED_MESSAGE = new AreaChangeMessage();

	public AreaChangeMessageDecoder() {
		super(110);
	}

	@Override
	public AreaChangeMessage decode(Packet frame) throws IOException {
		return REGION_CHANGED_MESSAGE;
	}

}
