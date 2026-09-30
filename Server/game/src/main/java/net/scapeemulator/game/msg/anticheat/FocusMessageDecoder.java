package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;

import java.io.IOException;

public final class FocusMessageDecoder extends MessageDecoder<FocusMessage> {

	private static final FocusMessage FOCUSED_MESSAGE = new FocusMessage(true);
	private static final FocusMessage NOT_FOCUSED_MESSAGE = new FocusMessage(false);

	public FocusMessageDecoder() {
		super(48);
	}

	@Override
	public FocusMessage decode(Packet frame) throws IOException {
		PacketReader reader = new PacketReader(frame);
		int focused = (int) reader.getUnsigned(DataType.BYTE);
		return focused != 0 ? FOCUSED_MESSAGE : NOT_FOCUSED_MESSAGE;
	}
}
