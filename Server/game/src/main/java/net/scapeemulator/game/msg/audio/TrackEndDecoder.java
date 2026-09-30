package net.scapeemulator.game.msg.audio;

import java.io.IOException;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;

/**
 * TODO DOC
 * 
 * @author Teemu
 *
 */
public class TrackEndDecoder extends MessageDecoder<TrackEndMessage> {

	public TrackEndDecoder() {
		super(25);
	}

	@Override
	public TrackEndMessage decode(Packet packet) throws IOException {
		PacketReader reader = new PacketReader(packet);
		int id = (int) reader.getUnsigned(DataType.INT);
		return new TrackEndMessage(id);
	}
}
