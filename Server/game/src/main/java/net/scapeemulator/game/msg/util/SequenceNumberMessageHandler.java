package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.player.Player;

public final class SequenceNumberMessageHandler extends MessageHandler<Player, SequenceNumberMessage> {

	@Override
	public void handle(Player player, SequenceNumberMessage message) {
		if (message.getSequenceNumber() != 0)
			player.packetsReached(message.getSequenceNumber());
	}
}
