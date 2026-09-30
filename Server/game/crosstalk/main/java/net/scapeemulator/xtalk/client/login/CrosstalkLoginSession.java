package net.scapeemulator.xtalk.client.login;

import java.io.IOException;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.scapeemulator.api.crypto.StreamCipher;
import net.scapeemulator.api.message.codec.impl.IncomingMessageDecoder;
import net.scapeemulator.api.message.codec.impl.OutgoingMessageEncoder;
import net.scapeemulator.api.message.codec.impl.PacketDecoder;
import net.scapeemulator.api.message.codec.impl.PacketEncoder;
import net.scapeemulator.game.GameServer;
import net.scapeemulator.util.crypto.ZeroStreamCipher;

import net.scapeemulator.xtalk.AbstractCrosstalkSession;
import net.scapeemulator.xtalk.ServerLoginResult;
import net.scapeemulator.xtalk.client.CrosstalkChannelHandler;
import net.scapeemulator.xtalk.client.CrosstalkClient;
import net.scapeemulator.xtalk.client.CrosstalkSession;
import net.scapeemulator.xtalk.login.XTalkLoginHandshake;
import net.scapeemulator.xtalk.login.XTalkLoginRequest;
import net.scapeemulator.xtalk.net.XTalkMessages;

public class CrosstalkLoginSession extends AbstractCrosstalkSession<CrosstalkClient> {
	private static Logger logger = LoggerFactory.getLogger(CrosstalkLoginSession.class);
	private static final SecureRandom random = new SecureRandom();
	private final GameServer gameServer;

	private WorldLoginState state = WorldLoginState.EXCHANGE_KEYS;
	private long serverSessionKey, clientSessionKey = random.nextLong();
	private StreamCipher inRandom, outRandom;

	public CrosstalkLoginSession(CrosstalkClient service, GameServer gameServer, Channel channel) {
		super(service, channel);
		this.gameServer = gameServer;
	}

	@Override
	public void messageReceived(Object message) throws IOException {
		if (message instanceof ServerLoginResult) {
			ServerLoginResult result = (ServerLoginResult) message;
			int resultCode = result.getResult();
			String error = "";
			switch (resultCode) {
			case ServerLoginResult.INVALID_VERSION:
				error = "Invalid Version specified. (Game files/Client version.)";
				break;
			case ServerLoginResult.INVALID_WORLD:
				error = "Invalid World specified.";
				break;
			case ServerLoginResult.KEY_MISMATCH:
				error = "Server session key mismatch.";
				break;
			case ServerLoginResult.WORLD_ALREADY_ONLINE:
				error = "World already online.";
				break;
			case ServerLoginResult.SUCCESS:
				logger.info("Succesful XTalk login!");
				int[] seed = new int[4];
				seed[0] = (int) (clientSessionKey >> 32);
				seed[1] = (int) clientSessionKey;
				seed[2] = (int) (serverSessionKey >> 32);
				seed[3] = (int) serverSessionKey;

				outRandom = new ZeroStreamCipher(); // new IsaacRandom(seed);
				for (int i = 0; i < seed.length; i++)
					seed[i] += 50;
				inRandom = new ZeroStreamCipher();// new IsaacRandom(seed);
				ChannelPipeline pipeline = channel.pipeline();

				CrosstalkSession xSession = new CrosstalkSession(service, channel);
				service.getGameServer().getWorldListService().setCountries(result.getCountries());
				CrosstalkChannelHandler handler = pipeline.get(CrosstalkChannelHandler.class);
				handler.setSession(xSession);

				pipeline.addFirst(new PacketEncoder(outRandom), new OutgoingMessageEncoder(service.getRepo()),
						new PacketDecoder(inRandom, XTalkMessages.SIZES),
						new IncomingMessageDecoder(service.getRepo()));

				return;
			}
			logger.warn(error);
			channel.close();
		} else if (message instanceof ServerSessionKeyResult) {
			if (((ServerSessionKeyResult) message).getStatus() == 0) {
				serverSessionKey = ((ServerSessionKeyResult) message).getServerSessionKey();
				state = WorldLoginState.LOGIN;
				channel.write(new XTalkLoginRequest(service.getWorldId(), service.getVersion(), service.getJs5(),
						clientSessionKey, serverSessionKey, gameServer.getWorldConfiguration()), channel.voidPromise());
			} else {
				channel.close();
			}
		}
	}

	public CrosstalkLoginSession requestLogin() {
		channel.write(new XTalkLoginHandshake(), channel.voidPromise());
		return this;
	}

	public WorldLoginState getState() {
		return state;
	}
}
