package net.scapeemulator.game.model.npc;

import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.model.mob.combat.AttackStyle;
import net.scapeemulator.game.model.mob.combat.AttackType;
import net.scapeemulator.game.model.mob.combat.BonusType;
import net.scapeemulator.game.model.mob.combat.ExperienceType;

public final class Npc extends Mob {
	private int type;
	private boolean transform;

	public Npc(int type) {
		skillSet.getSkillRoof()[3] = 10000;
		skillSet.reset(3);
		combat.setAttackStyles(5, new AttackStyle[] {
				new AttackStyle("Punch", AttackType.ACCURATE, BonusType.CRUSH, ExperienceType.NONE, 422) });
		this.type = type;
	}

	public void transform(int type) {
		this.type = type;
		transform = true;
	}

	@Override
	public void reset() {
		super.reset();
		transform = false;
	}

	public int getType() {
		return type;
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	public boolean doTransform() {
		return transform;
	}
}
