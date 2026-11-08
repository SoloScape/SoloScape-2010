package net.scapeemulator.game.model.hud.util;

import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.interf.child.SpriteChild;

public class ObjectiveTab extends Tab {
	public static final ObjectiveTab INSTANCE = new ObjectiveTab();

	public ObjectiveTab() {
		super(891, Tab.OBJECTIVE);
		addChild(new TestChild());
	}

	public static final class TestChild extends SpriteChild {

		public TestChild() {
			super(14, -1);
		}
	}
}