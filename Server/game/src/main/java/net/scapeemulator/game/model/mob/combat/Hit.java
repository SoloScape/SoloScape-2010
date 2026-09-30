package net.scapeemulator.game.model.mob.combat;

import net.scapeemulator.game.model.mob.Mob;

public class Hit {
	private boolean cosmetic = false;
	private ExperienceType experienceType;
	private final Mob yielder;
	private Type type;
	private int damage;
	private SpecialAttack specialAttack;

	public Hit(Type type, int damage, Mob yielder, ExperienceType experienceType) {
		this.type = type;
		this.damage = damage;
		this.yielder = yielder;
		this.experienceType = experienceType;
	}

	public Hit(int damage, Mob yielder, ExperienceType experienceType) {
		this.damage = damage;
		this.yielder = yielder;
		this.experienceType = experienceType;
		type = damage == 0 ? Type.miss : Type.hit;
	}

	public Hit(Type type, int damage, Mob yielder) {
		this.type = type;
		this.damage = damage;
		this.yielder = yielder;
		this.experienceType = ExperienceType.NONE;
	}

	public Hit(Type type, int damage) {
		this.type = type;
		this.damage = damage;
		this.yielder = null;
		this.experienceType = ExperienceType.NONE;
	}

	public static enum Type {
		miss, hit, poison, sickness;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setCosmetic(boolean cosmetic) {
		this.cosmetic = cosmetic;
	}

	public boolean isCosmetic() {
		return cosmetic;
	}

	public ExperienceType getExperienceType() {
		return experienceType;
	}

	public int getDamage() {
		return damage;
	}

	public Type getType() {
		return type;
	}

	public Mob getYielder() {
		return yielder;
	}

	public SpecialAttack getSpecialAttack() {
		return specialAttack;
	}

	public void setSpecialAttack(SpecialAttack specialAttack) {
		this.specialAttack = specialAttack;
	}
}
