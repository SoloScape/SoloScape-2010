package net.scapeemulator.game.msg.gameplay;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.*;

public final class SkillMessageEncoder extends MessageEncoder<SkillMessage> {

	public SkillMessageEncoder() {
		super(SkillMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, SkillMessage message) {
		PacketBuilder builder = new PacketBuilder(alloc, 9);
		builder.put(DataType.INT, DataOrder.LITTLE, message.getExperience());
		builder.put(DataType.BYTE, DataTransformation.ADD, message.getSkill());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, message.getLevel());
		return builder.toPacket();
	}

}
