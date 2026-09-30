package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;

import java.io.IOException;

public final class CommandMessageDecoder extends MessageDecoder<CommandMessage> {

	public CommandMessageDecoder() {
		super(78);
	}

	@Override
	public CommandMessage decode(Packet frame) throws IOException {
		PacketReader reader = new PacketReader(frame);
		reader.getUnsigned(DataType.BYTE);
		String command = reader.getString();
		return new CommandMessage(command);
	}
}
