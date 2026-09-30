package net.scapeemulator.game.model.hud.trigger;

import net.scapeemulator.cache.def.GameConstantContainer;
import net.scapeemulator.game.cache.GameConstants;
import net.scapeemulator.game.cache.ItemDefinition;
import net.scapeemulator.game.model.constants.GenericValue;
import net.scapeemulator.game.model.def.EquipmentDefinition;
import net.scapeemulator.game.model.hud.util.ItemDetails;
import net.scapeemulator.game.model.mob.Bonus;
import net.scapeemulator.game.model.player.Player;
//import net.scapeemulator.game.model.player.var.Status;
import net.scapeemulator.game.model.player.var.Trigger;
import net.scapeemulator.game.msg.interf.util.GlobalStringVarMessage;

public class ItemInformationTrigger extends Trigger<Integer> {
	public static GameConstantContainer<String> WORN_NO_REQUIREMENT = GameConstants.getStringConstants(1434);
	public static GameConstantContainer<String> WORN_REQUIREMENT = GameConstants.getStringConstants(1435);

	public static final String tagYellow = "<col=FFFF00>";
	public static final String tagRed = "<col=FF0000>";
	public static final String tagGreen = "<col=00FF00>";
	public static final String tagCol = "</col>";

	/*
	 * 1496 shopid 532 currency wat 946 pricemod root
	 * 
	 * 1052C prayer stat ad
	 * 
	 * script 2452 stops
	 * 
	 * 2688 script objectives tab
	 * 
	 * 1021C goal xp 1023C progress percentage 6505 SKILLID 6503 TargetType 0
	 * none 1 skill 2 quest
	 * 
	 * 743 currency 744 price (-1 free, 0 n/a)
	 */

	@SuppressWarnings("unused")
	@Override
	public void trigger(Player player, Integer current) {
		if (current == -1)
			return;
		ItemDefinition def = ItemDefinition.forId(current);
		EquipmentDefinition equipmentDef = EquipmentDefinition.forId(current);
		player.getDisplay().open(ItemDetails.INSTANCE, true);
		// Status status = player.getStatus();
		String examine = "", wornOn = "", reqUse = "";
		String column1 = "", column2 = "", middleColumn = "";
		if (def != null) {
			examine = "'All examine and no response make player bored.'<br>";
		}
		if (equipmentDef != null) {
			String overlap = "";
			for (int ix = 0; ix < GenericValue.EQUIPMENT_SKILL_REQUIREMENTS.length; ix++) {
				int reqId = GenericValue.EQUIPMENT_SKILL_REQUIREMENTS[ix];
				int skillId = def.getGeneric(reqId, -1);
				int level = def.getGeneric(reqId + 1, 1);
				if (skillId == -1)
					break;
				boolean meet = player.getSkillSet().getMaximumLevel(skillId) >= level;
				overlap += "<br><col=" + (meet ? "00FF00" : "FF0000") + "> Level " + level + " "
						+ GameConstants.getSkillName(skillId);
			}
			int questId = def.getGeneric(GenericValue.EQUIPMENT_REQUIRED_QUEST_ID_1, -1);
			if (questId != -1) {
				overlap += "<br><col=" + (false ? "00FF00" : "FF0000") + "> Quest complete: " + questId;
			}

			int useSkillId = def.getGeneric(GenericValue.USE_LEVEL_REQUIRED_ID, -1);
			if (useSkillId != -1) {
				reqUse += "<br>Requirements to use:";
				int useSkillReq = def.getGeneric(GenericValue.USE_LEVEL_REQUIRED_ID + 1, 1);
				boolean meet = player.getSkillSet().getMaximumLevel(useSkillId) >= useSkillReq;
				reqUse += "<br><col=" + (meet ? "00FF00" : "FF0000") + "> Level " + useSkillReq + " "
						+ GameConstants.getSkillName(useSkillId);
			}
			// TODO: Both hands
			int slot = equipmentDef.getSlot();
			if (overlap.length() == 0) {
				if (slot == 3 && equipmentDef.isTwoHanded())
					wornOn = "<br>Wielded in both hands.";
				else
					wornOn = "<br>" + WORN_NO_REQUIREMENT.getValue(slot);
			} else {
				overlap += "<br>";
				if (slot == 3 && equipmentDef.isTwoHanded())
					wornOn = "<br>Wielded in both hands, requiring:";
				else
					wornOn = "<br>" + WORN_REQUIREMENT.getValue(slot);
			}

			wornOn += overlap;
			column1 = "<br>Attack";
			column2 = "<br>Defence";
			middleColumn = "<br><br>Stab<br>Slash<br>Crush<br>Magic<br>Ranged<br>Summoning";
			for (int i = 0; i < Bonus.DEFENSIVE_NAMES.length; i++) {
				int defe = def.getGeneric(GenericValue.DEFENSIVE_BONUS_STAB + i, 0),
						offe = def.getGeneric(GenericValue.OFFENSIVE_BONUS_STAB + i, 0);
				if (i != Bonus.SUMMONING)
					column1 += "<br>" + tagYellow + getLead(offe) + offe;
				column2 += "<br>" + tagYellow + getLead(defe) + defe;
			}
			column1 += "<br><col=FFFF00>---";
			for (int i = 0; i < 4; i++) {
				column1 += "<br>" + Bonus.EXTRA_NAMES[i];
				int value = def.getGeneric(GenericValue.BONUS_STRENGTH + i, 0);
				column2 += "<br>" + tagYellow + getLead(value) + value;
			}
			column2 += '%';
		}

		player.send(new GlobalStringVarMessage(25, examine));
		player.send(new GlobalStringVarMessage(26, wornOn));
		player.send(new GlobalStringVarMessage(34, reqUse));
		player.send(new GlobalStringVarMessage(35, column1));
		player.send(new GlobalStringVarMessage(36, middleColumn));
		player.send(new GlobalStringVarMessage(52, column2));
	}

	private String getLead(int value) {
		return value > 0 ? "+" : "";
	}
}
