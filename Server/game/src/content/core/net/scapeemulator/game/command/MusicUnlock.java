package net.scapeemulator.game.command;

import net.scapeemulator.game.model.def.TrackDefinition;
import net.scapeemulator.game.model.hud.util.MusicTab;
import net.scapeemulator.game.model.player.Player;

public class MusicUnlock extends CommandHandler {

	public MusicUnlock() {
		super("musicf");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (arguments.length == 1) {
			player.getStatus().setVar(1189, TrackDefinition.getTrackId(Integer.parseInt(arguments[0])));
		} else {
			for (int i = 0; i <= TrackDefinition.getLastTrackId(); i++) {
				MusicTab.unlock(player, i);
			}
		}
	}
}
