package net.scapeemulator.game.model.mob.combat;

public class Factor {
	private double attack, strength, defence;
	private double magic, ranged;

	public Factor() {
		reset();
	}

	public void reset() {
		attack = 1.0;
		strength = 1.0;
		defence = 1.0;
		magic = 1.0;
		ranged = 1.0;
	}

	@Override
	public String toString() {
		return "Factor [attack=" + attack + ", strength=" + strength + ", defence=" + defence + ", magic=" + magic
				+ ", ranged=" + ranged + "]";
	}

	public double getAttack() {
		return attack;
	}

	public void setAttack(double attack) {
		this.attack = attack;
	}

	public double getStrength() {
		return strength;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}

	public double getDefence() {
		return defence;
	}

	public void setDefence(double defence) {
		this.defence = defence;
	}

	public double getMagic() {
		return magic;
	}

	public void setMagic(double magic) {
		this.magic = magic;
	}

	public double getRanged() {
		return ranged;
	}

	public void setRanged(double ranged) {
		this.ranged = ranged;
	}
}
