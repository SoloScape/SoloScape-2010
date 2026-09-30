package net.scapeemulator.js5;

public final class UpdateEncryptionMessage {

	private final int key;

	public UpdateEncryptionMessage(int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}

}
