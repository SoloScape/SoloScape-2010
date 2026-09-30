package net.scapeemulator.game.msg.audio;

import net.scapeemulator.api.message.Message;

public class SoundEffect implements Message {
	private final int id, repeats, delay, volume;

	public SoundEffect(int id) {
		this(id, 1, 0, 255);
	}

	public SoundEffect(int id, int delay) {
		this(id, 1, delay, 255);
	}

	public SoundEffect(int id, int repeats, int delay) {
		this(id, repeats, delay, 255);
	}

	public SoundEffect(int id, int repeats, int delay, int volume) {
		this.id = id;
		this.repeats = repeats;
		this.delay = delay;
		this.volume = volume;
	}

	public int getId() {
		return id;
	}

	public int getRepeats() {
		return repeats;
	}

	public int getDelay() {
		return delay;
	}

	// Speculating this to be volume.
	public int getVolume() {
		return volume;
	}
}
