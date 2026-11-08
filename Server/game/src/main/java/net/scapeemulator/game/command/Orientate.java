package net.scapeemulator.game.command;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;

public class Orientate extends CommandHandler {

	public Orientate() {
		super("orientate");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		try {
			if (arguments.length == 2) {
				player.orientateToPosition(
						new Position(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1])));
			} else if (arguments.length == 3) {
				player.orientateToPosition(new Position(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]),
						Integer.parseInt(arguments[2])));
			} else {
				player.sendMessage("Invalid arguments. Use orientate [x] [y] (z)", 99);
			}
		} catch (Exception e) {
			player.sendMessage("Invalid arguments. Use orientate [x] [y] (z)", 99);
		}
	}
}
