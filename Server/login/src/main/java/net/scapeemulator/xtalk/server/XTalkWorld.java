package net.scapeemulator.xtalk.server;

import net.scapeemulator.worldlist.OnlineWorld;
import net.scapeemulator.xtalk.net.gameserver.GameServerSession;

public class XTalkWorld extends OnlineWorld {
	private final GameServerSession session;

	public XTalkWorld(GameServerSession session, int id, int flags, int country, String activity, String ip) {
		super(id, flags, country, activity, ip);
		this.session = session;
	}

	public void setPlayers(int players) {
		super.setPlayers(players);
		session.getService().refreshPlayerCounts();
	}

	public GameServerSession getSession() {
		return session;
	}
}
