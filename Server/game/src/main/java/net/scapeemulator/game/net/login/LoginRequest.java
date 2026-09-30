package net.scapeemulator.game.net.login;

import net.scapeemulator.game.model.hud.DisplayMode;

public final class LoginRequest {
	private final boolean reconnecting;
	private final String username, password;
	private final long clientSessionKey, serverSessionKey;
	private final int[] crc;
	private final DisplayMode displayMode;
	private final int version;
	private final int seqNum;

	public LoginRequest(boolean reconnecting, String username, String password, long clientSessionKey,
			long serverSessionKey, int version, int seqNum, int[] crc, DisplayMode displayMode) {
		this.reconnecting = reconnecting;
		this.username = username;
		this.password = password;
		this.clientSessionKey = clientSessionKey;
		this.serverSessionKey = serverSessionKey;
		this.version = version;
		this.seqNum = seqNum;
		this.crc = crc;
		this.displayMode = displayMode;
	}

	public boolean isReconnecting() {
		return reconnecting;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public long getClientSessionKey() {
		return clientSessionKey;
	}

	public long getServerSessionKey() {
		return serverSessionKey;
	}

	public int getVersion() {
		return version;
	}

	public int[] getCrc() {
		return crc;
	}

	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	public int getSeqNum() {
		return seqNum;
	}
}
