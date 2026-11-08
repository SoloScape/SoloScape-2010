package net.scapeemulator.game.grandexchange.commands;

import net.scapeemulator.game.command.CommandHandler;
import net.scapeemulator.game.grandexchange.interf.*;
import net.scapeemulator.game.model.player.Player;

public class GrandExchangeInterOpen extends CommandHandler {

	public GrandExchangeInterOpen() {
		super("geinter");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		switch (arguments[0]) {
		case "sets":
			if (!player.getDisplay().open(ItemSets.INSTANCE, false)) {
				player.sendMessage("Please close the interface you have open before opening Grand Exchange item sets.");
			}
			break;
		case "common":
			player.getStatus().setVarC(1001, Integer.parseInt(arguments[1]));
			if (!player.getDisplay().open(CommonPrices.INSTANCE, false)) {
				player.sendMessage(
						"Please close the interface you have open before opening Grand Exchange common prices.");
			}
			break;
		case "collect":
			if (!player.getDisplay().open(CollectBox.INSTANCE, false)) {
				player.sendMessage("Please close the interface you have open before opening Grand Exchange collection box.");
			}
			break;
		case "main":
			break;
		case "history":
			if (!player.getDisplay().open(History.INSTANCE, false)) {
				player.sendMessage("Please close the interface you have open before opening Grand Exchange history.");
			}
			break;
		default:
			player.sendMessage("Unregistered subcommand!", 99);
			break;
		}
	}
}
