package net.scapeemulator.game.msg.entity;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.player.Player;

public class ExamineHandler extends MessageHandler<Player, ExamineMessage> {

	@Override
	public void handle(Player target, ExamineMessage message) {
		if (target.getRights() == 2) {
			target.sendMessage("[" + message.getType().name() + "=" + message.getId() + "]", 99);
		}
		switch (message.getType()) {
		case OBJECT:
			break;
		default:
			break;
		}
	}
}
