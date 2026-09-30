package net.scapeemulator.game.model.hud.interf.child;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.type.ChildInterfaceType;
import net.scapeemulator.game.model.player.Player;

public class TextChild extends ChildInterface {
	private final String text;

	public TextChild(int childId, String text) {
		super(childId);
		this.text = text;
	}

	@Override
	public ChildInterfaceType getType() {
		return ChildInterfaceType.TEXT;
	}

	public String getText(Player player) {
		return text;
	}
}
