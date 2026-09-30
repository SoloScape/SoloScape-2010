package net.scapeemulator.game.update.npc.descr;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.npc.NpcDescriptor;

public final class RemoveNpcDescriptor extends NpcDescriptor {

	public RemoveNpcDescriptor(Npc npc) {
		super(npc);
	}

	@Override
	public void encodeDescriptor(NpcUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBits(1, 1);
		builder.putBits(2, 3);
	}

}
