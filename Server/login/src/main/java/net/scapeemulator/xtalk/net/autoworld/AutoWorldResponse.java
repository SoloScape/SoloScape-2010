package net.scapeemulator.xtalk.net.autoworld;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class AutoWorldResponse {
	private final int status;
	private final ByteBuf payload;

	public AutoWorldResponse(int status) {
		this.status = status;
		this.payload = Unpooled.EMPTY_BUFFER;
	}

	public AutoWorldResponse(int status, ByteBuf payload) {
		this.status = status;
		this.payload = payload;
	}

	public int getStatus() {
		return status;
	}

	public ByteBuf getPayload() {
		return payload;
	}

	public static final int STATUS_OK = 2;
	public static final int STATUS_INVALID_PASSWORD = 3;
	public static final int STATUS_BANNED = 4;
	public static final int STATUS_ALREADY_ONLINE = 5;
	public static final int STATUS_GAME_UPDATED = 6;
	public static final int STATUS_WORLD_FULL = 7;
	public static final int STATUS_WORLD_MEMBERS = 12;
	public static final int STATUS_COULD_NOT_COMPLETE = 13;
	public static final int STATUS_UPDATE_IN_PROGRESS = 14;
	public static final int STATUS_TOO_MANY_FAILED_LOGINS = 16;
	public static final int STATUS_ACCOUNT_LOCKED = 18;
	public static final int STATUS_RETRY = 21;
	public static final int STATUS_MALFORMED_PACKET = 22;
	public static final int STATUS_ERROR_LOADING_PROFILE = 24;
	public static final int STATUS_IP_BANNED = 26;
	public static final int STATUS_SWITCH_WORLD_AND_RETRY = 101;
}
