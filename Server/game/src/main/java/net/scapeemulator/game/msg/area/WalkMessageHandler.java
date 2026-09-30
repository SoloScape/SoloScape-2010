package net.scapeemulator.game.msg.area;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.pathfinder.PathFinder;
import net.scapeemulator.game.model.player.Player;

public final class WalkMessageHandler extends MessageHandler<Player, WalkMessage> {

	@Override
	public void handle(Player player, WalkMessage message) {
		if (message.isMinimap()) {
			switch (player.getBlackout()) {
			case COMPASS_BLACKOUT_MINIMAP_UNUSABLE:
			case MINIMAP_BLACKOUT:
			case MINIMAP_UNUSABLE:
			case TOTAL_BLACKOUT:
				return;
			default:
				break;
			}
		}
		if (!player.blockingAction()) {
			player.stopAction();
			player.getDisplay().closeInterfaces();
			player.getMovement().reset();
			PathFinder.findPath(player, message.getX(), message.getY(), 1, 1, false, message.isRunning());
		}
	}
}
