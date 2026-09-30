package net.scapeemulator.game.model;

import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.game.conf.WorldConfiguration;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.mob.MobList;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.net.game.GameSession;
import net.scapeemulator.game.task.TaskScheduler;
import net.scapeemulator.game.update.UpdateCore;

public final class World {
	public static final int MAX_PLAYERS = 2048;
	private static World world;

	public static World getWorld(WorldConfiguration worldConfiguration) {
		if (world == null)
			world = new World(worldConfiguration);
		return world;
	}

	public static World getWorld() {
		if (world == null) {
			throw new NullPointerException("World is uninitialized!");
		}
		return world;
	}

	private final MobList<Player> players = new MobList<>(MAX_PLAYERS);
	private final MobList<Npc> npcs = new MobList<>(32000);
	private final TaskScheduler taskScheduler = new TaskScheduler();
	private final UpdateCore updater = new UpdateCore(this);
	private final WorldConfiguration configuration;

	private World(WorldConfiguration configuration) {
		this.configuration = configuration;
		Npc npc = new Npc(8349);
		npc.setPosition(new Position(3192, 3399));
		npcs.add(npc);
	}

	public MobList<Player> getPlayers() {
		return players;
	}

	public MobList<Npc> getNpcs() {
		return npcs;
	}

	public TaskScheduler getTaskScheduler() {
		return taskScheduler;
	}

	public boolean members() {
		return configuration.isMembers();
	}

	public void tick() {
		for (Player player : players) {
			GameSession session = player.getSession();
			if (session != null)
				session.processMessageQueue();
		}

		taskScheduler.tick();
		updater.tick();
		players.clean();
	}

	public Player getPlayerByName(String username) {
		for (Player player : players) {
			if (player.getUsername().equals(username))
				return player;
		}
		return null;
	}

	public Map<Integer, Position> buildPosBlock() {
		Map<Integer, Position> posBlock = new HashMap<>();
		for (Player player : players) {
			posBlock.put(player.getId(), player.getPosition());
		}
		return posBlock;
	}
}
