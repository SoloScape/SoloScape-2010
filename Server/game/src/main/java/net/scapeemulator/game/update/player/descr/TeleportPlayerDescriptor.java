package net.scapeemulator.game.update.player.descr;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerDescriptor;
import net.scapeemulator.game.update.player.PlayerUpdater;

public final class TeleportPlayerDescriptor extends PlayerDescriptor {
	private final Position start, target;

	public TeleportPlayerDescriptor(Player player, int[] tickets) {
		super(player, tickets);
		this.start = player.getStartStep();
		this.target = player.getPosition();
	}

	@Override
	public void encodeDescriptor(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBit(true);// Update
		builder.putBit(isBlockUpdatedRequired());
		builder.putBits(2, 3);
		boolean big = !target.isWithinDistance(start);
		builder.putBit(big);
		if (big) {
			int loc = PlayerUpdater.translate(start.getHeight(), target.getHeight(), 4) << 28;
			loc |= (PlayerUpdater.translate(start.getX(), target.getX(), 16384) << 14);
			loc |= (PlayerUpdater.translate(start.getY(), target.getY(), 16384));
			builder.putBits(30, loc);
		} else {
			int loc = PlayerUpdater.translate(start.getHeight(), target.getHeight(), 4) << 10;
			int xChange = target.getX() - start.getX();
			int yChange = target.getY() - start.getY();
			if (xChange < 0)
				xChange += 32;
			if (yChange < 0)
				yChange += 32;
			loc |= xChange << 5;
			loc |= yChange;
			builder.putBits(12, loc);
		}
	}
}
