package net.scapeemulator.game.model.mob.combat;

public enum AttackType {
	ACCURATE(3, 0, 0), CONTROLLED(1, 1, 1),

	AGRESSIVE(3), DEFENSIVE(0, 3), RAPID, LONG_RANGE(0, 1, 3), SHORT_FUSE, MEDIUM_FUSE, LONG_FUSE, AIM_AND_FIRE, MAGIC;
	private final int stanceAttackBonus, stanceDefenceBonus, stanceStrengthBonus;

	private AttackType(int stanceStrengthBonus) {
		this.stanceStrengthBonus = stanceStrengthBonus;
		stanceAttackBonus = 0;
		stanceDefenceBonus = 0;
	}

	private AttackType() {
		stanceAttackBonus = 0;
		stanceDefenceBonus = 0;
		stanceStrengthBonus = 0;
	}

	private AttackType(int stanceAttackBonus, int stanceDefenceBonus) {
		this.stanceAttackBonus = stanceAttackBonus;
		this.stanceDefenceBonus = stanceDefenceBonus;
		stanceStrengthBonus = 0;
	}

	private AttackType(int stanceAttackBonus, int stanceStrengthBonus, int stanceDefenceBonus) {
		this.stanceAttackBonus = stanceAttackBonus;
		this.stanceStrengthBonus = stanceStrengthBonus;
		this.stanceDefenceBonus = stanceDefenceBonus;
	}

	public int getStanceAttackBonus() {
		return stanceAttackBonus;
	}

	public int getDefenceBonus() {
		return stanceDefenceBonus;
	}

	public int getStanceStrengthBonus() {
		return stanceStrengthBonus;
	}
}
