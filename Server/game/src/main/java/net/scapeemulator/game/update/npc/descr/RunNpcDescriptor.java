package net.scapeemulator.game.update.npc.descr;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.mob.Direction;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.npc.NpcDescriptor;

public final class RunNpcDescriptor extends NpcDescriptor {

	private final Direction walkDirection, runDirection;

	public RunNpcDescriptor(Npc npc, Direction walkDirection, Direction runDirection) {
		super(npc);
		this.walkDirection = walkDirection;
		this.runDirection = runDirection;
	}

	@Override
	public void encodeDescriptor(NpcUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBits(1, 1);
		builder.putBits(2, 2);
		builder.putBits(1, 1);//Slow walking = 0
		builder.putBits(3, walkDirection.toInteger());
		builder.putBits(3, runDirection.toInteger());
		builder.putBits(1, isBlockUpdatedRequired() ? 1 : 0);
	}
}
