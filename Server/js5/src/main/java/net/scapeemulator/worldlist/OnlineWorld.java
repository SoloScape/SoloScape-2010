package net.scapeemulator.worldlist;

public class OnlineWorld extends World {
	private int players;

	public OnlineWorld(int id, int flags, int country, String activity, String ip) {
		super(id, flags, country, activity, ip);
	}

	public int getPlayers() {
		return players;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
