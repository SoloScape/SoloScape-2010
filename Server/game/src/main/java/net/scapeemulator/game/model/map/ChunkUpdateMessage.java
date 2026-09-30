package net.scapeemulator.game.model.map;

import java.util.List;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.game.msg.chunk.ChunkMessage;

public class ChunkUpdateMessage implements Message {
	private final List<ChunkMessage> packets;
	private final int x, y, height;

	public ChunkUpdateMessage(int height, int x, int y, List<ChunkMessage> packets) {
		this.height = height;
		this.x = x;
		this.y = y;
		this.packets = packets;
	}

	public List<ChunkMessage> getPackets() {
		return packets;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getHeight() {
		return height;
	}
}