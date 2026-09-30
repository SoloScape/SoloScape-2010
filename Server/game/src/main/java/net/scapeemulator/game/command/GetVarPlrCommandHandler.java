package net.scapeemulator.game.command;

import net.scapeemulator.game.model.player.Player;

public final class GetVarPlrCommandHandler extends CommandHandler {

	public GetVarPlrCommandHandler() {
		super("getvarp");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() < 2)
			return;

		if (arguments.length != 1) {
			player.sendMessage("Syntax: getvarp [id]", 99);
			return;
		}
		try {
			int id = Integer.parseInt(arguments[0]);
			player.sendMessage("VarP [" + id + "]: " + player.getStatus().getCurrentVar(id), 99);
		} catch (Exception e) {
			player.sendMessage("Oops. " + e.getMessage(), 99);
			e.printStackTrace();
		}
	}

}
