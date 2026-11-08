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
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoginService implements Service, Runnable {
	private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

	private static class LoginJob extends Job<LoginService> {

		private final LoginSession session;
		private final LoginRequest request;

		public LoginJob(LoginSession session, LoginRequest request) {
			this.session = session;
			this.request = request;
		}

		@Override
		public void perform(LoginService service) {
			String account = accountKey(request.getUsername());
			if (!service.activeAccounts.add(account)) {
				session.sendLoginFailure(LoginResponse.STATUS_ALREADY_ONLINE);
				return;
			}
			boolean queued = false;
			try {
				SerializeResult result = service.serializer.load(request.getUsername(), request.getPassword());
				int status = result.getStatus();

				if (status != LoginResponse.STATUS_OK) {
					/* send failure response immediately */
					session.sendLoginFailure(status);
				} else {
					/* add to new player queue */
					synchronized (service.newPlayers) {
						service.newPlayers.add(new SessionPlayerPair(session, result.getPlayer()));
						queued = true;
					}
				}
			} finally {
				if (!queued) service.activeAccounts.remove(account);
			}
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
	private final Set<String> activeAccounts = ConcurrentHashMap.newKeySet();
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
			if (!oldPlayers.contains(player)) oldPlayers.add(player);
		}
	}

	public void registerNewPlayers(World world) {
		MobList<Player> players = world.getPlayers();

		synchronized (oldPlayers) {
			for (int remaining = oldPlayers.size(); remaining > 0; remaining--) {
				Player player = oldPlayers.poll();
				if (!player.isListed()) continue;
				try {
					// Freeze on the game thread and finish the save before allowing a reconnect.
					player.stopAction();
					player.getMovement().reset();
					serializer.save(player);
					players.remove(player);
					activeAccounts.remove(accountKey(player.getUsername()));
				} catch (RuntimeException ex) {
					logger.error("Logout save failed for {}. Will retry; character remains reserved.", player.getUsername(), ex);
					oldPlayers.add(player);
				}
			}
		}

		synchronized (newPlayers) {
			SessionPlayerPair pair;
			while ((pair = newPlayers.poll()) != null) {
				if (!pair.session.isConnected()) {
					activeAccounts.remove(accountKey(pair.player.getUsername()));
				} else if (world.getPlayerByName(pair.player.getUsername()) != null) {
					/* player is already online */
					pair.session.sendLoginFailure(LoginResponse.STATUS_ALREADY_ONLINE);
					activeAccounts.remove(accountKey(pair.player.getUsername()));
				} else if (!players.add(pair.player)) {
					/* world is full */
					pair.session.sendLoginFailure(LoginResponse.STATUS_WORLD_FULL);
					activeAccounts.remove(accountKey(pair.player.getUsername()));
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
			} catch (RuntimeException e) {
				logger.error("Login request failed.", e);
			}
		}
	}

	public int getVersion() {
		return version;
	}

	/** Called by the shutdown hook under the same lock as the main game tick. */
	public void saveAllPlayers(World world) {
		synchronized (world) {
			for (Player player : world.getPlayers()) {
				try {
					player.stopAction();
					player.getMovement().reset();
					serializer.save(player);
				} catch (RuntimeException ex) {
					logger.error("Shutdown save failed for {}.", player.getUsername(), ex);
				}
			}
		}
	}

	private static String accountKey(String username) {
		return username.toLowerCase(Locale.ROOT).replace(' ', '_');
	}
}
