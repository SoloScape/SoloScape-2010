package net.scapeemulator.game.update;

import java.util.ArrayList;
import java.util.List;

import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.update.chunk.ChunkUpdater;
import net.scapeemulator.game.update.npc.NpcUpdater;
import net.scapeemulator.game.update.player.PlayerUpdater;

public class UpdateCore {
	private final List<EntityUpdater> updaters = new ArrayList<>();
	private final World world;

	public UpdateCore(World world) {
		this.world = world;
		updaters.add(new PlayerUpdater(world.getPlayers()));
		updaters.add(new NpcUpdater(world.getNpcs()));
		updaters.add(new ChunkUpdater());
		// NPCS
		// Objects
		// Items
	}

	public void tick() {
		for (EntityUpdater updater : updaters)
			updater.preProcess();
		for (Player player : world.getPlayers()) {
			for (EntityUpdater updater : updaters) {
				updater.process(player);
			}
		}
		for (EntityUpdater updater : updaters)
			updater.postProcess();
	}
}
