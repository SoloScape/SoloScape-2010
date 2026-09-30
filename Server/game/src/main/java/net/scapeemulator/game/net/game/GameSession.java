package net.scapeemulator.game.net.game;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.scapeemulator.api.Session;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.handler.MessageDispatcher;
import net.scapeemulator.game.GameServer;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.area.StaticAreaMessage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public final class GameSession extends Session<GameServer> {

	private final Player player;
	private final MessageDispatcher<Player> dispatcher;
	private final Queue<Message> messages = new ArrayDeque<>();

	public GameSession(GameServer server, Channel channel, Player player) {
		super(server, channel);
		this.player = player;
		this.dispatcher = server.getMessageDispatcher();
	}

	public void init(DisplayMode displayMode) {
		player.setSession(this);

		/* set up player for their initial region change */
		Position position = player.getPosition();
		player.setLastKnownRegion(position);
		player.getLocalPlayers().add(player);
		player.send(new StaticAreaMessage(player.getPosition(), player.getFov(), player.getId(),
				service.getWorld().buildPosBlock(), true));
		// TODO: wtf
		player.sendMessage("Welcome to RuneScape.");
		player.getDisplay().initialize(displayMode);
		/* refresh skills, energy, etc. */
		player.getSkillSet().refresh();
		player.setEnergy(player.getEnergy()); // TODO: nicer way than this?
	}

	@Override
	public void messageReceived(Object message) throws IOException {
		synchronized (messages) {
			messages.add((Message) message);
		}
	}

	@Override
	public void channelClosed() {
		service.getLoginService().addLogoutRequest(player);
	}

	public ChannelFuture send(Message message) {
		return channel.write(message);
	}

	public void processMessageQueue() {
		synchronized (messages) {
			Message message;
			while ((message = messages.poll()) != null)
				dispatcher.dispatch(player, message);
		}
	}
}
