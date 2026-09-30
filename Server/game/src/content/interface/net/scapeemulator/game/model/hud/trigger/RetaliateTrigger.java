package net.scapeemulator.game.model.hud.trigger;

import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.var.Trigger;

public class RetaliateTrigger extends Trigger<Boolean> {

	@Override
	public void trigger(Player player, Boolean retaliate) {
		player.getCombat().setRetaliate(retaliate);
	}
}
