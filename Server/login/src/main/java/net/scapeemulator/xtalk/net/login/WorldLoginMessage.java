package net.scapeemulator.xtalk.net.login;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WorldLoginMessage {
	public static final int STATUS_EXCHANGE_KEYS = 0;
	public static final int KEY_MISMATCH = 1;
	public static final int SUCCESS = 2;
	public static final int WORLD_ALREADY_ONLINE = 5;
	public static final int INVALID_VERSION = 6;
	public static final int INVALID_WORLD = 20;

	private final int status;
	private final ByteBuf payload;

	public WorldLoginMessage(int status) {
		this.status = status;
		this.payload = Unpooled.EMPTY_BUFFER;
	}

	public WorldLoginMessage(int status, ByteBuf payload) {
		this.status = status;
		this.payload = payload;
	}

	public int getStatus() {
		return status;
	}

	public ByteBuf getPayload() {
		return payload;
	}
}
