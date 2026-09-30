package net.scapeemulator.game.model.player;

import java.util.Arrays;

import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.combat.AttackStyle;
import net.scapeemulator.game.model.mob.combat.AttackType;
import net.scapeemulator.game.model.mob.combat.BonusType;
import net.scapeemulator.game.model.mob.combat.ExperienceType;

/**
 * TODO: Add Weapon interface id, etc shit for lover versions.
 */
public enum WeaponType {
	UNARMED(5, 424, new AttackStyle("Punch", AttackType.ACCURATE, BonusType.CRUSH, ExperienceType.ATTACK, 422),
			new AttackStyle("Kick", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 423),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.CRUSH, ExperienceType.DEFENCE, 422)),

	STAFF(5, 0, new AttackStyle("Punch", AttackType.ACCURATE, BonusType.CRUSH, ExperienceType.ATTACK, 0),
			new AttackStyle("Kick", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.CRUSH, ExperienceType.DEFENCE, 0)),

	AXE(6, 0, new AttackStyle("Chop", AttackType.ACCURATE, BonusType.SLASH, ExperienceType.ATTACK, 0),
			new AttackStyle("Hack", AttackType.AGRESSIVE, BonusType.SLASH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Smash", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.SLASH, ExperienceType.DEFENCE, 0)),

	SCEPTRE(5, 0, new AttackStyle("Bash", AttackType.ACCURATE, BonusType.CRUSH, ExperienceType.ATTACK, 0),
			new AttackStyle("Pound", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.CRUSH, ExperienceType.DEFENCE, 0)),

	PICKAXE(5, 0, new AttackStyle("Spike", AttackType.ACCURATE, BonusType.STAB, ExperienceType.ATTACK, 0),
			new AttackStyle("Impale", AttackType.AGRESSIVE, BonusType.STAB, ExperienceType.STRENGTH, 0),
			new AttackStyle("Smash", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.STAB, ExperienceType.DEFENCE, 0)),

	DAGGER_SWORD(4, 404, new AttackStyle("Stab", AttackType.ACCURATE, BonusType.STAB, ExperienceType.ATTACK, 12028),
			new AttackStyle("Lunge", AttackType.AGRESSIVE, BonusType.STAB, ExperienceType.STRENGTH, 12028),
			new AttackStyle("Slash", AttackType.AGRESSIVE, BonusType.SLASH, ExperienceType.STRENGTH, 12029),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.STAB, ExperienceType.DEFENCE, 12028)),

	SCIMITAR_LONGSWORD(5, 12030,
			new AttackStyle("Chop", AttackType.ACCURATE, BonusType.SLASH, ExperienceType.ATTACK, 12029),
			new AttackStyle("Slash", AttackType.AGRESSIVE, BonusType.SLASH, ExperienceType.STRENGTH, 12029),
			new AttackStyle("Lunge", AttackType.CONTROLLED, BonusType.STAB, ExperienceType.MELEE_SHARED, 12028),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.SLASH, ExperienceType.DEFENCE, 12029)),
			// 12028 scim

	TWO_HANDED_SWORD(7, 12022, // Done
			new AttackStyle("Chop", AttackType.ACCURATE, BonusType.SLASH, ExperienceType.ATTACK, 11979),
			new AttackStyle("Slash", AttackType.AGRESSIVE, BonusType.SLASH, ExperienceType.STRENGTH, 11979),
			new AttackStyle("Smash", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 11980),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.SLASH, ExperienceType.DEFENCE, 410)),

	MACE(5, 0, new AttackStyle("Pound", AttackType.ACCURATE, BonusType.CRUSH, ExperienceType.ATTACK, 0),
			new AttackStyle("Pummel", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Spike", AttackType.CONTROLLED, BonusType.STAB, ExperienceType.MELEE_SHARED, 0),
			new AttackStyle("Defensive", AttackType.DEFENSIVE, BonusType.CRUSH, ExperienceType.DEFENCE, 0)),

	CLAWS(4, 0, new AttackStyle("Chop", AttackType.ACCURATE, BonusType.STAB, ExperienceType.ATTACK, 0),
			new AttackStyle("Slash", AttackType.AGRESSIVE, BonusType.STAB, ExperienceType.STRENGTH, 0),
			new AttackStyle("Lunge", AttackType.CONTROLLED, BonusType.SLASH, ExperienceType.MELEE_SHARED, 0),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.STAB, ExperienceType.DEFENCE, 0)),

	WARHAMMER(6, 0, new AttackStyle("Pound", AttackType.ACCURATE, BonusType.CRUSH, ExperienceType.ATTACK, 0),
			new AttackStyle("Pummel", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.CRUSH, ExperienceType.DEFENCE, 0)),

	WHIP(4, 11974, new AttackStyle("Flick", AttackType.ACCURATE, BonusType.SLASH, ExperienceType.ATTACK, 11968),
			new AttackStyle("Lash", AttackType.CONTROLLED, BonusType.SLASH, ExperienceType.MELEE_SHARED, 11969),
			new AttackStyle("Deflect", AttackType.DEFENSIVE, BonusType.SLASH, ExperienceType.DEFENCE, 11970)),

	MELEE_FUN_WEAPON(5, 0, new AttackStyle("Pound", AttackType.ACCURATE, BonusType.CRUSH, ExperienceType.ATTACK, 0),
			new AttackStyle("Pummel", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.CRUSH, ExperienceType.DEFENCE, 0)),

	MUDPIE(5, 0, AttackStyle.RANGED_ATTACK_STYLES),

	SPEAR(5, 0, new AttackStyle("Lunge", AttackType.CONTROLLED, BonusType.STAB, ExperienceType.MELEE_SHARED, 0),
			new AttackStyle("Swipe", AttackType.CONTROLLED, BonusType.SLASH, ExperienceType.MELEE_SHARED, 0),
			new AttackStyle("Pound", AttackType.CONTROLLED, BonusType.CRUSH, ExperienceType.MELEE_SHARED, 0),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.STAB, ExperienceType.DEFENCE, 0)),

	HALBERD(7, 0, new AttackStyle("Jab", AttackType.CONTROLLED, BonusType.STAB, ExperienceType.MELEE_SHARED, 0),
			new AttackStyle("Swipe", AttackType.AGRESSIVE, BonusType.SLASH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Fend", AttackType.DEFENSIVE, BonusType.STAB, ExperienceType.DEFENCE, 0)),

	BOW(5, 0, AttackStyle.RANGED_ATTACK_STYLES),

	CROSSBOW(6, 0, AttackStyle.RANGED_ATTACK_STYLES),

	RANGED_THROWABLE(3, 0, AttackStyle.RANGED_ATTACK_STYLES),

	RANGED_MISSILE(5, 0,
			new AttackStyle("Short fuse", AttackType.SHORT_FUSE, BonusType.RANGED, ExperienceType.RANGED, 0),
			new AttackStyle("Medium fuse", AttackType.MEDIUM_FUSE, BonusType.RANGED, ExperienceType.RANGED, 0),
			new AttackStyle("Long fuse", AttackType.LONG_FUSE, BonusType.RANGED, ExperienceType.LONG_RANGE, 0)),

	FIXED_DEVICE(5, 0, new AttackStyle("Aim and Fire", AttackType.AIM_AND_FIRE, BonusType.NONE, ExperienceType.NONE, 0),
			new AttackStyle("Kick", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 0)),

	LIZARD(5, 0, new AttackStyle("Scorch", AttackType.AGRESSIVE, BonusType.SLASH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Flare", AttackType.ACCURATE, BonusType.RANGED, ExperienceType.RANGED, 0),
			new AttackStyle("Blaze", AttackType.DEFENSIVE, BonusType.MAGIC, ExperienceType.MAGIC, 0)),

	SCYTHE(5, 0, new AttackStyle("Reap", AttackType.ACCURATE, BonusType.SLASH, ExperienceType.ATTACK, 0),
			new AttackStyle("Chop", AttackType.AGRESSIVE, BonusType.STAB, ExperienceType.STRENGTH, 0),
			new AttackStyle("Jab", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Block", AttackType.DEFENSIVE, BonusType.SLASH, ExperienceType.DEFENCE, 0)),

	FLAIL(5, 0, new AttackStyle("Slash", AttackType.ACCURATE, BonusType.SLASH, ExperienceType.ATTACK, 0),
			new AttackStyle("Crush", AttackType.AGRESSIVE, BonusType.CRUSH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Slash", AttackType.DEFENSIVE, BonusType.SLASH, ExperienceType.DEFENCE, 0)),

	WEPTYPSLING(5, 0),

	TRIDENT(5, 0, new AttackStyle("Jab", AttackType.ACCURATE, BonusType.STAB, ExperienceType.ATTACK, 0),
			new AttackStyle("Swipe", AttackType.AGRESSIVE, BonusType.SLASH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Fend", AttackType.DEFENSIVE, BonusType.CRUSH, ExperienceType.DEFENCE, 0)),

	STAFF_OF_LIGHT(5, 0, new AttackStyle("Jab", AttackType.ACCURATE, BonusType.STAB, ExperienceType.ATTACK, 0),
			new AttackStyle("Swipe", AttackType.AGRESSIVE, BonusType.SLASH, ExperienceType.STRENGTH, 0),
			new AttackStyle("Fend", AttackType.DEFENSIVE, BonusType.CRUSH, ExperienceType.DEFENCE, 0));

	private final Animation[] attackAnimations;
	private final AttackStyle[] styles;
	private final int attackSpeed;
	private final Animation defenceAnimation;

	@Override
	public String toString() {
		return "WeaponType [" + name() + ": " + Arrays.toString(styles) + "]";
	}

	private WeaponType(int attackSpeed, int defenceAnimation, AttackStyle... styles) {
		this.attackSpeed = attackSpeed;
		this.defenceAnimation = new Animation(defenceAnimation);
		this.styles = styles;
		this.attackAnimations = new Animation[styles.length];
		for (int i = 0; i < attackAnimations.length; i++) {
			attackAnimations[i] = styles[i].getAttackAnimation();
		}
	}

	public AttackStyle[] getStyles() {
		return styles;
	}

	public Animation getDefenceAnimation() {
		return defenceAnimation;
	}

	public Animation[] getAttackAnimations() {
		return attackAnimations;
	}

	public int getAttackSpeed() {
		return attackSpeed;
	}
}
