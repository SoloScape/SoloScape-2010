package net.scapeemulator.game.model.player.combat;

import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.model.mob.SpotAnimation;

public class HealingBlade extends Godsword {

	public HealingBlade() {
		super(new Animation(12019), new SpotAnimation(2109));
	}

	@Override
	public void afterAttack(Mob attacker, Mob victim, int damage) {
		if (damage != 0) {
			int hitpoints = (int) (damage * 0.5), prayerPoints = (int) (damage * 0.25);
			if (damage < 20) {
				hitpoints = 10;
				prayerPoints = 5;
			}
			attacker.getSkillSet().restore(3, hitpoints, false);
			attacker.getSkillSet().restore(5, prayerPoints, false);
		}
	}
}
