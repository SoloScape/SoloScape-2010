package net.scapeemulator.game.update.npc.descr;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.mob.Direction;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.npc.NpcDescriptor;

public final class WalkNpcDescriptor extends NpcDescriptor {

	private final Direction direction;

	public WalkNpcDescriptor(Npc npc, Direction direction) {
		super(npc);
		this.direction = direction;
	}

	@Override
	public void encodeDescriptor(NpcUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBits(1, 1);
		builder.putBits(2, 1);
		builder.putBits(3, direction.toInteger());
		builder.putBits(1, isBlockUpdatedRequired() ? 1 : 0);
	}

}
