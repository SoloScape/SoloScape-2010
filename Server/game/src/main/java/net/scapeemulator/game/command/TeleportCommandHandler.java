package net.scapeemulator.game.command;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;

public final class TeleportCommandHandler extends CommandHandler {

	public TeleportCommandHandler() {
		super("tele");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() < 2)
			return;

		if (arguments.length != 1) {
			if (arguments.length != 2 && arguments.length != 3) {
				player.sendMessage("Syntax: tele [x] [y] [height=0] or: tele [height],[xArea],[yArea],[xLocal],[yLocal]", 99);
				return;
			}
			int x = Integer.parseInt(arguments[0]);
			int y = Integer.parseInt(arguments[1]);
			int height = player.getPosition().getHeight();

			if (arguments.length == 3)
				height = Integer.parseInt(arguments[2]);

			player.teleport(new Position(x, y, height));
		} else {
			arguments = arguments[0].split(",");
			if (arguments.length != 5) {
				player.sendMessage("Syntax: tele [x] [y] [height=0] or: tele [height],[xArea],[yArea],[xLocal],[yLocal]", 99);
				return;
			}
			int x = (Integer.parseInt(arguments[1]) << 6) + Integer.parseInt(arguments[3]);
			int y = (Integer.parseInt(arguments[2]) << 6) + Integer.parseInt(arguments[4]);
			int height = Integer.parseInt(arguments[0]);
			player.teleport(new Position(x, y, height));
		}
	}

}
