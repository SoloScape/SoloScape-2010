package net.scapeemulator.game.msg.gameplay;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;
import net.scapeemulator.util.ChatUtils;

import java.io.IOException;

public final class ChatMessageDecoder extends MessageDecoder<ChatMessage> {

	public ChatMessageDecoder() {
		super(237);
	}

	@Override
	public ChatMessage decode(Packet frame) throws IOException {
		PacketReader reader = new PacketReader(frame);
		int size = reader.getLength() - 2;

		int color = (int) reader.getUnsigned(DataType.BYTE);
		int effects = (int) reader.getUnsigned(DataType.BYTE);

		byte[] bytes = new byte[size];
		reader.getBytes(bytes);
		String text = ChatUtils.unpack(bytes);

		return new ChatMessage(color, effects, text);
	}

}
