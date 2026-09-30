package net.scapeemulator.game.model.hud.interf.child;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.player.Player;

public final class SettingChild extends ChildInterface {

	public SettingChild(int childId, String setting) {
		super(childId);
		addOption(1, new SettingAction(setting), false);
	}

	private final static class SettingAction implements ClickActionHandler {
		private String setting;

		public SettingAction(String setting) {
			this.setting = setting;
		}

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			player.getStatus().toggle(setting);
		}
	}
}