package net.scapeemulator.game.update.npc.block;

import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.mob.combat.Hit;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.npc.NpcBlock;

public class PrimaryDamageNpcBlock extends NpcBlock {
	private final Hit hit;
	private final int hpRatio;

	public PrimaryDamageNpcBlock(Npc npc) {
		super(0x20);
		hit = npc.getSecondaryHit() == null ? npc.getPrimaryHit() : npc.getSecondaryHit();
		hpRatio = npc.getSkillSet().getCurrentLevel(3) * 255 / npc.getSkillSet().getMaximumLevel(3);
	}

	@Override
	public void encode(NpcUpdateMessage message, PacketBuilder builder) {
		builder.putSmart(hit.getDamage());
		builder.put(DataType.BYTE, DataTransformation.ADD, hit.getType().ordinal());
		builder.put(DataType.BYTE, DataTransformation.ADD, hpRatio);
	}
}
