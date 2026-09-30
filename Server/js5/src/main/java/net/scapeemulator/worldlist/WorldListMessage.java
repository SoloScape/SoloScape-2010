package net.scapeemulator.worldlist;

import java.util.Map;

public final class WorldListMessage {
	private final Map<Integer, Country> countries;
	private final World[] worlds;

	public WorldListMessage(Map<Integer, Country> countries, World[] worlds) {
		this.countries = countries;
		this.worlds = worlds;
	}

	public Map<Integer, Country> getCountries() {
		return countries;
	}

	public World[] getWorlds() {
		return worlds;
	}
}
