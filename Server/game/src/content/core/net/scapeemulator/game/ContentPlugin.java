package net.scapeemulator.game;

import java.util.ArrayList;
import java.util.List;

import net.scapeemulator.game.grandexchange.GrandExchangeCore;

public class ContentPlugin extends Plugin {
	private List<Plugin> subPlugins;

	public ContentPlugin() {
		subPlugins = new ArrayList<>();
		subPlugins.add(new GrandExchangeCore());
	}

	@Override
	public void register(GameServer gameServer) {
		for(Plugin plugin : subPlugins) {
			plugin.register(gameServer);
		}
	}
}
