package net.scapeemulator.game.model.hud;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.player.Player;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdditionalTabsTest {
	private static final int[] IDS = {190, 259, 192, 550, 551, 589, 464, 34};
	private static final int[] SLOTS = {Tab.QUEST, Tab.ACHIEVEMENTS, Tab.MAGIC, Tab.FRIENDS,
		Tab.IGNORES, Tab.CLAN, Tab.EMOTES, Tab.NOTES};

	@Test
	public void missingTabsOpenInEveryDisplayModeAndSurviveClosingDialogs() {
		for (DisplayMode mode : DisplayMode.values()) {
			Player player = new Player();
			player.getDisplay().initialize(mode);
			AdditionalTabs.open(player);
			player.getDisplay().closeInterfaces();
			assertTabs(player, mode);
		}
	}

	@Test
	public void tabsRemainRegisteredAfterResizingAndRepeatedInitialization() {
		Player player = new Player();
		player.getDisplay().initialize(DisplayMode.FIXED);
		AdditionalTabs.open(player);
		for (DisplayMode mode : new DisplayMode[] {DisplayMode.RESIZED, DisplayMode.FULLSCREEN,
				DisplayMode.FIXED}) {
			player.getDisplay().changeDisplayMode(mode);
			AdditionalTabs.open(player);
			assertTabs(player, mode);
		}
	}

	private void assertTabs(Player player, DisplayMode mode) {
		for (int i = 0; i < IDS.length; i++) {
			Interface tab = player.getDisplay().getInterface(IDS[i]);
			assertNotNull("Missing interface " + IDS[i] + " in " + mode, tab);
			assertEquals(mode.getTabRoot() + SLOTS[i], tab.getDisplaySlot(mode));
		}
	}
}
