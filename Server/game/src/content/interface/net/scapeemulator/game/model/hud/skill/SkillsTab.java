package net.scapeemulator.game.model.hud.skill;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.Tab;

public class SkillsTab extends Tab {
	public static final Interface INSTANCE = new SkillsTab();

	public SkillsTab() {
		super(320, Tab.SKILLS);
	}
}
