package net.scapeemulator.game.model.player.var;

import net.scapeemulator.game.model.player.Player;

public abstract class Trigger<T> {

	@SuppressWarnings("unchecked")
	public final void stateChanged(Player player, Object a) {
		try {
			trigger(player, (T) a);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract void trigger(Player player, T current);
}
