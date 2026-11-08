package net.scapeemulator.game.model.player.combat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.game.model.constants.Skill;

public enum PrayerDetails {
	THICK_SKIN(new Number[] { Skill.DEFENCE, 1.05 }), //
	BURST_OF_STRENGTH(new Number[] { Skill.STRENGTH, 1.05 }), //
	CLARITY_OF_THOUGHT(new Number[] { Skill.ATTACK, 1.05 }), //
	ROCK_SKIN(new Number[] { Skill.DEFENCE, 1.10 }), //
	SUPERHUMAN_STRENGTH(new Number[] { Skill.STRENGTH, 1.10 }), //
	IMPROVED_REFLEXES(new Number[] { Skill.ATTACK, 1.10 }), //
	RAPID_RESTORE, RAPID_HEAL, PROTECT_ITEM, //
	STEEL_SKIN(new Number[] { Skill.DEFENCE, 1.15 }), //
	ULTIMATE_STRENGTH(new Number[] { Skill.STRENGTH, 1.15 }), //
	INCREDIBLE_REFLEXES(new Number[] { Skill.ATTACK, 1.15 }), //
	PROTECT_MAGIC, PROTECT_RANGED, PROTECT_MELEE, //
	RETRIBUTION, REDEMPTION, SMITE, //
	SHARP_EYE(new Number[] { Skill.RANGED, 1.05 }), //
	MYSTIC_WILL(new Number[] { Skill.MAGIC, 1.05 }), //
	HAWK_EYE(new Number[] { Skill.RANGED, 1.10 }), //
	MYSTIC_LORE(new Number[] { Skill.MAGIC, 1.10 }), //
	EAGLE_EYE(new Number[] { Skill.RANGED, 1.15 }), //
	MYSTIC_MIGHT(new Number[] { Skill.MAGIC, 1.15 }), //
	PROTECT_SUMMONING, //
	CHIVALRY(getChivalry()), //
	PIETY(new Number[] { Skill.DEFENCE, 1.25 }, new Number[] { Skill.STRENGTH, 1.23 },
			new Number[] { Skill.ATTACK, 1.20 });//
	private final Map<Integer, Double> factors;// ???

	private PrayerDetails() {
		factors = null;
	}

	private PrayerDetails(Number[]... array) {
		Map<Integer, Double> modifiable = new HashMap<>();
		for (Number[] factors : array) {
			modifiable.put(factors[0].intValue(), factors[1].doubleValue());
		}
		factors = Collections.unmodifiableMap(modifiable);
	}

	public Map<Integer, Double> getFactors() {
		return factors;
	}

	private static Number[][] getChivalry() {
		return new Number[][] { new Number[] { Skill.DEFENCE, 1.20 }, //
				new Number[] { Skill.STRENGTH, 1.18 }, //
				new Number[] { Skill.ATTACK, 1.15 } };
	}

}
