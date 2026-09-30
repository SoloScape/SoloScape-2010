package net.scapeemulator.game;

import net.scapeemulator.game.command.CommandHandler;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.util.WelcomeScreen;
import net.scapeemulator.game.model.player.Player;

public class Worldmap extends CommandHandler {

	public Worldmap() {
		super("wmap");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() != 2)
			return;
		if (!player.getDisplay().open(WelcomeScreen.INSTANCE, DisplayMode.FULLSCREEN_INTERFACE, false)) {
			player.getDisplay().closeInterfaces();
		}
	}
}
