package net.scapeemulator.xtalk.net.handshake;

public final class HandshakeMessage {

	public static final int SERVICE_LOGIN = 14;
	public static final int SERVICE_UPDATE = 15;
	public static final int SERVICE_JAGGRAB = 17;
	public static final int SERVICE_REGISTER_PERSONAL_DETAILS = 147;
	public static final int SERVICE_REGISTER_USERNAME = 186;
	public static final int SERVICE_REGISTER_COMMIT = 36;
	public static final int SERVICE_WORLD_LIST = 23;
	public static final int SERVICE_AUTO_LOGIN = 24;
	
	public static final int SERVICE_WORLD_LOGIN = 128;// Woo

	private final int service;

	public HandshakeMessage(int service) {
		this.service = service;
	}

	public int getService() {
		return service;
	}

}
