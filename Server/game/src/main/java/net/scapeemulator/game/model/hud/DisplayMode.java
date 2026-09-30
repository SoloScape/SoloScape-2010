package net.scapeemulator.game.model.hud;

import net.scapeemulator.game.model.hud.interf.Interface;

public enum DisplayMode {
	BACKDOOR(FixedHUD.INSTANCE), FIXED(FixedHUD.INSTANCE), RESIZED(ResizedHUD.INSTANCE), FULLSCREEN(
			ResizedHUD.INSTANCE);
	public static final int FULLSCREEN_INTERFACE = -1;
	private final Interface root;
	// TODO: Some of these need checking.
	private final int tabRoot, chatRoot, chatOpRoot;
	private final int paneRoot, overChatboxRoot, blockingTabRoot;
	private final int centerRoot, infoRoot, orbRoot;

	private DisplayMode(Interface root) {
		this.root = root;
		if (root.getId() == 548) {
			tabRoot = 152;
			chatRoot = 142;
			chatOpRoot = 20;
			paneRoot = 5;
			centerRoot = 6;
			overChatboxRoot = 5;
			blockingTabRoot = 147;
			infoRoot = 12;
			orbRoot = 134;
		} else {
			tabRoot = 33;
			chatRoot = 18;
			chatOpRoot = 15;
			centerRoot = 8;
			paneRoot = 6;
			overChatboxRoot = 5;
			blockingTabRoot = 30;
			infoRoot = 11;
			orbRoot = 169;
		}
	}

	public Interface getRoot() {
		return root;
	}

	public int getTabRoot() {
		return tabRoot;
	}

	public int getChatRoot() {
		return chatRoot;
	}

	public int getChatOpRoot() {
		return chatOpRoot;
	}

	public int getPaneRoot() {
		return paneRoot;
	}

	public int getOverChatboxRoot() {
		return overChatboxRoot;
	}

	public int getBlockingTabRoot() {
		return blockingTabRoot;
	}

	public int getInfoRoot() {
		return infoRoot;
	}

	public int getCenterRoot() {
		return centerRoot;
	}

	public int getOrbRoot() {
		return orbRoot;
	}
}
