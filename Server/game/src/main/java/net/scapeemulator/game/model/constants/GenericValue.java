package net.scapeemulator.game.model.constants;

public class GenericValue {
	/**
	 * Start Constant Values.
	 */

	public static final int BOOK_ITEM_ID = 813;

	public static final int RENDER_PACK = 644;
	public static final int WEAPON_TYPE = 686;
	public static final int WEAPON_HAS_SPECIAL = 687;

	public static final int ATTACK_SPEED = 1024;
	public static final int ATTACK_ANIMATION_STYLE_1 = 1025;
	public static final int ATTACK_ANIMATION_STYLE_2 = 1026;
	public static final int ATTACK_ANIMATION_STYLE_3 = 1027;
	public static final int ATTACK_ANIMATION_STYLE_4 = 1028;
	public static final int DEFEND_ANIMATION = 1029;
	public static final int[] ATTACK_ANIMATIONS = new int[] { ATTACK_ANIMATION_STYLE_1, ATTACK_ANIMATION_STYLE_2,
			ATTACK_ANIMATION_STYLE_3, ATTACK_ANIMATION_STYLE_4 };

	public static final int OFFENSIVE_BONUS_STAB = 1031;
	public static final int OFFENSIVE_BONUS_SLASH = 1032;
	public static final int OFFENSIVE_BONUS_CRUSH = 1033;
	public static final int OFFENSIVE_BONUS_MAGIC = 1034;
	public static final int OFFENSIVE_BONUS_RANGED = 1035;
	public static final int OFFENSIVE_BONUS_SUMMONING = 1036;

	public static final int DEFENSIVE_BONUS_STAB = 1037;
	public static final int DEFENSIVE_BONUS_SLASH = 1038;
	public static final int DEFENSIVE_BONUS_CRUSH = 1039;
	public static final int DEFENSIVE_BONUS_MAGIC = 1040;
	public static final int DEFENSIVE_BONUS_RANGED = 1041;
	public static final int DEFENSIVE_BONUS_SUMMONING = 1042;

	public static final int BONUS_STRENGTH = 1044;
	public static final int BONUS_RANGED_STRENGTH = 1045;
	public static final int BONUS_PRAYER = 1046;
	public static final int BONUS_MAGIC_DAMAGE = 1047;

	public static final int EQUIPMENT_REQUIRED_QUEST_ID_1 = 743;

	public static final int EQUIPMENT_REQUIRED_LEVEL_ID_1 = 749;
	public static final int EQUIPMENT_REQUIRED_LEVEL_ID_2 = 751;
	public static final int EQUIPMENT_REQUIRED_LEVEL_ID_3 = 753;

	public static final int[] EQUIPMENT_SKILL_REQUIREMENTS = new int[] { EQUIPMENT_REQUIRED_LEVEL_ID_1,
			EQUIPMENT_REQUIRED_LEVEL_ID_2, EQUIPMENT_REQUIRED_LEVEL_ID_3 };
	

	public static final int USE_LEVEL_REQUIRED_ID = 770;

	/**
	 * Books.
	 */
	public static final int BOOK_NAME = 924;
	// or could it be examine, idfk
	public static final int BOOK_DESCRIPTION = 925;

	/**
	 * Quest Identification.
	 */
	public static final int QUEST_NAME = 845;
	public static final int QUEST_SORT_NAME = 846;
	public static final int QUEST_ID = 847;
	public static final int QUEST_DIFFICULTY = 848;

	/**
	 * How to get the values:
	 * 
	 * x position: take the value, apply bit and with value 16383.
	 * 
	 * y position: shift 14 to the right, apply bit and with value 16383.
	 * 
	 * Height level: Value shifted 28 to the right.
	 * 
	 */
	public static final int QUEST_START_LOCATION = 850;

	/**
	 * Is it a members quest.
	 */
	public static final int QUEST_MEMBERS = 856;

	/**
	 * Quest quest requirements.
	 */
	public static final int QUEST_REQUIREMENT_1 = 859;
	public static final int QUEST_REQUIREMENT_2 = 860;
	public static final int QUEST_REQUIREMENT_3 = 861;
	public static final int QUEST_REQUIREMENT_4 = 862;
	public static final int QUEST_REQUIREMENT_5 = 863;
	public static final int QUEST_REQUIREMENT_6 = 864;
	public static final int QUEST_REQUIREMENT_7 = 865;
	public static final int QUEST_REQUIREMENT_8 = 866;
	public static final int QUEST_REQUIREMENT_9 = 867;
	public static final int QUEST_REQUIREMENT_10 = 868;
	public static final int QUEST_REQUIREMENT_11 = 869;
	public static final int QUEST_REQUIREMENT_12 = 870;

	/**
	 * Start quest skill requirements.
	 */
	public static final int QUEST_SKILL_REQUIREMENT_ID_1 = 871;
	public static final int QUEST_SKILL_REQUIREMENT_LEVEL_1 = 872;
	public static final int QUEST_SKILL_REQUIREMENT_ID_2 = 873;
	public static final int QUEST_SKILL_REQUIREMENT_LEVEL_2 = 874;
	public static final int QUEST_SKILL_REQUIREMENT_ID_3 = 875;
	public static final int QUEST_SKILL_REQUIREMENT_LEVEL_3 = 876;
	public static final int QUEST_SKILL_REQUIREMENT_ID_4 = 877;
	public static final int QUEST_SKILL_REQUIREMENT_LEVEL_4 = 878;
	public static final int QUEST_SKILL_REQUIREMENT_ID_5 = 879;
	public static final int QUEST_SKILL_REQUIREMENT_LEVEL_5 = 880;
	public static final int QUEST_SKILL_REQUIREMENT_ID_6 = 881;
	public static final int QUEST_SKILL_REQUIREMENT_LEVEL_6 = 882;
	public static final int QUEST_SKILL_REQUIREMENT_ID_7 = 883;
	public static final int QUEST_SKILL_REQUIREMENT_LEVEL_7 = 884;
	public static final int QUEST_SKILL_REQUIREMENT_ID_8 = 885;
	public static final int QUEST_SKILL_REQUIREMENT_LEVEL_8 = 886;
	public static final int QUEST_SKILL_REQUIREMENT_ID_9 = 887;
	public static final int QUEST_SKILL_REQUIREMENT_LEVEL_9 = 888;
	public static final int QUEST_SKILL_REQUIREMENT_ID_10 = 889;
	public static final int QUEST_SKILL_REQUIREMENT_LEVEL_10 = 890;

	/**
	 * Final requirements.
	 */
	public static final int QUEST_POINT_REQUIREMENT = 895;
	public static final int COMBAT_LEVEL_REQUIREMENT = 896;

	private GenericValue() {
	};
}
