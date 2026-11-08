package net.scapeemulator.game.model.hud;

import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.player.Player;

/** Cache-backed tabs whose server-side content handlers are not implemented yet. */
public final class AdditionalTabs {
	private static final Tab[] TABS = {
		new Tab(190, Tab.QUEST),
		new Tab(259, Tab.ACHIEVEMENTS),
		new Tab(192, Tab.MAGIC),
		new Tab(550, Tab.FRIENDS),
		new Tab(551, Tab.IGNORES),
		new Tab(589, Tab.CLAN),
		new Tab(464, Tab.EMOTES),
		new Tab(34, Tab.NOTES)
	};

	private AdditionalTabs() {
	}

	public static Tab forSlot(int slot) {
		for (Tab tab : TABS)
			if (tab.getSlot() == slot)
				return tab;
		return null;
	}

	public static void open(Player player) {
		for (Tab tab : TABS) {
			player.getDisplay().open(tab, false);
		}
	}
}
