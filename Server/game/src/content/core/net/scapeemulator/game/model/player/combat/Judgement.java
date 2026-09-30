package net.scapeemulator.game.model.player.combat;

import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.SpotAnimation;

/**
 * Special attack example
 * 
 * @author Teemu
 *
 */
public class Judgement extends Godsword {

	public Judgement() {
		super(new Animation(11989), new SpotAnimation(2113));
	}

	@Override
	public double getMaxHit(double maxhit_raw) {
		return maxhit_raw * 1.375;
	}
}
