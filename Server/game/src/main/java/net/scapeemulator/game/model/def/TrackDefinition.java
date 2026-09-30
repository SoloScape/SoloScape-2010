package net.scapeemulator.game.model.def;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.cache.def.GameConstantContainer;
import net.scapeemulator.game.cache.GameConstants;

public class TrackDefinition implements Message {
	private static final Logger logger = LoggerFactory.getLogger(TrackDefinition.class);
	private static Map<Integer, TrackDefinition> tracks;
	private static List<Integer> unlocks;
	private static Integer lastTrackId;
	private static Map<Integer, Integer> ids;
	private final String name, unlock;
	private final int fileId;
	private final boolean locked;

	public static final TrackDefinition get(int arrayId) {
		TrackDefinition def = tracks.get(arrayId);
		if (def == null) {
			logger.warn("Track definition " + arrayId + " not found. Reverting to default.");
			def = tracks.get(0);
		}
		return def;
	}

	public TrackDefinition(String name, int fileId, String unlock, boolean locked) {
		this.name = name;
		this.fileId = fileId;
		this.unlock = unlock;
		this.locked = locked;
	}

	public String getName() {
		return name;
	}

	public String getUnlock() {
		return unlock;
	}

	public int getFileId() {
		return fileId;
	}

	public boolean isLocked() {
		return locked;
	}

	public static boolean init() {
		GameConstantContainer<String> names = GameConstants.getStringConstants(1345);
		GameConstantContainer<Integer> defaults = GameConstants.getIntegerConstants(1350);
		GameConstantContainer<Integer> fileIds = GameConstants.getIntegerConstants(1351);
		GameConstantContainer<String> unlock = GameConstants.getStringConstants(1349);
		GameConstantContainer<String> other = GameConstants.getStringConstants(952);
		boolean twoContainer = false;
		if (unlock.getValue(500).equalsIgnoreCase(unlock.getDefaultValue())) {
			twoContainer = true;
		}
		List<Integer> unlocks = new ArrayList<>();
		Map<Integer, TrackDefinition> tracks = new HashMap<>();
		Map<Integer, Integer> ids = new HashMap<>();
		for (Integer track : names.getValues().keySet()) {
			lastTrackId = track;
			GameConstantContainer<String> unlockCon = track >= 500 && twoContainer ? other : unlock;
			tracks.put(track, new TrackDefinition(names.getValue(track), fileIds.getValue(track),
					unlockCon.getValue(track), defaults.getValue(track) == 1));
			ids.put(tracks.get(track).fileId, track);
			if (defaults.getValue(track) == 0 & unlockCon.getValue(track).equalsIgnoreCase("automatically.")) {
				unlocks.add(track);
			}
		}
		TrackDefinition.tracks = Collections.unmodifiableMap(tracks);
		TrackDefinition.unlocks = Collections.unmodifiableList(unlocks);
		TrackDefinition.ids = Collections.unmodifiableMap(ids);
		logger.info("Track Definitions loaded! " + tracks.size());
		return true;
	}

	@Override
	public String toString() {
		return "TrackDefinition [name=" + name + ", unlock=" + unlock + ", fileId=" + fileId + ", locked=" + locked
				+ "]";
	}

	public static Integer getLastTrackId() {
		return lastTrackId;
	}

	public static int getTrackId(int fileId) {
		return ids.get(fileId);
	}

	public static List<Integer> getUnlocks() {
		return unlocks;
	}
}
