package net.scapeemulator.game.update.npc.block;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.npc.NpcBlock;

public class NpcShoutBlock extends NpcBlock {
	private final String shout;

	public NpcShoutBlock(Npc npc) {
		super(0x80);
		shout = npc.getShout();
	}

	@Override
	public void encode(NpcUpdateMessage message, PacketBuilder builder) {
		builder.putString(shout);
	}
}
