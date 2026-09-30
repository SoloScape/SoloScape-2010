package net.scapeemulator.game.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.scapeemulator.cache.Archive;
import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.Container;
import net.scapeemulator.cache.ReferenceTable;
import net.scapeemulator.cache.def.GameConstantContainer;
import net.scapeemulator.game.model.def.TrackDefinition;

public class GameConstants {
	private static final Logger logger = LoggerFactory.getLogger(GameConstants.class);
	private static Map<Integer, GameConstantContainer<?>> definitions = new HashMap<>();
	private static GameConstantContainer<String> skillNames;

	public static void init(Cache cache) throws IOException {
		int count = 0;

		Container tableContainer = Container.decode(cache.getStore().read(255, 17));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());

		int files = table.capacity();
		for (int file = 0; file < files; file++) {
			ReferenceTable.Entry entry = table.getEntry(file);
			if (entry == null)
				continue;

			Archive archive = Archive.decode(cache.read(17, file).getData(), entry.size());
			int nonSparseMember = 0;
			for (int member = 0; member < entry.capacity(); member++) {
				ReferenceTable.ChildEntry childEntry = entry.getEntry(member);
				if (childEntry == null)
					continue;

				int id = file * 256 + member;
				GameConstantContainer<?> definition = GameConstantContainer.decode(archive.getEntry(nonSparseMember++));
				definitions.put(id, definition);
				count++;
			}
		}
		logger.info("Loaded " + count + " constant type definitions.");
		initAllTypes();
	}

	public static void initAllTypes() {
		skillNames = GameConstants.getStringConstants(680);
		TrackDefinition.init();
	}

	public static String getSkillName(int skillId) {
		return skillNames.getValue(skillId);
	}

	public static Map<Integer, GameConstantContainer<?>> getDefMap() {
		return definitions;
	}

	public static GameConstantContainer<?> getConstants(int id) {
		return definitions.get(id);
	}

	@SuppressWarnings("unchecked")
	public static GameConstantContainer<String> getStringConstants(int id) {
		return (GameConstantContainer<String>) definitions.get(id);
	}

	@SuppressWarnings("unchecked")
	public static GameConstantContainer<Integer> getIntegerConstants(int id) {
		return (GameConstantContainer<Integer>) definitions.get(id);
	}
}
