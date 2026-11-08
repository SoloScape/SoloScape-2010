package net.scapeemulator.game.model.hud.combat;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.child.TextChild;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.mob.Bonus;
import net.scapeemulator.game.model.player.Player;

public class EquipmentBonus extends Interface {
	public static final Interface INSTANCE = new EquipmentBonus();

	public EquipmentBonus() {
		super(667, InterfacePersistence.TRANSIENT);
		for (int i = 0; i < Bonus.AGRESSIVE_NAMES.length; i++) {
			addChild(new BonusChild(36 + i, i, false));
		}
		for (int i = 0; i < Bonus.DEFENSIVE_NAMES.length; i++) {
			addChild(new BonusChild(41 + i, i, true));
		}
		for (int i = 0; i < Bonus.EXTRA_NAMES.length; i++) {
			addChild(new ExtraBonusChild(48 + i, i));
		}
		// 32 weight
	}

	public static final class BonusChild extends TextChild {
		private final boolean defensive;
		private final int bonusId;

		public BonusChild(int childId, int id, boolean defensive) {
			super(childId, "");
			bonusId = id;
			this.defensive = defensive;
		}

		@Override
		public String getText(Player player) {
			int bonus = defensive ? player.getBonus().getDefensive(bonusId) : player.getBonus().getAgressive(bonusId);
			String name = defensive ? Bonus.DEFENSIVE_NAMES[bonusId] : Bonus.AGRESSIVE_NAMES[bonusId];
			return name + ": " + getLead(bonus) + bonus;
		}
	}

	public static final class ExtraBonusChild extends TextChild {
		private int bonusId;

		public ExtraBonusChild(int childId, int bonusId) {
			super(childId, "");
			this.bonusId = bonusId;
		}

		@Override
		public String getText(Player player) {
			int bonus = player.getBonus().getExtra(bonusId);
			return Bonus.EXTRA_NAMES[bonusId] + ": " + getLead(bonus) + bonus + (bonusId == 3 ? '%' : "");
		}
	}

	private static String getLead(int value) {
		return value > 0 ? "+" : "";
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return mode.getPaneRoot();
	}
}
