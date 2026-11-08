package net.scapeemulator.game.model.player.combat;

import net.scapeemulator.game.model.constants.Skill;
import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.model.mob.SkillSet;
import net.scapeemulator.game.model.mob.SpotAnimation;

public class WarStrike extends Godsword {
	public static int[] DRAIN_ORDER = { Skill.DEFENCE, Skill.STRENGTH, Skill.PRAYER, Skill.ATTACK, Skill.MAGIC,
			Skill.RANGED };

	/**
	 * Inflicts 21% more damage, has double accuracy, and drains the target's
	 * combat statistics equivalent of the amount of damage inflicted. If a
	 * combat stat is drained to 1, remaining damage is used to drain another
	 * combat stat. They are drained in the following order: Defence, Strength,
	 * Prayer, Attack, Magic, Ranged.
	 */
	@Override
	public void afterAttack(Mob attacker, Mob victim, int damage) {
		// TODO: Drain (damage)
		if (damage == 0)
			return;
		SkillSet victimSkills = victim.getSkillSet();
		int i = 0;
		while (damage > 0) {
			int skillId = DRAIN_ORDER[i];
			int remaining = victimSkills.getCurrentLevel(skillId);
			if (remaining > damage) {
				victimSkills.damage(skillId, damage);
				damage = 0;
			} else {
				if (remaining != 1) {
					int remove = remaining - 1;
					if (remove != 0) {
						victimSkills.damage(skillId, remove);
						damage -= remove;
					}
				}
			}
			i++;
		}
	}

	@Override
	public double getMaxHit(double maxhit_raw) {
		return maxhit_raw * 1.21;
	}

	@Override
	public int getDrain() {
		return 650;
	}

	public WarStrike() {
		super(new Animation(11991), new SpotAnimation(2114));
	}
}
