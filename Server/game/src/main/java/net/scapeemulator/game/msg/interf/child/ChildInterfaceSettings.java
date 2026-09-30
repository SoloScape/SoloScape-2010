package net.scapeemulator.game.msg.interf.child;

import net.scapeemulator.game.model.hud.interf.type.EntryOnEntryType;
import net.scapeemulator.game.msg.CachedMessage;

public class ChildInterfaceSettings extends CachedMessage {
	private final int interfaceId, childId;
	private int end;
	private int start;
	private int setting = 0;

	public ChildInterfaceSettings(int interfaceId, int childId) {
		this.interfaceId = interfaceId;
		this.childId = childId;
	}

	@Override
	public String toString() {
		return "ChildInterfaceSettings [interfaceId=" + interfaceId + ", childId=" + childId + ", end=" + end
				+ ", start=" + start + ", setting=" + setting + "]";
	}

	public void enableOptions(Integer... options) {
		for (int option : options) {
			setOptionClickAllowed(option, true);
		}
	}

	public void disableOptions(int... options) {
		for (int option : options) {
			setOptionClickAllowed(option, false);
		}
	}

	/**
	 * @param option
	 *            value, 0-10, zero is the one used in dialogues.
	 * @param allowed
	 *            wether this option is enabled or disabled.
	 */
	private void setOptionClickAllowed(int option, boolean allowed) {
		if (option < 0 || option > 10)
			return;
		setting &= ~(0x1 << (option)); // disable
		if (allowed)
			setting |= (0x1 << (option));
	}

	public void setAffectingSlots(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getId() {
		return interfaceId;
	}

	public int getChildId() {
		return childId;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getSetting() {
		return setting;
	}

	public void setDepth(int depth) {
		if (depth < 0 || depth > 7) {
			return;
		}
		setting &= ~(0x7 << 18);
		setting |= (depth << 18);
	}

	public void setInternalInputEnabled(boolean enable) {
		setting &= ~(1 << 21);
		if (enable)
			setting |= (1 << 21);
	}

	public void setExternalInputEnabled(boolean enable) {
		setting &= ~(1 << 22);
		if (enable)
			setting |= (1 << 22);
	}

	public void bit23test(boolean enable) {
		setting &= ~(1 << 23);
		if (enable)
			setting |= (1 << 23);
	}

	public void setEntryOnEntrySettings(EntryOnEntryType[] handlerTypes) {
		int flags = 0;
		for (EntryOnEntryType type : handlerTypes) {
			flags |= 1 << type.ordinal();
		}
		setting &= ~(0x7f << 11);
		setting |= flags << 11;
	}
}
