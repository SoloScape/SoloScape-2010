package net.scapeemulator.xtalk.client.login;

public class ServerSessionKeyResult {
	private final int status;
	private final long serverSessionKey;

	public ServerSessionKeyResult(int status, long serverSessionKey) {
		this.status = status;
		this.serverSessionKey = serverSessionKey;
	}

	public int getStatus() {
		return status;
	}

	public long getServerSessionKey() {
		return serverSessionKey;
	}
}
