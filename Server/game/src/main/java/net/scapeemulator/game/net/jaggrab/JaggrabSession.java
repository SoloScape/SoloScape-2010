package net.scapeemulator.game.net.jaggrab;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.FileRegion;
import net.scapeemulator.api.Session;
import net.scapeemulator.game.net.file.FileProvider;

import java.io.IOException;

public final class JaggrabSession extends Session<FileProvider> {

	public JaggrabSession(Channel channel) {
		super(new FileProvider(true), channel);
	}

	@Override
	public void messageReceived(Object message) throws IOException {
		JaggrabRequest request = (JaggrabRequest) message;
		FileRegion file = service.serve(request.getPath());
		if (file != null) {
			channel.write(file).addListener(ChannelFutureListener.CLOSE);
		} else {
			channel.close();
		}
	}

}
