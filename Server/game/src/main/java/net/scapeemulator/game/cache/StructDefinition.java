package net.scapeemulator.game.cache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.cache.Archive;
import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.Container;
import net.scapeemulator.cache.ReferenceTable;
import net.scapeemulator.cache.util.ByteBufferUtils;

/** Integer parameters from build-592 struct definitions (config index 2, archive 26). */
public final class StructDefinition {
    private static Map<Integer, Map<Integer, Integer>> definitions = new HashMap<>();

    private StructDefinition() { }

    public static void init(Cache cache) throws IOException {
        ReferenceTable table = ReferenceTable.decode(
                Container.decode(cache.getStore().read(255, 2)).getData());
        ReferenceTable.Entry entry = table.getEntry(26);
        if (entry == null)
            throw new IOException("Missing struct archive 2/26 from the game cache.");
        Archive archive = Archive.decode(cache.read(2, 26).getData(), entry.size());
        Map<Integer, Map<Integer, Integer>> loaded = new HashMap<>();
        int dense = 0;
        for (int id = 0; id < entry.capacity(); id++) {
            if (entry.getEntry(id) == null)
                continue;
            Map<Integer, Integer> params = decode(archive.getEntry(dense++));
            if (!params.isEmpty())
                loaded.put(id, params);
        }
        definitions = loaded;
    }

    static Map<Integer, Integer> decode(ByteBuffer buffer) throws IOException {
        Map<Integer, Integer> params = new HashMap<>();
        while (buffer.hasRemaining()) {
            int opcode = buffer.get() & 0xff;
            if (opcode == 0)
                break;
            if (opcode != 249)
                throw new IOException("Unexpected struct opcode " + opcode);
            int count = buffer.get() & 0xff;
            for (int i = 0; i < count; i++) {
                boolean string = (buffer.get() & 0xff) == 1;
                int key = ByteBufferUtils.getTriByte(buffer);
                if (string)
                    ByteBufferUtils.getJagexString(buffer);
                else
                    params.put(key, buffer.getInt());
            }
        }
        return params;
    }

    public static Integer getIntParam(int structId, int key) {
        Map<Integer, Integer> params = definitions.get(structId);
        return params == null ? null : params.get(key);
    }
}
