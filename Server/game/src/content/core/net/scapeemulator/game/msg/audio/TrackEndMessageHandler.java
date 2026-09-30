package net.scapeemulator.game.msg.audio;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.def.TrackDefinition;
import net.scapeemulator.game.model.player.Player;

public class TrackEndMessageHandler extends MessageHandler<Player, TrackEndMessage> {

	@Override
	public void handle(Player target, TrackEndMessage message) {
		if (target.getRights() == 2) {
			target.sendMessage("[TrackEnd=" + message.getId() + '('
					+ TrackDefinition.get(TrackDefinition.getTrackId(message.getId())) + ")]", 99);
		}
	}
}
