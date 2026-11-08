package net.scapeemulator.game.model.hud.settings;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.PaneInterface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class GraphicsSettings extends PaneInterface {
	public static final Interface INSTANCE = new GraphicsSettings();

	public GraphicsSettings() {
		super(742, InterfacePersistence.TRANSIENT);
	}
}
