package net.scapeemulator.game.model.hud.combat;

import net.scapeemulator.game.model.constants.Interfaces;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.interf.child.InterfaceOpenButton;

public class EquipmentTab extends Tab {
	public static final Interface INSTANCE = new EquipmentTab();

	public EquipmentTab() {
		super(Interfaces.EQUIPMENT, Tab.EQUIPMENT);
		addChild(new InterfaceOpenButton(39, "Equipment Stats", EquipmentBonus.INSTANCE));
	}
}
