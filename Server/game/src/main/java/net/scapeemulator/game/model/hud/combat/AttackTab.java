package net.scapeemulator.game.model.hud.combat;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.mob.combat.SpecialAttack;
import net.scapeemulator.game.model.player.Player;

public class AttackTab extends Tab {
	public static final Interface INSTANCE = new AttackTab();

	public AttackTab() {
		super(884, Tab.ATTACK);
		for (int i = 0; i < 4; i++) {
			addChild(new AttackStyle(i));
		}
		addChild(new AutoRetaliate());
		addChild(new SpecialAttackButton());
	}

	public static final class SpecialAttackButton extends ChildInterface implements ClickActionHandler {

		public SpecialAttackButton() {
			super(4);
			addOption(1, this, false);
		}

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			if ((boolean) player.getStatus().getStatus("using_special")) {
				player.getStatus().setStatus("using_special", false);
				return;
			}
			SpecialAttack specAttack = SpecialAttack.get(player);
			if (specAttack != null) {
				if ((Integer) player.getStatus().getStatus("special_attack_power") < specAttack.getDrain()) {
					player.sendMessage("You don't have enough power left.", 0);
					player.sendMessage("You don't have enough power left.", 99);
					return;
				}
				player.getStatus().setStatus("using_special", true);
			} else {
				player.sendMessage("Special attack missing.");
			}
		}
	}

	public static final class AutoRetaliate extends ChildInterface implements ClickActionHandler {
		public AutoRetaliate() {
			super(15);
			addOption(1, this, false);
		}

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			player.getStatus().toggle("autoretaliate");
		}
	}

	public static final class AttackStyle extends ChildInterface implements ClickActionHandler {
		private int style;

		public AttackStyle(int style) {
			super(11 + style);
			addOption(1, this, false);
			this.style = style;
			setSettings(884);
		}

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			if (style < player.getCombat().getStyles().length)
				player.getStatus().setStatus("attack_style", style);
		}

		@Override
		public int firstSlot() {
			return -1;
		}
	}
}
