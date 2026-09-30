package net.scapeemulator.game.update.player;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.Descriptor;

public class SkipDescriptor extends Descriptor<PlayerUpdateMessage> {
	private final int skipNum;
	private final int opcode;

	public SkipDescriptor(int skipNum) {
		this.skipNum = skipNum;
		if (skipNum == 0) {
			opcode = 0;
		} else if (skipNum < 32) {
			opcode = 1;
		} else if (skipNum < 256) {
			opcode = 2;
		} else {
			opcode = 3;
		}
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBit(false);
		builder.putBits(2, opcode);
		switch (opcode) {
		case 1:
			builder.putBits(5, skipNum);
			break;
		case 2:
			builder.putBits(8, skipNum);
			break;
		case 3:
			builder.putBits(11, skipNum);
			break;
		}
	}
}
