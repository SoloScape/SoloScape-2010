package net.scapeemulator.xtalk.net;

import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.handler.MessageDispatcher;
import net.scapeemulator.xtalk.client.CrosstalkClient;
import net.scapeemulator.xtalk.net.message.XCountryTypeRefreshMessage;
import net.scapeemulator.xtalk.net.message.XPingMessage;
import net.scapeemulator.xtalk.net.message.XWorldRefreshMessage;

public class XTalkMessages {
	public static final int[] SIZES = new int[256];

	public static void init(CodecRepository repo, MessageDispatcher<CrosstalkClient> dispatcher) {
		sizes();
		init(repo);
		init(dispatcher);
	}

	private static void sizes() {
		for (int i = 0; i < SIZES.length; i++) {
			SIZES[i] = -3;
		}
		SIZES[XPingMessage.OPCODE] = 0;
		SIZES[XWorldRefreshMessage.OPCODE] = -2;
		SIZES[XCountryTypeRefreshMessage.OPCODE] = -2;
	}

	private static void init(CodecRepository repo) {
		repo.bind(new XPingMessage.Decoder());
		repo.bind(new XPingMessage.Encoder());

		repo.bind(new XWorldRefreshMessage.Decoder());
		repo.bind(new XCountryTypeRefreshMessage.Decoder());
	}

	private static void init(MessageDispatcher<CrosstalkClient> dsp) {
		dsp.bind(XPingMessage.class, new XPingMessage.Handler());
		dsp.bind(XWorldRefreshMessage.class, new XWorldRefreshMessage.Handler());
		dsp.bind(XCountryTypeRefreshMessage.class, new XCountryTypeRefreshMessage.Handler());
	}
}
