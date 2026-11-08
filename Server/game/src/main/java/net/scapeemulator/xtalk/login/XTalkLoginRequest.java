package net.scapeemulator.xtalk.login;

import net.scapeemulator.game.conf.WorldConfiguration;
import net.scapeemulator.js5.UpdateService;
import net.scapeemulator.worldlist.Country;

public class XTalkLoginRequest extends XTalkMessage {
	private final int worldId, version;
	private final UpdateService js5;
	private final long clientSessionKey, serverSessionKey;
	private final WorldConfiguration worldConfiguration;

	public XTalkLoginRequest(int worldId, int version, UpdateService js5, long clientSessionKey, long serverSessionKey,
			WorldConfiguration worldConfiguration) {
		this.worldId = worldId;
		this.version = version;
		this.js5 = js5;
		this.clientSessionKey = clientSessionKey;
		this.serverSessionKey = serverSessionKey;
		this.worldConfiguration = worldConfiguration;
	}

	public int getWorldId() {
		return worldId;
	}

	public int getVersion() {
		return version;
	}

	public UpdateService getJs5() {
		return js5;
	}

	public long getClientSessionKey() {
		return clientSessionKey;
	}

	public long getServerSessionKey() {
		return serverSessionKey;
	}

	public Country getCountry() {
		return worldConfiguration.getCountry();
	}

	public int getFlags() {
		return worldConfiguration.getFlags();
	}

	public String getActivity() {
		return worldConfiguration.getActivity();
	}
}
