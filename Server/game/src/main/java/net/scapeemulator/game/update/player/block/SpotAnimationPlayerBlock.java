package net.scapeemulator.game.update.player.block;

import net.scapeemulator.api.net.packet.*;
import net.scapeemulator.game.model.mob.SpotAnimation;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerBlock;

public final class SpotAnimationPlayerBlock extends PlayerBlock {

	private final SpotAnimation spotAnimation;

	public SpotAnimationPlayerBlock(Player player) {
		super(0x1000);
		this.spotAnimation = player.getSpotAnimation();
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder) {
		builder.put(DataType.SHORT, DataOrder.LITTLE, spotAnimation.getId());
		builder.put(DataType.INT, (spotAnimation.getHeight() << 16) | spotAnimation.getDelay());
		builder.put(DataType.BYTE, DataTransformation.NEGATE, 0);
	}
}
