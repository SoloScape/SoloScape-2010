package net.scapeemulator.game.model.hud.util;

import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.def.TrackDefinition;
import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.child.TextChild;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.var.PVariable.Type;
import net.scapeemulator.game.model.player.var.SimpleIntegerVarBit;
import net.scapeemulator.game.model.player.var.SimpleIntegerVariable;
import net.scapeemulator.game.model.player.var.Trigger;
import net.scapeemulator.game.model.player.var.VarBitParentVariable;
import net.scapeemulator.game.model.player.var.VarBitVariable;
import net.scapeemulator.game.task.Task;

public class MusicTab extends Tab {
	public static final Interface INSTANCE = new MusicTab();
	private static final int[] MUSIC_VARS = new int[] { 20, 21, 22, 23, 24, 25, 298, 311, 346, 414, 464, 598, 662, 721,
			906, 1009, 1104, 1136, 1180, 1202, 1381, 1394, 1434, 1596 };

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case OPEN_INTER:
			for (int i : TrackDefinition.getUnlocks()) {
				World.getWorld().getTaskScheduler().schedule(new UnlockTask(player, i));
			}
			break;
		default:
			break;
		}
	}

	public MusicTab() {
		super(187, Tab.MUSIC);
		addChild(new TrackList());
		addChild(new NowPlaying());
		// TODO: Playlist childs
	}

	public static final class NowPlaying extends TextChild {

		public NowPlaying() {
			super(4, "");
		}

		@Override
		public String getText(Player player) {
			int value = (int) player.getStatus().getWaitingStatus("music_now_playing");
			if (value == -1) {
				return "";
			}
			TrackDefinition def = TrackDefinition.get(value);
			if (def == null)
				return "";
			player.send(def);
			return def.getName();
		}
	}

	public class TrackList extends ChildInterface {

		public TrackList() {
			super(1);
			addOption(1, new PlayTrackAction(), false);
			setSettings(187);
		}

		@Override
		public int size() {
			return (TrackDefinition.getLastTrackId() << 1) | 1;
		}
	}

	public class PlayTrackAction implements ClickActionHandler {

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			int trackId = action.getSlot() >> 1;
			if (unlocked(player, trackId)) {
				player.getStatus().setStatus("music_now_playing", trackId);
			} else {
				player.sendMessage("You haven't unlocked this track yet.", 0);
				player.sendMessage("You haven't unlocked this track yet.", 99);
			}
		}
	}

	public static final class TrackChangeTrigger extends Trigger<Integer> {

		@Override
		public void trigger(Player player, Integer current) {
			MusicTab.INSTANCE.refresh(player, MusicTab.INSTANCE.getChild(4));
			// TODO Remove!
			World.getWorld().getTaskScheduler().schedule(new UnlockTask(player, current));
		}
	}

	public static final class UnlockTask extends Task {
		private Player player;
		private int track;

		public UnlockTask(Player player, int track) {
			super(1, true);
			this.player = player;
			this.track = track;

		}

		@Override
		public void execute() {
			unlock(player, track);
			stop();
		}
	}

	public static void unlock(Player player, int trackId) {
		TrackDefinition track = TrackDefinition.get(trackId);
		if (track != null && !track.isLocked()) {
			int varIndex = trackId >> 5;
			int varSlot = trackId & 31;
			if (varIndex >= MUSIC_VARS.length)
				return;
			player.getStatus().setVar(MUSIC_VARS[varIndex],
					player.getStatus().getWaitingVar(MUSIC_VARS[varIndex]) | (1 << varSlot));
		}
		player.sendMessage("Unlock " + TrackDefinition.get(trackId), 99);
	}

	public static boolean unlocked(Player player, int trackId) {
		int varIndex = trackId >> 5;
		int varSlot = trackId & 31;
		return (player.getStatus().getCurrentVar(MUSIC_VARS[varIndex]) & (1 << varSlot)) != 0;
	}

	public static void buildVariables() {
		for (int ix = 0; ix < MUSIC_VARS.length; ix++) {
			SimpleIntegerVariable.Builder builder = new SimpleIntegerVariable.Builder("music_unlocked_" + (ix << 5));
			builder.id(MUSIC_VARS[ix]).persist().type(Type.VARP).register();
		}
		SimpleIntegerVariable.Builder builder = new SimpleIntegerVariable.Builder("music_now_playing");
		builder.id(1189).setTrigger(new TrackChangeTrigger()).setDefault(16).persist().type(Type.VARP).register();

		for (int outer = 0; outer < 6; outer++) {
			VarBitParentVariable.Builder parent = new VarBitParentVariable.Builder("playlist_track_pair_" + outer);
			parent.persist().id(1621 + outer).type(Type.VARBIT);
			switch (outer) {
			case 0:
				/*
				 * TODO: Triggers
				 */
				VarBitVariable.Builder<Boolean> child = new VarBitVariable.Builder<Boolean>("music_playlist_state");
				child.id(7078).value(Boolean.FALSE).value(Boolean.TRUE).setDefault(Boolean.FALSE);
				parent.value(child.register());

				child = new VarBitVariable.Builder<Boolean>("music_playlist_shuffle");
				child.id(7079).value(Boolean.FALSE).value(Boolean.TRUE).setDefault(Boolean.FALSE);
				parent.value(child.register());
				break;
			}
			SimpleIntegerVarBit.Builder child;
			for (int inner = 0; inner < 2; inner++) {
				child = new SimpleIntegerVarBit.Builder("music_playlist_track_" + ((outer * 2) + inner + 1));
				child.id(7081 + (outer * 2) + inner).setDefault(32767);
				parent.value(child.register());
			}
			parent.register();
		}
	}
}
