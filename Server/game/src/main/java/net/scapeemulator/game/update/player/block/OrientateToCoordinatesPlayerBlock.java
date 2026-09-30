package net.scapeemulator.game.update.player.block;

import net.scapeemulator.api.net.packet.*;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerBlock;

public final class OrientateToCoordinatesPlayerBlock extends PlayerBlock {
	private final int position;

	public OrientateToCoordinatesPlayerBlock(Player player) {
		super(0x40);
		int xDiff = -player.getOrientationPosition().getX() + player.getPosition().getX();
		int yDiff = -player.getOrientationPosition().getY() + player.getPosition().getY();
		//TODO: HALF TILES
		position = 0x3fff & (int) (2607.5945876176133D * Math.atan2(xDiff, yDiff));
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder) {
		builder.put(DataType.SHORT, position);
	}
}
