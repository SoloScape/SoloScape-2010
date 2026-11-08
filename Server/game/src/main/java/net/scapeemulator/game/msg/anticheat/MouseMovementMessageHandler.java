package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.player.Player;

public final class MouseMovementMessageHandler extends MessageHandler<Player, MouseMovementMessage> {
	@Override
	public void handle(Player player, MouseMovementMessage message) {
		// Intentionally ignored; mouse telemetry must not trigger game movement.
	}
}
