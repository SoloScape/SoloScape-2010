package net.scapeemulator.game.model.mob;

public interface SkillListener {

	public void skillChanged(SkillSet set, int skill);

	public void skillLevelledUp(SkillSet set, int skill);

}
