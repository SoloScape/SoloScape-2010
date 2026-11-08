package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.player.Player;

public final class ClientPreferencesMessageHandler extends MessageHandler<Player, ClientPreferencesMessage> {
	@Override
	public void handle(Player player, ClientPreferencesMessage message) {
		// Preferences affect the client only; no server state needs updating.
	}
}
