package net.scapeemulator.game.update.player.block;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerBlock;

public class PlayerShoutBlock extends PlayerBlock {
	private final String shout;

	public PlayerShoutBlock(Player player) {
		super(123);
		shout = player.getShout();
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder) {
		builder.putString(shout);
	}
}
