package net.scapeemulator.game.update.npc.block;

import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.npc.NpcBlock;

public final class TransformationNpcBlock extends NpcBlock {
	private final int definitionId;

	public TransformationNpcBlock(Npc npc) {
		super(0x10);
		this.definitionId = npc.getType();
	}

	@Override
	public void encode(NpcUpdateMessage message, PacketBuilder builder) {
		builder.put(DataType.SHORT, definitionId);
	}
}
