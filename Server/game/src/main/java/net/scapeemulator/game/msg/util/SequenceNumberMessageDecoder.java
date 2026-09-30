package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;

import java.io.IOException;

public final class SequenceNumberMessageDecoder extends MessageDecoder<SequenceNumberMessage> {

	public SequenceNumberMessageDecoder() {
		super(71);
	}

	@Override
	public SequenceNumberMessage decode(Packet frame) throws IOException {
		PacketReader reader = new PacketReader(frame);
		int sequenceNumber = (int) reader.getUnsigned(DataType.SHORT);
		return new SequenceNumberMessage(sequenceNumber);
	}

}
