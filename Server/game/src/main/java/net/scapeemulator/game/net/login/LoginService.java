package net.scapeemulator.game.net.login;

import net.scapeemulator.api.Job;
import net.scapeemulator.api.Service;
import net.scapeemulator.game.io.PlayerSerializer;
import net.scapeemulator.game.io.PlayerSerializer.SerializeResult;
import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.mob.MobList;
import net.scapeemulator.game.model.player.Player;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public final class LoginService implements Service, Runnable {

	private static class LoginJob extends Job<LoginService> {

		private final LoginSession session;
		private final LoginRequest request;

		public LoginJob(LoginSession session, LoginRequest request) {
			this.session = session;
			this.request = request;
		}

		@Override
		public void perform(LoginService service) {
			SerializeResult result = service.serializer.load(request.getUsername(), request.getPassword());
			int status = result.getStatus();

			if (status != LoginResponse.STATUS_OK) {
				/* send failure response immediately */
				session.sendLoginFailure(status);
			} else {
				/* add to new player queue */
				synchronized (service.newPlayers) {
					service.newPlayers.add(new SessionPlayerPair(session, result.getPlayer()));
				}
			}
		}

	}

	private static class LogoutJob extends Job<LoginService> {

		private final Player player;

		public LogoutJob(Player player) {
			this.player = player;
		}

		@Override
		public void perform(LoginService service) {
			service.serializer.save(player);
		}
	}

	private static class SessionPlayerPair {

		private final LoginSession session;
		private final Player player;

		public SessionPlayerPair(LoginSession session, Player player) {
			this.session = session;
			this.player = player;
		}

	}

	private final PlayerSerializer serializer;
	private final BlockingQueue<Job<LoginService>> jobs = new LinkedBlockingDeque<>();
	private final Queue<SessionPlayerPair> newPlayers = new ArrayDeque<>();
	private final Queue<Player> oldPlayers = new ArrayDeque<>();
	private final int version;

	public LoginService(PlayerSerializer serializer, int version) {
		this.serializer = serializer;
		this.version = version;
	}

	public void addLoginRequest(LoginSession session, LoginRequest request) {
		jobs.add(new LoginJob(session, request));
	}

	public void addLogoutRequest(Player player) {
		synchronized (oldPlayers) {
			oldPlayers.add(player);
		}
	}

	public void registerNewPlayers(World world) {
		MobList<Player> players = world.getPlayers();

		synchronized (oldPlayers) {
			Player player;
			while ((player = oldPlayers.poll()) != null) {
				player.unlist();
				jobs.add(new LogoutJob(player));
			}
		}

		synchronized (newPlayers) {
			SessionPlayerPair pair;
			while ((pair = newPlayers.poll()) != null) {
				if (world.getPlayerByName(pair.player.getUsername()) != null) {
					/* player is already online */
					pair.session.sendLoginFailure(LoginResponse.STATUS_ALREADY_ONLINE);
				} else if (!players.add(pair.player)) {
					/* world is full */
					pair.session.sendLoginFailure(LoginResponse.STATUS_WORLD_FULL);
				} else {
					/* send success packet & switch to GameSession */
					pair.session.sendLoginSuccess(LoginResponse.STATUS_OK, pair.player);
				}
			}
		}
	}

	@Override
	public void run() {
		for (;;) {
			try {
				jobs.take().perform(this);
			} catch (InterruptedException e) {
				/* ignore */
			}
		}
	}

	public int getVersion() {
		return version;
	}
}
