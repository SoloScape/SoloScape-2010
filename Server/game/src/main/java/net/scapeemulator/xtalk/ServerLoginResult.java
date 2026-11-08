package net.scapeemulator.xtalk;

import java.util.Map;

import net.scapeemulator.worldlist.Country;

public class ServerLoginResult {
	public static final int STATUS_EXCHANGE_KEYS = 0;
	public static final int KEY_MISMATCH = 1;
	public static final int SUCCESS = 2;
	public static final int WORLD_ALREADY_ONLINE = 5;
	public static final int INVALID_VERSION = 6;
	public static final int INVALID_WORLD = 20;
	
	private final int result;
	private final Map<Integer, Country> countries;

	public ServerLoginResult(int result, Map<Integer, Country> countries) {
		this.result = result;
		this.countries = countries;
	}

	public int getResult() {
		return result;
	}

	public Map<Integer, Country> getCountries() {
		return countries;
	}
}
