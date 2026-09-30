package net.scapeemulator.game.update.npc.block;

import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.mob.combat.Hit;
import net.scapeemulator.game.update.npc.NpcBlock;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;


public class SecondaryDamageNpcBlock extends NpcBlock {
	private final Hit hit;

	public SecondaryDamageNpcBlock(Npc npc) {
		super(2);
		hit = npc.getPrimaryHit();
	}

	@Override
	public void encode(NpcUpdateMessage message, PacketBuilder builder) {
		builder.putSmart(hit.getDamage());
		builder.put(DataType.BYTE, DataTransformation.ADD, hit.getType().ordinal());
	}
}
