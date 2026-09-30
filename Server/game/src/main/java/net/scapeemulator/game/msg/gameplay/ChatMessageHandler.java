package net.scapeemulator.game.msg.gameplay;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.player.Player;

public final class ChatMessageHandler extends MessageHandler<Player, ChatMessage> {

	@Override
	public void handle(Player player, ChatMessage message) {
		player.setChatMessage(message);
	}

}
