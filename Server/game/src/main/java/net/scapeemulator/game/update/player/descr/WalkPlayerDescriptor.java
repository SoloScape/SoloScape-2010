package net.scapeemulator.game.update.player.descr;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.mob.Direction;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerDescriptor;

public final class WalkPlayerDescriptor extends PlayerDescriptor {
	private final Direction direction;

	public WalkPlayerDescriptor(Player player, int[] tickets) {
		super(player, tickets);
		this.direction = Direction.between(player.getStartStep(), player.getWalkStep());
	}

	@Override
	public void encodeDescriptor(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBit(true);// Update
		builder.putBit(isBlockUpdatedRequired());
		builder.putBits(2, 1);
		builder.putBits(3, direction.ordinal() - 1);
	}
}
