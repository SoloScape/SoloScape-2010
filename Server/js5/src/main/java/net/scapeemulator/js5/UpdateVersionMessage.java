package net.scapeemulator.js5;

public final class UpdateVersionMessage {

	private final int version;

	public UpdateVersionMessage(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

}
