package net.scapeemulator.game.model.mob;

public class Bonus {
	private int[] defensiveBonus;
	private int[] agressiveBonus;
	
	//These are actually doubles
	private int[] extraBonus;

	public Bonus() {
		agressiveBonus = new int[6];
		defensiveBonus = new int[6];
		extraBonus = new int[4];
	}

	public int getDefensive(int id) {
		return defensiveBonus[id];
	}

	public void addDefensive(int id, int value) {
		defensiveBonus[id] += value;
	}

	public void addAgressive(int id, int value) {
		agressiveBonus[id] += value;
	}

	public void addExtra(int id, int value) {
		extraBonus[id] += value;
	}

	public void removeDefensive(int id, int value) {
		defensiveBonus[id] -= value;
	}

	public void removeAgressive(int id, int value) {
		agressiveBonus[id] -= value;
	}

	public void removeExtra(int id, int value) {
		extraBonus[id] -= value;
	}

	public void setDefensive(int id, int value) {
		defensiveBonus[id] = value;
	}

	public int getAgressive(int id) {
		return agressiveBonus[id];
	}

	public void setAgressive(int id, int value) {
		agressiveBonus[id] = value;
	}

	public int getExtra(int id) {
		return extraBonus[id];
	}

	public void setExtra(int id, int value) {
		extraBonus[id] = value;
	}

	// Magic damage needs a proper way...
	public static final String[] AGRESSIVE_NAMES = new String[] { "Stab", "Slash", "Crush", "Magic", "Ranged" };
	public static final String[] DEFENSIVE_NAMES = new String[] { "Stab", "Slash", "Crush", "Magic", "Range",
			"Summoning" };
	public static final String[] EXTRA_NAMES = new String[] { "Strength", "Ranged Strength", "Prayer", "Magic Damage" };
	public static final int STAB = 0, SLASH = 1, CRUSH = 2, MAGIC = 3, RANGED = 4, SUMMONING = 5;
	public static final int STRENGTH = 0, RANGED_STRENGTH = 1, PRAYER = 2, MAGIC_DAMAGE = 3;

	public int[] getAgressives() {
		return agressiveBonus;
	}

	public int[] getDefensives() {
		return defensiveBonus;
	}

	public int[] getExtra() {
		return extraBonus;
	}
}
