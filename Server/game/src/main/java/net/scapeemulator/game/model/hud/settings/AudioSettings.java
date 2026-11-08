package net.scapeemulator.game.model.hud.settings;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.PaneInterface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class AudioSettings extends PaneInterface {
	public static final Interface INSTANCE = new AudioSettings();

	public AudioSettings() {
		super(743, InterfacePersistence.TRANSIENT);
	}
}
