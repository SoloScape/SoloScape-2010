package net.scapeemulator.game.command;

import net.scapeemulator.game.model.player.Player;

public final class PositionCommandHandler extends CommandHandler {

	public PositionCommandHandler() {
		super("pos");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() < 2)
			return;

		if (arguments.length != 0) {
			player.sendMessage("Syntax: pos", 99);
			return;
		}

		player.sendMessage("You are at: " + player.getPosition(), 99);
	}
}
