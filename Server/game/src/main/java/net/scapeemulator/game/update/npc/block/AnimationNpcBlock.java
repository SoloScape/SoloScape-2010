package net.scapeemulator.game.update.npc.block;

import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.npc.NpcBlock;

public final class AnimationNpcBlock extends NpcBlock {

	private final Animation animation;

	public AnimationNpcBlock(Npc npc) {
		super(4);
		this.animation = npc.getAnimation();
	}

	@Override
	public void encode(NpcUpdateMessage message, PacketBuilder builder) {
		builder.put(DataType.SHORT, DataOrder.LITTLE, animation.getId());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, animation.getDelay());
	}
}
