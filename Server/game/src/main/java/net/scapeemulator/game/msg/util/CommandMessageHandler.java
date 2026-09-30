package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.command.CommandDispatcher;
import net.scapeemulator.game.model.player.Player;

public final class CommandMessageHandler extends MessageHandler<Player, CommandMessage> {
	private final CommandDispatcher dispatcher;

	public CommandMessageHandler(CommandDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void handle(Player player, CommandMessage message) {
		dispatcher.handle(player, message.getCommand());
	}
}
