package net.scapeemulator.game.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.cache.def.GameConstantContainer;
import net.scapeemulator.game.cache.GameConstants;

public class TypeDefinitionDumper {
	private static final Logger logger = LoggerFactory.getLogger(EquipmentDumper.class);

	public static void main(String[] args) throws IOException {
		logger.info("Dumping type data...");

		Cache cache = new Cache(FileStore.open("../resources/cache"));
		GameConstants.init(cache);
		File file = new File("./dump/");
		if (!file.exists()) {
			file.mkdir();
		}
		file = new File(file, "type.txt");
		if (file.exists()) {
			file.delete();
		}

		file.createNewFile();
		try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
			Map<Integer, GameConstantContainer<?>> defMap = GameConstants.getDefMap();
			for (Entry<Integer, GameConstantContainer<?>> def : defMap.entrySet()) {
				GameConstantContainer<?> type = def.getValue();
				Map<Integer, ?> values = type.getValues();
				if (values.size() != 0) {
					output.write("---------------TYPE " + def.getKey() + "---------------");
					output.newLine();
					if (!type.getDefaultValue().equals("") && !type.getDefaultValue().equals(0)) {
						output.write("Default: " + type.getDefaultValue());
						output.newLine();
					}
					for (Entry<Integer, ?> value : values.entrySet()) {
						output.write("Key: " + value.getKey() + " value: " + value.getValue().toString());
						output.newLine();
					}
					output.newLine();
					output.flush();
				}
			}
		}
	}
}
