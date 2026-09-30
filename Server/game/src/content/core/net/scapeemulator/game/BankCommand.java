package net.scapeemulator.game;

import net.scapeemulator.game.command.CommandHandler;
import net.scapeemulator.game.model.hud.storage.BankInterface;
import net.scapeemulator.game.model.player.Player;

public class BankCommand extends CommandHandler {

	public BankCommand() {
		super("bank");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() != 2)
			return;
		player.getDisplay().open(BankInterface.INSTANCE, false);
	}
}
