package net.scapeemulator.game.net.register;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import net.scapeemulator.api.Service;
import net.scapeemulator.api.Session;

import java.io.IOException;

public final class RegisterSession extends Session<Service> {

	public RegisterSession(Service service, Channel channel) {
		super(service, channel);
	}

	@Override
	public void messageReceived(Object message) throws IOException {
		channel.write(new RegisterResponse(RegisterResponse.STATUS_OK)).addListener(ChannelFutureListener.CLOSE);
	}

}
