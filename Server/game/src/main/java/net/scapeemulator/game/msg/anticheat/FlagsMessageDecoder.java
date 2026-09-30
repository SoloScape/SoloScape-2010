package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;

import java.io.IOException;

public final class FlagsMessageDecoder extends MessageDecoder<FlagsMessage> {

	public FlagsMessageDecoder() {
		super(98);
	}

	@Override
	public FlagsMessage decode(Packet frame) throws IOException {
		PacketReader reader = new PacketReader(frame);
		int flags = (int) reader.getUnsigned(DataType.INT);
		return new FlagsMessage(flags);
	}

}
