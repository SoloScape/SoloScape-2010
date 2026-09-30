package net.scapeemulator.game.command;

import net.scapeemulator.game.model.player.Player;

public final class VarBitCommandHandler extends CommandHandler {

	public VarBitCommandHandler() {
		super("varbit");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() < 2)
			return;

		if (arguments.length != 2) {
			player.sendMessage("Syntax: varbit [id] [status]", 99);
			return;
		}
		int id = Integer.parseInt(arguments[0]);
		int status = Integer.parseInt(arguments[1]);
		try {
			player.getStatus().setVarBit(id, status);
		} catch (Exception e) {
			player.sendMessage("Oops. " + e.getMessage(), 99);
			e.printStackTrace();
		}
	}

}
