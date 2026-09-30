package net.scapeemulator.game.net.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.burtleburtle.bob.rand.IsaacRandom;
import net.scapeemulator.api.Session;
import net.scapeemulator.api.message.codec.impl.IncomingMessageDecoder;
import net.scapeemulator.api.message.codec.impl.OutgoingMessageEncoder;
import net.scapeemulator.api.message.codec.impl.PacketDecoder;
import net.scapeemulator.api.message.codec.impl.PacketEncoder;
import net.scapeemulator.cache.ChecksumTable;
import net.scapeemulator.game.GameServer;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.Messages;
import net.scapeemulator.game.net.RsChannelHandler;
import net.scapeemulator.game.net.game.*;

import java.io.IOException;
import java.security.SecureRandom;

public final class LoginSession extends Session<LoginService> {
	private static final SecureRandom random = new SecureRandom();

	private final long serverSessionKey = random.nextLong();
	private DisplayMode displayMode;
	private IsaacRandom inRandom, outRandom;

	private final GameServer server;

	public LoginSession(GameServer server, Channel channel) {
		super(server.getLoginService(), channel);
		this.server = server;
		init();
	}

	private void init() {
		ByteBuf buf = channel.alloc().buffer(8);
		buf.writeLong(serverSessionKey);
		channel.write(new LoginResponse(LoginResponse.STATUS_EXCHANGE_KEYS, buf));
	}

	public void sendLoginFailure(int status) {
		LoginResponse response = new LoginResponse(status);
		channel.write(response).addListener(ChannelFutureListener.CLOSE);
	}

	public void sendLoginSuccess(int status, Player player) {
		ByteBuf buf = channel.alloc().buffer(10);
		buf.writeByte(player.getRights());
		buf.writeByte(0);
		buf.writeByte(0);
		buf.writeByte(0);
		buf.writeByte(0);
		buf.writeByte(0);
		buf.writeShort(player.getId());
		buf.writeByte(1);// Member
		buf.writeByte(server.getWorldConfiguration().isMembers() ? 1 : 0);

		ChannelPipeline pipeline = channel.pipeline();

		GameSession session = new GameSession(server, channel, player);
		RsChannelHandler handler = pipeline.get(RsChannelHandler.class);
		handler.setSession(session);

		// TODO a different timeout mechanism is used in-game
		pipeline.remove(ReadTimeoutHandler.class);

		LoginResponse response = new LoginResponse(status, buf);
		channel.write(response);

		pipeline.addFirst(new PacketEncoder(outRandom), new OutgoingMessageEncoder(server.getCodecRepository()),
				new PacketDecoder(inRandom, Messages.SIZES), new IncomingMessageDecoder(server.getCodecRepository()));
		session.init(displayMode);
	}

	@Override
	public void messageReceived(Object message) throws IOException {
		LoginRequest request = (LoginRequest) message;
		if (request.getServerSessionKey() != serverSessionKey)
			throw new IOException("Server session key mismatch.");

		boolean versionMismatch = false;
		if (request.getVersion() != service.getVersion())
			versionMismatch = true;

		ChecksumTable table = server.getUpdateService().getChecksumTable();
		int[] crc = request.getCrc();
		for (int i = 0; i < crc.length; i++) {
			if (table.getEntry(i).getCrc() != crc[i]) {
				versionMismatch = true;
				break;
			}
		}

		if (versionMismatch) {
			sendLoginFailure(LoginResponse.STATUS_GAME_UPDATED);
			return;
		}

		long clientSessionKey = request.getClientSessionKey();
		long serverSessionKey = request.getServerSessionKey();
		int[] seed = new int[4];
		seed[0] = (int) (clientSessionKey >> 32);
		seed[1] = (int) clientSessionKey;
		seed[2] = (int) (serverSessionKey >> 32);
		seed[3] = (int) serverSessionKey;

		inRandom = new IsaacRandom(seed);
		for (int i = 0; i < seed.length; i++)
			seed[i] += 50;
		outRandom = new IsaacRandom(seed);
		displayMode = request.getDisplayMode();

		service.addLoginRequest(this, request);
	}

}
