package net.scapeemulator.game.model.player;

import net.scapeemulator.game.model.mob.SkillListener;
import net.scapeemulator.game.model.mob.SkillSet;
import net.scapeemulator.game.msg.gameplay.SkillMessage;

public final class SkillMessageListener implements SkillListener {

	private final Player player;

	public SkillMessageListener(Player player) {
		this.player = player;
	}

	@Override
	public void skillChanged(SkillSet set, int skill) {
		int level = set.getCurrentLevel(skill);
		int experience = (int) set.getExperience(skill);
		player.send(new SkillMessage(skill, level, experience));
	}

	@Override
	public void skillLevelledUp(SkillSet set, int skill) {
		/* empty */
	}
}
