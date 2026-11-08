package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.content.tutorial.ControlsTutorial;

public final class CameraMessageHandler extends MessageHandler<Player, CameraMessage> {

	@Override
	public void handle(Player player, CameraMessage message) {
		boolean rotated = player.recordCameraPosition(message.getYaw(), message.getPitch());
		if (rotated)
			ControlsTutorial.completeOnCameraRotation(player);
	}

}
