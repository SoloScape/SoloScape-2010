package net.scapeemulator.game.model.tele;

import net.scapeemulator.game.command.CommandHandler;
import net.scapeemulator.game.model.player.Player;

public class TeleportCommand extends CommandHandler {

	public TeleportCommand() {
		super("teleport");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() != 2)
			return;

		if (arguments.length == 1) {
			Teleport teleport = Teleport.getTeleport(player, arguments[0]);
			if (teleport == null) {
				player.sendMessage("Invalid teleport!", 99);
			} else {
				player.startAction(teleport);
			}
		} else {

		}
	}
}
