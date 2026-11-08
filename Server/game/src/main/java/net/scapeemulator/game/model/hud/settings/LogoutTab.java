package net.scapeemulator.game.model.hud.settings;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.util.ServerMessage;

public class LogoutTab extends Tab {
	public static final Interface INSTANCE = new LogoutTab();

	public LogoutTab() {
		super(182, Tab.LOGOUT);
		addChild(new Button());
	}

	private static final class Button extends ChildInterface implements ClickActionHandler {
		private static final ServerMessage LOGOUT_WAIT = new ServerMessage("You need to be 10 seconds out of combat before logging out!");

		public Button() {
			super(6);
			addOption(1, this, false);
		}

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			try {
				player.logout();
			} catch(Exception e) {
				player.send(LOGOUT_WAIT);
			}
		}
	}
}
