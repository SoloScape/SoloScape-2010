package net.scapeemulator.game.command;

import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.util.GlobalStringVarMessage;

public final class VarStringCommandHandler extends CommandHandler {

	public VarStringCommandHandler() {
		super("varstr");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() < 2)
			return;

		if (arguments.length != 2) {
			player.sendMessage("Syntax: varstr [id] [text]", 99);
			return;
		}
		int id = Integer.parseInt(arguments[0]);
		try {
			player.send(new GlobalStringVarMessage(id, arguments[1]));
		} catch (Exception e) {
			player.sendMessage("Oops. " + e.getMessage(), 99);
			e.printStackTrace();
		}
	}

}
