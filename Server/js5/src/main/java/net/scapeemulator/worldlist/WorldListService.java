package net.scapeemulator.worldlist;

import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.api.Service;

public class WorldListService implements Service {
	protected Map<Integer, Country> countries = new HashMap<>();
	protected World[] worlds = new World[RESERVE_COUNT];

	public WorldListService() {
		initWorlds();
	}

	protected void initWorlds() {
		worlds[0] = new OnlineWorld(1, World.FLAG_MEMBERS, 0, "", "127.0.0.1");
		countries.put(0, new Country(69, "Finland"));
	}

	public World[] getWorlds() {
		return worlds;
	}

	public void setWorlds(World[] worlds) {
		this.worlds = worlds;
	}

	public Map<Integer, Country> getCountries() {
		return countries;
	}

	public void setCountries(Map<Integer, Country> countries) {
		this.countries = countries;
	}

	private static final int RESERVE_COUNT = 1;

	public void setPlayers(int id, int players) {
		for (World world : worlds) {
			if (world.getId() == id) {
				if (world instanceof OnlineWorld) {
					((OnlineWorld) world).setPlayers(players);
				}
			}
		}
	}
}
