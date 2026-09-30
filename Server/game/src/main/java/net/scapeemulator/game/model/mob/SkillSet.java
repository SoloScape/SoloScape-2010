package net.scapeemulator.game.model.mob;

import java.util.ArrayList;
import java.util.List;

import net.scapeemulator.game.model.constants.Skill;

public final class SkillSet {

	public static final double MAXIMUM_EXPERIENCE = 200000000;

	private static int getLevelRoof(double xp) {
		int points = 0;
		int output = 0;

		for (int level = 1; level <= 99; level++) {
			points += Math.floor(level + 300.0 * Math.pow(2.0, level / 7.0));
			output = points / 4;

			if ((output - 1) >= xp)
				return level;
		}

		return 99;
	}

	private final int[] level = new int[24];
	private final int[] skillRoof = new int[level.length];
	private final double[] exp = new double[level.length];
	private final List<SkillListener> listeners = new ArrayList<>();

	public SkillSet() {
		for (int i = 0; i < level.length; i++) {
			level[i] = 1;
			skillRoof[i] = 1;
			exp[i] = 0;
		}

		level[Skill.HITPOINTS] = 10;
		skillRoof[Skill.HITPOINTS] = 10;
		exp[Skill.HITPOINTS] = 1154;
	}

	public void reset(int id) {
		level[id] = skillRoof[id];
	}

	public void damage(int id, int damage) {
		level[id] -= damage;
		if (level[id] < 0)
			level[id] = 0;
		for (SkillListener listener : listeners)
			listener.skillChanged(this, id);
	}

	public void restore(int id, int restoration, boolean canOverflow) {
		level[id] += restoration;
		if (level[id] > skillRoof[id] && !canOverflow)
			level[id] = skillRoof[id];
		for (SkillListener listener : listeners)
			listener.skillChanged(this, id);
	}

	public void restore() {
		for (int i = 0; i < skillRoof.length; i++) {
			level[i] = skillRoof[i];
			for (SkillListener listener : listeners)
				listener.skillChanged(this, i);
		}
	}

	public void restore(int id) {
		if (level[id] != skillRoof[id]) {
			if (level[id] < skillRoof[id]) {
				level[id]++;
			} else if (level[id] > skillRoof[id]) {
				level[id]--;
			}
			for (SkillListener listener : listeners)
				listener.skillChanged(this, id);
		}
	}

	public void addExperience(int skill, double xp) {
		if (xp < 0)
			throw new IllegalArgumentException("Experience cannot be negative.");

		int oldLevel = getMaximumLevel(skill);
		exp[skill] = Math.min(exp[skill] + xp, MAXIMUM_EXPERIENCE);

		int delta = getLevelRoof(exp[skill]) - oldLevel;
		level[skill] += delta;
		skillRoof[skill] += delta;

		for (SkillListener listener : listeners)
			listener.skillChanged(this, skill);

		if (delta > 0) {
			for (SkillListener listener : listeners)
				listener.skillLevelledUp(this, skill);
		}
	}

	public void refresh() {
		for (int skill = 0; skill < level.length; skill++) {
			for (SkillListener listener : listeners)
				listener.skillChanged(this, skill);
		}
	}

	public int getCombatLevel() {
		int defence = getMaximumLevel(Skill.DEFENCE);
		int hitpoints = getMaximumLevel(Skill.HITPOINTS);
		int prayer = getMaximumLevel(Skill.PRAYER);
		int attack = getMaximumLevel(Skill.ATTACK);
		int strength = getMaximumLevel(Skill.STRENGTH);
		int magic = getMaximumLevel(Skill.MAGIC);
		int ranged = getMaximumLevel(Skill.RANGED);
		// TODO set to zero on a F2P world
		int summoning = getMaximumLevel(Skill.SUMMONING);

		double base = 1.3 * Math.max(Math.max(attack + strength, 1.5 * magic), 1.5 * ranged);

		return (int) Math
				.floor((defence + hitpoints + Math.floor(prayer / 2.0) + Math.floor(summoning / 2.0) + base) / 4.0);
	}

	public int getTotalLevel() {
		int total = 0;
		for (int skill = 0; skill < level.length; skill++)
			total += getMaximumLevel(skill);
		return total;
	}

	public void addListener(SkillListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SkillListener listener) {
		listeners.remove(listener);
	}

	public void removeListeners() {
		listeners.clear();
	}

	public int size() {
		return level.length;
	}

	public int getCurrentLevel(int skill) {
		return level[skill];
	}

	public int getMaximumLevel(int skill) {
		return skillRoof[skill];
	}

	public double getExperience(int skill) {
		return exp[skill];
	}

	public int[] getSkillRoof() {
		return skillRoof;
	}
}
