package net.scapeemulator.game.model.player.combat;

import java.util.Arrays;

import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.Combat;
import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.model.mob.SpotAnimation;
import net.scapeemulator.game.model.mob.combat.ExperienceType;
import net.scapeemulator.game.model.mob.combat.Hit;
import net.scapeemulator.game.model.mob.combat.SpecialAttack;
import net.scapeemulator.game.model.player.Player;

public class SliceAndDice extends SpecialAttack {
	private final Animation animation;
	private final SpotAnimation graphic;

	private SpecialAttack nextState = null;//Fucks things up, doesn't belong here
	private final ExperienceType experienceType;
	private final int hit, secondaryHit;

	public SliceAndDice() {
		animation = new Animation(10961);
		graphic = new SpotAnimation(1950);
		experienceType = null;
		hit = -1;
		secondaryHit = -1;
	}

	public SliceAndDice(int hit, int secondaryHit, ExperienceType experienceType) {
		this.hit = hit;
		this.secondaryHit = secondaryHit;
		this.experienceType = experienceType;
		animation = null;
		graphic = null;
	}

	@Override
	public int damage(int damage) {
		if (hit != -1)
			return hit;
		return checkDamage(0, damage);
	}

	public int checkDamage(int roll, int damage) {
		if (hit != -1)
			return hit;
		if (roll == 0 && damage < 4) {
			return 0;
		}
		if (roll == 1 && damage < 2)
			return 0;
		return damage;
	}

	@Override
	public void secondaryAttack(Mob mob, Mob target, boolean didHit, double chance, int dealtDamage, int maxHit) {
		if (experienceType != null) {
			target.addHit(new Hit(Hit.Type.hit, secondaryHit, mob, experienceType));
		} else {
			dealtDamage = checkDamage(0, dealtDamage);
			if (dealtDamage == 0)
				didHit = false;
			int damage[] = { dealtDamage, 0, 0, 0 };
			int i = 1;
			for (; i < 4; i++) {
				if (didHit) {
					if (i == 3)
						damage[i] = damage[i - 1] + 1;
					else
						damage[i] = damage[i - 1] / 2;
				}
				if (!didHit) {
					boolean doHit = Combat.roll(chance);
					if (doHit) {
						damage[i] = checkDamage(i, (int) (Math.random() * getMaxHit(maxHit, i, damage[i - 1])));
						if (damage[i] != 0) {
							didHit = true;
						}
					}
				}
			}
//			Startspec
//			hit 0
//			Spec secondaryattack
//			No hit net.scapeemulator.game.model.player.combat.SliceAndDice@5c7fa833
//			hit 1
//			Spec secondaryattack
			if (!didHit) {
				target.addHit(new Hit(1, mob, mob.getCombat().currentStyle().getExperienceType()));
			} else {
				if (mob.getClass() == Player.class)
					((Player) mob).sendMessage("Claw spec: " + Arrays.toString(damage), 99);
				target.addHit(new Hit(damage[1], mob, mob.getCombat().currentStyle().getExperienceType()));
				nextState = new SliceAndDice(damage[2], damage[3], mob.getCombat().currentStyle().getExperienceType());
			}
		}
	}

	@Override
	public void afterAttack(Mob attacker, Mob victim, int damage) {

	}

	@Override
	public SpotAnimation getAttackGraphic() {
		return graphic;
	}

	@Override
	public Animation getAttackAnimation() {
		return animation;
	}

	@Override
	public void startAttack(Mob attacker, Mob victim) {
		if (hit == -1) {
			attacker.getCombat().setSpecialAttack(new SliceAndDice());
			super.startAttack(attacker, victim);
		}
	}

	@Override
	public int getDrain() {
		return 500;
	}

	@Override
	public SpecialAttack nextState() {
		return nextState;
	}

	@Override
	public int getCombatDelay() {
		if (nextState != null)
			return 1;// 2;
		return 0;
	}

	public static double getMaxHit(double maxhit_raw, int state, double prevDamage) {
		if (state == 3) {
			return prevDamage <= 0 ? maxhit_raw * 2 : prevDamage + 1;
		}
		if (prevDamage <= 0) {
			return maxhit_raw;
		}
		return prevDamage * 0.5;
	}

	@Override
	public double getMaxHit(double maxhit_raw) {
		if (hit != -1)
			return hit;
		return maxhit_raw;
	}

	@Override
	public boolean max() {
		return hit != -1;
	}

	@Override
	public double modAccuracy(double chance) {
		return chance;
	}
}
