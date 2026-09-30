package net.scapeemulator.game.update.player.block;

import net.scapeemulator.api.net.packet.*;
import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerBlock;

public final class AnimationPlayerBlock extends PlayerBlock {
	private final Animation animation;

	public AnimationPlayerBlock(Player player) {
		super(8);
		this.animation = player.getAnimation();
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder) {
		builder.put(DataType.SHORT, animation.getId());
		builder.put(DataType.BYTE, animation.getDelay());
	}
}
