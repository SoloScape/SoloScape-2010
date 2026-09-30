package net.scapeemulator.game.msg.entity;

import java.io.IOException;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;
import net.scapeemulator.game.msg.entity.ExamineMessage.ExamineType;

public class ExamineDecoder extends MessageDecoder<ExamineMessage> {
	private final ExamineType type;

	public ExamineDecoder(ExamineType type) {
		super(type.getOpcode());
		this.type = type;
	}

	@Override
	public ExamineMessage decode(Packet packet) throws IOException {
		PacketReader reader = new PacketReader(packet);
		int id = (int) reader.getUnsigned(DataType.SHORT, type.getOrder(), type.getTransf());
		return new ExamineMessage(type, id);
	}
}
