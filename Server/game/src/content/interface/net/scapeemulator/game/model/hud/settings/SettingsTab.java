package net.scapeemulator.game.model.hud.settings;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.interf.child.InterfaceOpenButton;
import net.scapeemulator.game.model.hud.interf.child.SettingChild;

public class SettingsTab extends Tab {
	public static final Interface INSTANCE = new SettingsTab();
	private static final int /* HOUSE_SETTINGS = 8, */ GRAPHICS_SETTINGS = 16, AUDIO_SETTINGS = 18;

	public SettingsTab() {
		super(261, Tab.SETTINGS);
		addChild(new InterfaceOpenButton(GRAPHICS_SETTINGS, "graphics settings", GraphicsSettings.INSTANCE));
		addChild(new InterfaceOpenButton(AUDIO_SETTINGS, "audio settings", AudioSettings.INSTANCE));
		// addChild(new InterfaceOpenButton(HOUSE_SETTINGS, "house options",
		// HouseSettings.INSTANCE));
		addChild(new SettingChild(4, "chateffects"));
		addChild(new SettingChild(5, "split_private_chat"));
		addChild(new SettingChild(6, "mousebuttons"));
		addChild(new SettingChild(7, "accept_aid"));
	}
}
