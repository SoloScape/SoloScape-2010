package net.scapeemulator.game.update.player.descr;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerDescriptor;

public final class IdlePlayerDescriptor extends PlayerDescriptor {

	public IdlePlayerDescriptor(Player player, int[] tickets) {
		super(player, tickets);
	}

	@Override
	public void encodeDescriptor(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBit(true);// Update
		builder.putBit(isBlockUpdatedRequired());
		builder.putBits(2, 0);
	}
}
