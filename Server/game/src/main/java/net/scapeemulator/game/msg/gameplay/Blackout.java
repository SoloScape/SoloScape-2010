package net.scapeemulator.game.msg.gameplay;

import net.scapeemulator.api.message.Message;

public enum Blackout implements Message {
	NONE, MINIMAP_UNUSABLE, MINIMAP_BLACKOUT, COMPASS_BLACKOUT, COMPASS_BLACKOUT_MINIMAP_UNUSABLE, TOTAL_BLACKOUT
}
