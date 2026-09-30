package net.scapeemulator.game.model.player.combat;

import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.model.mob.SpotAnimation;
import net.scapeemulator.game.model.mob.combat.SpecialAttack;
import net.scapeemulator.game.model.player.Player;

/**
 * Special attack example
 * 
 * @author Teemu
 *
 */
public class EnergyDrain extends SpecialAttack {
	private final Animation special_anim = new Animation(11969);
	private final SpotAnimation drain = new SpotAnimation(2108, 0, 100);

	@Override
	public void afterAttack(Mob attacker, Mob victim, int damage) {
		if (victim.getClass() == Player.class) {
			Player pVict = (Player) victim;
			/*
			 * According to RSWiki historic pages, transfer is 10-35%
			 */
			int energy = pVict.getEnergy();
			int drain = (int) (energy * (0.1 + (0.25 * Math.random())));
			pVict.setEnergy(energy - drain);
			pVict.playSpotAnimation(this.drain);
			if (attacker.getClass() == Player.class) {
				Player pAtt = (Player) attacker;
				pAtt.setEnergy(pAtt.getEnergy() + drain);
			}
		}
	}

	@Override
	public Animation getAttackAnimation() {
		return special_anim;
	}

	@Override
	public double getMaxHit(double maxhit_raw) {
		return maxhit_raw;
	}

	@Override
	public double modAccuracy(double accuracy_raw) {
		return accuracy_raw * 1.1;
	}

	@Override
	public int getDrain() {
		return 500;
	}

	@Override
	public SpecialAttack nextState() {
		return null;
	}

	@Override
	public int getCombatDelay() {
		return 4;
	}
}
