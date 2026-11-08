package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.game.GameServer;
import net.scapeemulator.game.Plugin;
import net.scapeemulator.game.model.player.var.PVariable;
import net.scapeemulator.game.model.player.var.PVariable.Type;

public class TutorialIsland extends Plugin {

	@Override
	public void register(GameServer gameServer) {
		initVariable();
	}

	private void initVariable() {
		PVariable.Builder<String> bldr = new PVariable.Builder<>("tutorial_island");
		bldr.type(Type.VARP);
		bldr.id(406);
		bldr.persist().setTrigger(new TutorialTrigger());
		bldr.value("design_character");
		bldr.value("runescape_guide");
		// States:
		// 1-introduction
		// 2-Open settings (flash)
		// 3-Retalk-go-to-survexpt
		bldr.value("survival_expert");// <- State changes on door open
		//Open-inventory (flash)
		//Hint-at-tree, chop-make-fire
		//Fire-made, open skills (flash)
		//Skill stats
		bldr.value("fishing_tutorial");
		//net, hint at fishing spot
		//Burn first shrimp
		//Gate
		bldr.value("cooking_tutorial");
		bldr.value("bread_baked");
		bldr.value("emotes_and_running");
		//Flash settings, on open reset running
		//Try emote, flash settings tab/run orb
		bldr.value("quest_guide");
		bldr.value("prospecting_and_mining");
		bldr.value("smelting_and_smithing");
		bldr.value("equipment");
		//Flash equipment tab
		//Flash combat tab
		bldr.value("melee_combat");
		bldr.value("ranged_combat");
		bldr.value("spacer");
		bldr.value("banking");
		bldr.value("financial_advice");
		bldr.value("prayer");
		//Prayer tab
		bldr.value("friends_and_ignores");
		bldr.value("left_church");
		//Friends, ignores
		bldr.value("spacer");
		bldr.value("magic_tab_opened");
		bldr.value("teleport_to_mainland");
		bldr.value("tutorial_complete").setDefault("tutorial_complete").register();
	}
}
