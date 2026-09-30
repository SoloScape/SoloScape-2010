package net.scapeemulator.cache.tools;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.Container;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.cache.ReferenceTable;
import net.scapeemulator.cache.def.InvType;

import java.io.IOException;
import java.io.PrintStream;

public final class InvTypePrinter {

	public static void main(String[] args) throws IOException {
		PrintStream out = new PrintStream("./invtype.txt");
		try (Cache cache = new Cache(FileStore.open("../resources/cache/"))) {
			ReferenceTable rt = ReferenceTable.decode(Container.decode(cache.getStore().read(255, 2)).getData());
			int memberCount = rt.getEntry(5).capacity();
			out.println("InvTypes: " + memberCount);
			for (int id = 0; id < memberCount; id++) {
				InvType invType = InvType.decode(id, cache.read(2, 5, id));
				out.println(invType);
			}
			out.flush();
			out.close();
		}
	}
}
