package net.scapeemulator.game.model.player.combat;

import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.model.mob.SpotAnimation;
import net.scapeemulator.game.model.mob.combat.SpecialAttack;

public abstract class Godsword extends SpecialAttack {
	private final Animation anim;
	private final SpotAnimation graphic;

	public Godsword(Animation anim, SpotAnimation graphic) {
		this.anim = anim;
		this.graphic = graphic;
	}

	@Override
	public void afterAttack(Mob attacker, Mob victim, int damage) {

	}

	@Override
	public final SpotAnimation getAttackGraphic() {
		return graphic;
	}

	@Override
	public final Animation getAttackAnimation() {
		return anim;
	}

	@Override
	public int getDrain() {
		return 500;
	}

	@Override
	public final SpecialAttack nextState() {
		return null;
	}

	@Override
	public final int getCombatDelay() {
		return 0;
	}

	@Override
	public double getMaxHit(double maxhit_raw) {
		return maxhit_raw * 1.10;
	}

	@Override
	public final double modAccuracy(double chance) {
		return chance * 2;
	}

}
