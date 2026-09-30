package net.scapeemulator.game.update.player.block;

import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.mob.combat.Hit;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerBlock;

public class SecondaryDamagePlayerBlock extends PlayerBlock {
	private final Hit hit;

	public SecondaryDamagePlayerBlock(Player player) {
		super(0x100);
		hit = player.getSecondaryHit();
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder) {
		builder.putSmart(hit.getDamage());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, hit.getType().ordinal());
	}
}
