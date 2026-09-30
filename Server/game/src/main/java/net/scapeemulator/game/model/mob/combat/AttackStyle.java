package net.scapeemulator.game.model.mob.combat;

import net.scapeemulator.game.model.mob.Animation;

public class AttackStyle {
	public static final AttackStyle[] RANGED_ATTACK_STYLES = new AttackStyle[] {
			new AttackStyle("Accurate", AttackType.ACCURATE, BonusType.RANGED, ExperienceType.RANGED, 0),
			new AttackStyle("Rapid", AttackType.RAPID, BonusType.RANGED, ExperienceType.RANGED, 0),
			new AttackStyle("Long range", AttackType.LONG_RANGE, BonusType.RANGED, ExperienceType.LONG_RANGE, 0) };

	private final String name;
	private final BonusType bonusType;
	private final AttackType attackType;
	private final ExperienceType experienceType;
	private final Animation attackAnimation;

	public AttackStyle(String name, AttackType attackType, BonusType bonusType, int attackAnimation) {
		this(name, attackType, bonusType, ExperienceType.NONE, attackAnimation);
	}

	public AttackStyle(String name, AttackType attackType, BonusType bonusType, ExperienceType experienceType,
			int attackAnimation) {
		this.name = name;
		this.attackType = attackType;
		this.bonusType = bonusType;
		this.experienceType = experienceType;
		this.attackAnimation = new Animation(attackAnimation);
	}

	@Override
	public String toString() {
		return "AttackStyle [name=" + name + ", bonusType=" + bonusType + ", attackType=" + attackType
				+ ", experienceType=" + experienceType + "]";
	}

	public String getName() {
		return name;
	}

	public BonusType getBonusType() {
		return bonusType;
	}

	public AttackType getAttackType() {
		return attackType;
	}

	public ExperienceType getExperienceType() {
		return experienceType;
	}

	public Animation getAttackAnimation() {
		return attackAnimation;
	}
}
