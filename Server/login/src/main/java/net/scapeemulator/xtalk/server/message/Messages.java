package net.scapeemulator.xtalk.server.message;

import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.handler.MessageDispatcher;
import net.scapeemulator.xtalk.net.gameserver.GameServerSession;
import net.scapeemulator.xtalk.server.message.world.*;

public class Messages {
	public static final int[] SIZES = new int[256];

	static {
		for (int i = 0; i < SIZES.length; i++) {
			SIZES[i] = -3;
		}
		SIZES[XPingMessage.OPCODE] = 0;
		SIZES[PlayerCountRefreshMessage.OPCODE] = 2;
	}

	public static void set(CodecRepository repo, MessageDispatcher<GameServerSession> dispatcher) {
		set(repo);
		set(dispatcher);
	}

	private static void set(MessageDispatcher<GameServerSession> dispatcher) {
		dispatcher.bind(XPingMessage.class, new XPingMessage.Handler());
		dispatcher.bind(PlayerCountRefreshMessage.class, new PlayerCountRefreshMessage.Handler());
	}

	private static void set(CodecRepository repo) {
		repo.bind(new XPingMessage.Decoder());
		repo.bind(new XPingMessage.Encoder());

		repo.bind(new XWorldRefreshMessage.Encoder());
		repo.bind(new XCountryTypeRefreshMessage.Encoder());
		
		repo.bind(new PlayerCountRefreshMessage.Decoder());
		repo.bind(new PlayerCountRefreshMessage.Encoder());
	}
}
