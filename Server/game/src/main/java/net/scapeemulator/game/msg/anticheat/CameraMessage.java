package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.Message;

public final class CameraMessage implements Message {

	private final int yaw, pitch;

	public CameraMessage(int yaw, int pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public int getYaw() {
		return yaw;
	}

	public int getPitch() {
		return pitch;
	}

}
