package net.scapeemulator.game.update.player.descr;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerDescriptor;

public final class RunPlayerDescriptor extends PlayerDescriptor {
	private final int dir;

	public RunPlayerDescriptor(Player player, int[] tickets, int dir) {
		super(player, tickets);
		this.dir = dir;
	}

	@Override
	public void encodeDescriptor(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBit(true);// Update
		builder.putBit(isBlockUpdatedRequired());
		builder.putBits(2, 2);
		builder.putBits(4, dir);
	}

	public static int convert(Player player) {
		Position start = player.getStartStep();
		Position walk = player.getWalkStep();
		Position run = player.getRunStep();
		if (run == null)
			return -1;
		int deltaX = walk.getX() - start.getX();
		int deltaY = walk.getY() - start.getY();
		deltaX += run.getX() - walk.getX();
		deltaY += run.getY() - walk.getY();
		Position thirdStep;
		if ((deltaY != 2 && deltaY != -2) && (deltaX != 2 && deltaX != -2)) {
			thirdStep = player.getMovement().peek();
			deltaX += thirdStep.getX() - run.getX();
			deltaY += thirdStep.getY() - run.getY();
		}
		return convert(deltaX, deltaY);
	}

	public static int convert(int xChange, int yChange) {
		switch (yChange) {
		case -2:
			return 2 + xChange;
		case -1:
			if (xChange == 2) {
				return 6;
			} else if (xChange == -2) {
				return 5;
			} else {
				return -1;
			}
		case 0:
			if (xChange == 2) {
				return 8;
			} else if (xChange == -2) {
				return 7;
			} else {
				return -1;
			}
		case 1:
			if (xChange == 2) {
				return 10;
			} else if (xChange == -2) {
				return 9;
			} else {
				return -1;
			}
		case 2:
			return 13 + xChange;
		default:
			return -1;
		}
	}
}
