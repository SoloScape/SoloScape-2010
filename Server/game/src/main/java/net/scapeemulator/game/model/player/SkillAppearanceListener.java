package net.scapeemulator.game.model.player;

import net.scapeemulator.game.model.mob.SkillListener;
import net.scapeemulator.game.model.mob.SkillSet;

public final class SkillAppearanceListener implements SkillListener {

	private final Player player;

	public SkillAppearanceListener(Player player) {
		this.player = player;
	}

	@Override
	public void skillChanged(SkillSet set, int skill) {
		/* empty */
	}

	@Override
	public void skillLevelledUp(SkillSet set, int skill) {
		player.setAppearance(player.getAppearance());
	}

}
