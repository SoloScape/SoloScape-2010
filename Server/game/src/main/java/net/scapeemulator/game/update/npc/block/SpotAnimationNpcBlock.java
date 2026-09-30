package net.scapeemulator.game.update.npc.block;

import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.mob.SpotAnimation;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.npc.NpcBlock;

public final class SpotAnimationNpcBlock extends NpcBlock {

	private final SpotAnimation spotAnimation;

	public SpotAnimationNpcBlock(Npc npc) {
		super(1);
		this.spotAnimation = npc.getSpotAnimation();
	}

	@Override
	public void encode(NpcUpdateMessage message, PacketBuilder builder) {
		builder.put(DataType.SHORT, DataTransformation.ADD, spotAnimation.getId());
		builder.put(DataType.INT, DataOrder.INVERSED_MIDDLE,
				(spotAnimation.getHeight() << 16) | spotAnimation.getDelay());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, 0);
	}

}
