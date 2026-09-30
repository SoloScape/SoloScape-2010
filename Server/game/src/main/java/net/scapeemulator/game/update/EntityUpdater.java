package net.scapeemulator.game.update;

import net.scapeemulator.game.model.player.Player;

public abstract class EntityUpdater {
	public abstract void preProcess();

	public abstract void process(Player player);

	public abstract void postProcess();
}
