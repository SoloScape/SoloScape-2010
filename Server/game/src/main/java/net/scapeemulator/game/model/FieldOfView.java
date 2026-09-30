package net.scapeemulator.game.model;

public enum FieldOfView {
	REGULAR(104), MEDIUM(120), BIG(136), LARGE(168);
	private final int tiles, chunks;

	private FieldOfView(int tiles) {
		this.tiles = tiles;
		chunks = tiles >> 3;
	}

	public int getChunks() {
		return chunks;
	}

	public int getTiles() {
		return tiles;
	}
}
