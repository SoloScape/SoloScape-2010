package net.scapeemulator.xtalk.net.autoworld;

public class AutoWorldRequest {
	private final String username, password;
	private final int version;

	public AutoWorldRequest(int version, String username, String password) {
		this.version = version;
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getVersion() {
		return version;
	}

}
