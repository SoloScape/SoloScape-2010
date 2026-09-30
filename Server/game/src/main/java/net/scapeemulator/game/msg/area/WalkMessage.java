package net.scapeemulator.game.msg.area;

import net.scapeemulator.api.message.Message;

public final class WalkMessage implements Message {
	public static final int HUD_WALK = 5, MINIMAP_WALK = 59;
	private final boolean running, minimap;
	private final int x, y;

	// public WalkMessage(Step[] steps, boolean running) {
	// this.steps = steps;
	// this.running = running;
	// }

	public WalkMessage(int x, int y, boolean running, boolean minimap) {
		this.x = x;
		this.y = y;
		this.running = running;
		this.minimap = minimap;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isMinimap() {
		return minimap;
	}
}
