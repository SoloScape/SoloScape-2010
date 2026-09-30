package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.player.Player;

public final class DisplayMessageHandler extends MessageHandler<Player, DisplayMessage> {

	@Override
	public void handle(Player player, DisplayMessage message) {
		player.getDisplay().changeDisplayMode(message.getMode());
	}
}
