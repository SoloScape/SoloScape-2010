package net.scapeemulator.game.model.hud.trigger;

import net.scapeemulator.game.model.mob.combat.SpecialAttack;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.var.Trigger;

public class SpecialTrigger extends Trigger<Boolean> {

	@Override
	public void trigger(Player player, Boolean enabled) {
		if (enabled) {
			if (player.getCombat().getSpecialAttack() == null) {
				SpecialAttack spec = SpecialAttack.get(player);
				if (spec != null)
					spec.init(player);
				player.getCombat().setSpecialAttack(spec);
			}
		}
	}
}
