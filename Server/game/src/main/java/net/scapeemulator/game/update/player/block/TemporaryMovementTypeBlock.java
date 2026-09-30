package net.scapeemulator.game.update.player.block;

import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerBlock;

public class TemporaryMovementTypeBlock extends PlayerBlock {
	private final int type;

	public TemporaryMovementTypeBlock(Player player, int type) {
		super(0x200);
		this.type = type;
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder) {
		builder.put(DataType.BYTE, DataTransformation.NEGATE, type);
	}
}
