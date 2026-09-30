package net.scapeemulator.game.update.player.block;

import net.scapeemulator.api.net.packet.*;
import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerBlock;

public final class OrientateToMobPlayerBlock extends PlayerBlock {
	private final int interactingMobId;

	public OrientateToMobPlayerBlock(Player player) {
		super(2);
		Mob target = player.getInteractingTarget();
		if (target != null)
			interactingMobId = target.getClass() == Player.class ? target.getId() + 32768 : target.getId();
		else
			interactingMobId = -1;
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder) {
		builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, interactingMobId);
	}
}
