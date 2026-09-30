package net.scapeemulator.game.model.mob;

import net.scapeemulator.game.cache.ItemDefinition;
import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.constants.GenericValue;
import net.scapeemulator.game.model.constants.Skill;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.mob.combat.AttackStyle;
import net.scapeemulator.game.model.mob.combat.AttackType;
import net.scapeemulator.game.model.mob.combat.BonusType;
import net.scapeemulator.game.model.mob.combat.ExperienceType;
import net.scapeemulator.game.model.mob.combat.Hit;
import net.scapeemulator.game.model.mob.combat.SpecialAttack;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.WeaponType;
import net.scapeemulator.game.model.player.combat.SliceAndDice;
import net.scapeemulator.game.model.player.var.Status;

/**
 * Major shoutout to this thread.
 * https://www.rune-server.org/runescape-development/rs-503-client-server/
 * informative-threads/542266-calculating-player-vs-player-combat-accuracy.html
 * 
 * @author Teemu
 *
 */
public class Combat {
	private int currentStyle;
	private int speed, delay, previousOffenseTick;

	private SpecialAttack specialAttack;
	private AttackStyle[] styles;
	private Animation[] attackAnimations;
	private Animation defAnim, deathAnim;
	private final Mob mob;
	private Mob target, previousOffender;

	// TODO
	private boolean retaliate;

	public Combat(Mob mob) {// Woo woo :P
		this.mob = mob;
		delay = 0;
		currentStyle = 0;
		styles = WeaponType.UNARMED.getStyles();
		speed = WeaponType.UNARMED.getAttackSpeed();
		defAnim = WeaponType.UNARMED.getDefenceAnimation();
		attackAnimations = WeaponType.UNARMED.getAttackAnimations();
		deathAnim = new Animation(7197);
	}

	public void process() {
		if (delay > 0)
			delay--;

		/**
		 * Singleway combat related timers
		 */
		if (previousOffenseTick > 0) {
			previousOffenseTick--;
		}
		if (previousOffenseTick == 0 || (previousOffender != null && !previousOffender.isListed())) {
			previousOffender = null;
			if (previousOffenseTick == 0)
				previousOffenseTick = -1;
		}
		// TODO: Projectile
		// TODO: Dwarven cannon?

		// TODO: COMBAT
		// TODO: Smite

		// How much of this should be executed elsewhere?
		if (target != null) {
			// Check for death here
			if (!target.isListed() || target.dead()) {
				mob.setInteractingTarget(null);
				target = null;

				// Ongoing (multi-hit) special attack cancellation due to death
				if (mob.getClass() == Player.class) {
					boolean spec = (boolean) ((Player) mob).getStatus().getStatus("using_special");
					if (!spec && specialAttack != null) {
						specialAttack = null;
					}
				}
				return;
			}
			// Cancel projectile here

			if (delay == 0) {
				// Combat type how? by attack style? Answer: YES
				AttackStyle attackStyle = styles[getCurrentStyle()];

				// Distance, NPC size, can't overlap
				int allowedDistance = 1;
				boolean singleway = false;
				// Check for autocast, then use magic
				switch (attackStyle.getBonusType()) {
				case MAGIC:
					allowedDistance = 12;
					break;
				case NONE:
					if (attackStyle.getAttackType() == AttackType.AIM_AND_FIRE) {// TODO
					}
					break;
				case RANGED:
					allowedDistance = 12;
					if (attackStyle.getAttackType() == AttackType.LONG_RANGE) {
						allowedDistance += 1;
					}
					break;
				case SUMMONING:// ????
					break;
				default:
					// Halberd
					break;
				}
				mob.setInteractingTarget(target);
				// combat follow
				if (singleway) {
					if (previousOffender != null) {
						if (previousOffender.combat.target == mob && previousOffender != target) {
							return;
							// I'm already under attack
						}
						if (target.combat.previousOffender != mob && target.combat.previousOffender != mob) {
							return;
							// Someone else is fighting that, cancel combat
						}
					}
				}
				/*
				 * Allow the dragon claw special to override the maximum allowed
				 * distance
				 */
				if (specialAttack != null && specialAttack.getClass() == SliceAndDice.class)
					allowedDistance = 3;
				// Check for distance
				// Fist of guthix stone
				// Multiway combat areas
				// can't melee through fences
				// Can't shoot projectiles through walls, but can over fences
				// Auto retaliate
				if (target.getCombat().retaliate()) {
					target.getCombat().setTarget(mob);
					target.setInteractingTarget(mob);
				}
				if (specialAttack != null) {
					delay = specialAttack.getCombatDelay();
					specialAttack.startAttack(mob, target);
				}
				// passive offense bonuses
				// Passive defence bonuses
				// item set bonuses (void, dharoks)

				// Get maximum hit
				double maxhit_ = getMaxHit(mob, 1.0);
				Animation attackAnimation = styles[currentStyle].getAttackAnimation();
				SpotAnimation attackGraphic = null;

				double multiplyingBonus = 1.0, additiveBonus = 1.0;
				/*
				 * Multiplying bonus: black mask, salve amulet, etc.
				 * 
				 * Additive bonus: 1/3rd of each level above required grants
				 * extra level to effective attack.
				 */

				// attack
				double effectiveAttack = getEffectiveAttack(mob, multiplyingBonus, additiveBonus);
				double bonus = mob.getBonus().getAgressive(styles[currentStyle].getBonusType().ordinal());
				double augmentedAttack = Math.floor(((effectiveAttack + 8) * (bonus + 64)) / 10);

				// defence
				double effectiveDefence = getEffectiveDefence(target, styles[currentStyle]);
				bonus = target.getBonus().getDefensive(styles[currentStyle].getBonusType().ordinal());
				double augmentedDefence = Math.floor(((effectiveDefence + 8) * (bonus + 64)) / 10);

				double chance;
				if (augmentedAttack < augmentedDefence) {
					chance = (augmentedAttack - 1) / (augmentedDefence * 2);
				} else {
					chance = 1 - ((augmentedDefence + 1) / (augmentedAttack * 2));
				}

				// special attack roll modificaction
				if (specialAttack != null) {
					// Modify hit and accuracy
					maxhit_ = specialAttack.getMaxHit(maxhit_);
					chance = specialAttack.modAccuracy(chance);

					// Special attack anim & gfx
					attackAnimation = specialAttack.getAttackAnimation();
					attackGraphic = specialAttack.getAttackGraphic();
				}

				int maxhit = (int) trunc(maxhit_);

				@SuppressWarnings("unused")
				boolean pvp = mob.getClass() == Player.class && target.getClass() == Player.class;
				/*
				 * TODO: Switch(target.headicon()) {
				 * 
				 * Protect prayer reduces chances to *= 0.6 or 0
				 * 
				 * NVN: 100% of protected damage is blocked PVN: 100% of
				 * protected damage is blocked NVP: 100% of damage is protected
				 * 
				 * pvp 40%
				 */

				Hit hit = null;
				boolean doHit;
				if (maxhit == -1) {
					doHit = false;
					// if hit -1 skip adding hit
				} else {
					doHit = roll(chance);
					// roll hit
					int damage = random(maxhit);
					if (specialAttack != null) {
						damage = specialAttack.damage(damage);
						if (specialAttack.max())
							doHit = true;
					}
					if (doHit && damage > 0) {
						hit = new Hit(damage, mob, attackStyle.getExperienceType());
					} else {
						hit = new Hit(Hit.Type.miss, 0);
					}
				}

				if (mob.getClass() == Player.class) {
					((Player) mob).sendMessage(
							"Augmented attack/defence (" + augmentedAttack + '/' + augmentedDefence + ") hit/max ("
									+ hit.getDamage() + '/' + maxhit + ") chance/hit (" + chance + ',' + doHit + ')',
							99);
				}
				// Animate
				if (attackAnimation != null)
					mob.playAnimation(attackAnimation);
				// Graphic, if one
				if (attackGraphic != null)
					mob.playSpotAnimation(attackGraphic);

				// TODO: Projectile, postpone rest (add into projectile)

				// Add hit(s)
				if (hit != null) {
					target.addHit(hit);
				}
				if (specialAttack != null) {
					hit.setSpecialAttack(specialAttack);
					specialAttack.secondaryAttack(mob, target, doHit, chance, hit.getDamage(), maxhit);
					delay = specialAttack.getCombatDelay();
				}

				// TODO multiway both target eachother same?
				// victim combat delay SHOULD(or?) be postponed if single-way
				if (target.getCombat().getDelay() == 0 && singleway) {
					target.getCombat().setDelay(1);
				}
				// Make victim defend
				// only the first attack defends dclaws spec
				if (target.getAnimation() == null)
					target.playAnimation(target.getCombat().getDefAnim());

				// Post-attack offensive factors (F. ex. poison)
				if (specialAttack != null)
					specialAttack = specialAttack.nextState();

				// Set delay and singleway combat related variables
				target.getCombat().previousOffender = mob;
				target.getCombat().previousOffenseTick = 16;// ??
				if (delay <= 0) {
					delay = speed;
				}
			}
		}
	}

	private static int random(int roof) {
		return (int) Math.floor(Math.random() * (roof + 1));
	}

	public static boolean roll(double chance) {
		int hitChance = 0
				+ (int) Math.floor(chance /* * off_void_bonus ) * .6) */ * 100);
		int blockChance = 0
				+ (int) Math.floor(101 - (chance /* * off_void_bonus ) * .6) */ * 100));
		// roll
		hitChance *= Math.random();
		blockChance *= Math.random();
		return random((int) trunc(hitChance)) > random((int) trunc(blockChance));
	}

	/*
	 * TODO: Combat (NPCDefinition def)
	 */

	/**
	 * Should we add the magic base experience here or not?
	 * 
	 * @param victim
	 *            the victim who we dealt the damage to.
	 * @param experienceType
	 * @param i
	 */
	public void postAttack(Mob victim, Hit hit) {
		int damage = hit.getDamage();
		ExperienceType experienceType = hit.getExperienceType();
		SkillSet skills = mob.getSkillSet();
		if (damage != 0) {
			if (damage < 0)
				System.out.println(hit.getYielder()+" "+damage);
			if (experienceType != ExperienceType.NONE)
				skills.addExperience(Skill.HITPOINTS, damage * 1.33);
			switch (experienceType) {
			case ATTACK:
				skills.addExperience(Skill.ATTACK, damage * 4);
				break;
			case DEFENCE:
				skills.addExperience(Skill.DEFENCE, damage * 4);
				break;
			case RANGED:
				skills.addExperience(Skill.RANGED, damage * 4);
				break;
			case LONG_RANGE:
				skills.addExperience(Skill.RANGED, damage * 2);
				skills.addExperience(Skill.DEFENCE, damage * 2);
				break;
			case MAGIC:
				skills.addExperience(Skill.MAGIC, damage * 2);
				break;
			case MAGIC_DEFENSIVE:
				skills.addExperience(Skill.MAGIC, damage * 1.33);
				skills.addExperience(Skill.DEFENCE, damage);
				break;
			case MELEE_SHARED:
				skills.addExperience(Skill.ATTACK, damage * 1.33);
				skills.addExperience(Skill.STRENGTH, damage * 1.33);
				skills.addExperience(Skill.DEFENCE, damage * 1.33);
				break;
			case STRENGTH:
				skills.addExperience(Skill.STRENGTH, damage * 4);
				break;
			default:
				break;
			}
		}
		if (hit.getSpecialAttack() != null) {
			hit.getSpecialAttack().afterAttack(mob, victim, damage);
		}
		if (specialAttack == null) {
			if (mob instanceof Player) {
				Status status = ((Player) mob).getStatus();
				status.refresh("using_special");
			}
		}
		// What else?
	}

	public static int getAttackSpeed(Item item) {
		ItemDefinition def = item.getDefinition();
		if (def == null) {
			return -1;
		} else {
			if (def.isMembersOnly() && World.getWorld().members()) {
				return 5;
			} else {
				if (def.getGeneric(GenericValue.ATTACK_SPEED, -1) == -1) {
					WeaponType type = WeaponType.values()[def.getGeneric(GenericValue.WEAPON_TYPE, 0)];
					if (type == WeaponType.UNARMED) {
						return 5;
					}
					return type.getAttackSpeed();
				} else {
					return def.getGeneric(GenericValue.ATTACK_SPEED, -1);
				}
			}
		}
	}

	/*
	 * get rid of weapon bonus
	 * 
	 * Additive bonus: Weapon bonus based on attack or so
	 */
	public static double getEffectiveAttack(Mob mob, double multiplyingBonus, double additiveBonus) {
		Combat combat = mob.getCombat();
		AttackStyle style = combat.getStyles()[combat.getCurrentStyle()];
		int stanceBonus = style.getAttackType().getStanceAttackBonus();
		int level;
		double prayerBonus = 1.0;
		if (style.getBonusType() == BonusType.RANGED) {
			level = mob.getSkillSet().getCurrentLevel(Skill.RANGED);
			prayerBonus = mob.getFactor().getRanged();
		} else if (style.getBonusType() == BonusType.MAGIC) {
			level = mob.getSkillSet().getCurrentLevel(Skill.MAGIC);
			prayerBonus = mob.getFactor().getMagic();
		} else {
			level = mob.getSkillSet().getCurrentLevel(Skill.ATTACK);
			prayerBonus = mob.getFactor().getAttack();
		}
		return trunc((level * prayerBonus * multiplyingBonus) + stanceBonus + additiveBonus);
	}

	public static double getEffectiveDefence(Mob mob, AttackStyle offense) {
		AttackStyle currentStyle = mob.combat.styles[mob.combat.currentStyle];
		double defence = mob.getSkillSet().getCurrentLevel(Skill.DEFENCE);
		int stance = currentStyle.getAttackType().getDefenceBonus();
		double prayerBonus = mob.getFactor().getDefence();

		if (offense.getAttackType() == AttackType.MAGIC) {
			defence = (defence * 0.3) + (mob.getSkillSet().getCurrentLevel(Skill.MAGIC) * 0.7);
		}
		return trunc((defence * prayerBonus) + stance);
	}

	public static double getMaxHit(Mob mob, double otherBonus) {
		Combat combat = mob.getCombat();
		AttackStyle current = combat.getStyles()[combat.getCurrentStyle()];
		boolean ranged = current.getBonusType() == BonusType.RANGED;

		double strength = mob.getSkillSet().getCurrentLevel(ranged ? Skill.RANGED : Skill.STRENGTH);
		double prayBonus = ranged ? mob.getFactor().getRanged() : mob.getFactor().getStrength();
		double effectiveStrength = trunc(strength * prayBonus * otherBonus);

		if (ranged) {
			effectiveStrength += current.getAttackType().getStanceAttackBonus()
					+ current.getAttackType().getStanceStrengthBonus();
		} else {
			effectiveStrength += current.getAttackType().getStanceStrengthBonus();
		}
		int effStrBonus = ranged ? Bonus.RANGED_STRENGTH : Bonus.STRENGTH;

		double base = 1.3 + (effectiveStrength / 10) + (strength / 80)
				+ ((mob.getBonus().getExtra(effStrBonus) * strength) / 640);

		return base;
	}

	private static double trunc(double d) {
		return Math.floor(d);
	}

	public void setAttackStyles(int speed, AttackStyle[] styles) {
		this.speed = speed;
		this.styles = styles;
	}

	public Animation[] getAttackAnimations() {
		return attackAnimations;
	}

	public void setAttackAnimations(Animation[] attackAnimations) {
		this.attackAnimations = attackAnimations;
	}

	public void setDefAnim(Animation defAnim) {
		this.defAnim = defAnim;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getSpeed() {
		return speed;
	}

	public AttackStyle[] getStyles() {
		return styles;
	}

	public Animation getDefAnim() {
		return defAnim;
	}

	public int getCurrentStyle() {
		return currentStyle;
	}

	public void setCurrentStyle(int currentStyle) {
		this.currentStyle = currentStyle;
	}

	public Mob getTarget() {
		return target;
	}

	public void setTarget(Mob target) {
		this.target = target;
	}

	private boolean retaliate() {
		return retaliate;
	}

	public void setRetaliate(boolean retaliate) {
		this.retaliate = retaliate;
	}

	public SpecialAttack getSpecialAttack() {
		return specialAttack;
	}

	public void setSpecialAttack(SpecialAttack specialAttack) {
		this.specialAttack = specialAttack;
	}

	public AttackStyle currentStyle() {
		return styles[currentStyle];
	}

	public Animation getDeathAnimation() {
		return deathAnim;
	}
}
