package net.scapeemulator.game.tools;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.game.cache.VarBitDefinition;

public class VarBitPrinter {

	private static final Logger logger = LoggerFactory.getLogger(VarBitPrinter.class);
	private static final String compareSourceRoot = "../resources/";// Older

	public VarBitPrinter(Cache source) throws IOException {
		VarBitDefinition.init(source);
		work();
	}

	private void work() {
		for (int i = 7078; i <= 7092; i++)
			System.out.println(VarBitDefinition.forId(i).toString());
	}

	public static void main(String[] args) throws IOException {
		logger.info("Starting varbitdumper!");
		Cache source = new Cache(FileStore.open(compareSourceRoot + "cache"));
		new VarBitPrinter(source);
	}

	public static Logger getLogger() {
		return logger;
	}
}
