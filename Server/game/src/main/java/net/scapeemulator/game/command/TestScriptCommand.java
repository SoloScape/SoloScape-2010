package net.scapeemulator.game.command;

import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.util.ScriptMessage;

public final class TestScriptCommand extends CommandHandler {

	public TestScriptCommand() {
		super("script");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() < 2)
			return;
		try {
			player.send(new ScriptMessage(Integer.parseInt(arguments[0]), ""));
		} catch (Exception e) {
			player.sendMessage("Oops! Something went wrong.", 99);
		}
	}
}
