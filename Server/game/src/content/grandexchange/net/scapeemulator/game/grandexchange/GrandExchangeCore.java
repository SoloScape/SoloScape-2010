package net.scapeemulator.game.grandexchange;

import net.scapeemulator.game.GameServer;
import net.scapeemulator.game.Plugin;
import net.scapeemulator.game.grandexchange.commands.GrandExchangeInterOpen;

public class GrandExchangeCore extends Plugin {

	@Override
	public void register(GameServer gameServer) {
		gameServer.getCommandDispatcher().bind(new GrandExchangeInterOpen());
	}
}
