package net.scapeemulator.game.update.player.block;

import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.mob.combat.Hit;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerBlock;

public class PrimaryDamagePlayerBlock extends PlayerBlock {
	private final Hit hit;
	private final int hpRatio;

	public PrimaryDamagePlayerBlock(Player player) {
		super(0x10);
		hit = player.getPrimaryHit();
		int hp = player.getSkillSet().getCurrentLevel(3);
		if (hit.isCosmetic()) {// ???
			hp -= hit.getDamage();
			if (hp < 0)
				hp = 0;
		}
		hpRatio = hp * 255 / player.getSkillSet().getMaximumLevel(3);
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder) {
		builder.putSmart(hit.getDamage());
		builder.put(DataType.BYTE, DataTransformation.ADD, hit.getType().ordinal());
		builder.put(DataType.BYTE, DataTransformation.ADD, hpRatio);
	}
}
