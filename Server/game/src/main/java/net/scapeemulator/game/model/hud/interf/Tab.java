package net.scapeemulator.game.model.hud.interf;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class Tab extends Interface {
	public static final int TAB_COUNT = 19;
	public static final int ATTACK = 0;
	public static final int SKILLS = 1;
	public static final int QUEST = 2;
	public static final int ACHIEVEMENTS = 3;
	public static final int INVENTORY = 4;
	public static final int EQUIPMENT = 5;
	public static final int PRAYER = 6;
	public static final int MAGIC = 7;
	public static final int OBJECTIVE = 8;
	public static final int FRIENDS = 9;
	public static final int IGNORES = 10;
	public static final int CLAN = 11;
	public static final int SETTINGS = 12;
	public static final int EMOTES = 13;
	public static final int MUSIC = 14;
	public static final int NOTES = 15;
	public static final int LOGOUT = 18;

	private final int slot;

	public Tab(int id, int slot) {
		super(id, InterfacePersistence.PERSISTENT);
		this.slot = slot;
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return mode.getTabRoot() + slot;
	}
}
