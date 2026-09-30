package net.scapeemulator.xtalk.net.autoworld;

import java.io.IOException;

import io.netty.channel.Channel;
import net.scapeemulator.api.Session;
import net.scapeemulator.xtalk.server.GameServerService;

public class AutoWorldSession extends Session<GameServerService> {

	public AutoWorldSession(GameServerService service, Channel channel) {
		super(service, channel);
	}

	@Override
	public void messageReceived(Object message) throws IOException {
		if (message instanceof AutoWorldRequest) {
			AutoWorldRequest request = (AutoWorldRequest) message;
			if (request.getVersion() != service.getVersion()) {
				sendResponse(new AutoWorldResponse(AutoWorldResponse.STATUS_GAME_UPDATED));
				return;
			}
			// TODO: Match credentials
			service.matchWorld(this, request);
		}
	}

	public void sendResponse(AutoWorldResponse response) {
		channel.write(response);
		channel.flush();
	}

	public Channel channel() {
		return channel;
	}
}
