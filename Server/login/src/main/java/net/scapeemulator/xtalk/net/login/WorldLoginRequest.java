package net.scapeemulator.xtalk.net.login;

import java.util.Arrays;

import net.scapeemulator.worldlist.Country;

public class WorldLoginRequest {
	private final int version, worldId, flags;
	private final String activity;
	private final int[] crc;
	private final long clientSessionKey, serverSessionKey;
	private final Country country;

	public WorldLoginRequest(int version, int worldId, int flags, String activity, Country country, int[] crc, long clientSessionKey,
			long serverSessionKey) {
		this.version = version;
		this.worldId = worldId;
		this.flags = flags;
		this.activity = activity;
		this.country = country;
		this.crc = crc;
		this.clientSessionKey = clientSessionKey;
		this.serverSessionKey = serverSessionKey;
	}

	@Override
	public String toString() {
		return "WorldLoginRequest [version=" + version + ", worldId=" + worldId + ", flags=" + flags + ", activity="
				+ activity + ", crc=" + Arrays.toString(crc) + ", clientSessionKey=" + clientSessionKey
				+ ", serverSessionKey=" + serverSessionKey + "]";
	}

	public int getVersion() {
		return version;
	}

	public int getWorldId() {
		return worldId;
	}

	public int getFlags() {
		return flags;
	}

	public String getActivity() {
		return activity;
	}

	public int[] getCrc() {
		return crc;
	}

	public long getClientSessionKey() {
		return clientSessionKey;
	}

	public long getServerSessionKey() {
		return serverSessionKey;
	}

	public Country getCountry() {
		return country;
	}
}
