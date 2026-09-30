package net.scapeemulator.game.command;

import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.audio.Jingle;
import net.scapeemulator.game.msg.audio.SoundEffect;

public final class AudioCommand extends CommandHandler {

	public AudioCommand() {
		super("audio");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() < 2)
			return;

		if (arguments.length < 1) {
			player.sendMessage("Syntax: audio [type (sfx, jingle, music)]", 99);
			return;
		}
		switch (arguments[0]) {
		case "music":
			if (arguments.length != 2) {
				player.sendMessage("Syntax: audio music [id]", 99);
			} else
				player.getStatus().setVar(1189, Integer.parseInt(arguments[1]));
			break;
		case "sfx":
			if (arguments.length != 5) {
				player.sendMessage("Syntax: audio sfx [repeats] [delay] [vol] [id]", 99);
			} else
				player.send(new SoundEffect(Integer.parseInt(arguments[4]), Integer.parseInt(arguments[1]),
						Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3])));
			break;
		case "jingle":
			if (arguments.length != 4) {
				player.sendMessage("Syntax: audio jingle [var1] [vol] [id]", 99);
			} else
				player.send(new Jingle(Integer.parseInt(arguments[3]), Integer.parseInt(arguments[1]),
						Integer.parseInt(arguments[2])));
		default:
			break;
		}
	}
}
