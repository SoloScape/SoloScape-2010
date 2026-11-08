package net.scapeemulator.game.cache;

import org.junit.Test;
import java.nio.ByteBuffer;
import java.util.Map;
import static org.junit.Assert.assertEquals;

public class StructDefinitionTest {
    @Test
    public void extractsHairKitAndAlternateModelFromBuild592StructParams() throws Exception {
        // Archive 2/26, struct 742: param 788=1 (wearable hair kit),
        // param 791=170 (alternate model). Do not confuse the two.
        byte[] raw = {
            (byte)249, 2,
            0, 0, 3, 20, 0, 0, 0, 1,
            0, 0, 3, 23, 0, 0, 0, (byte)170,
            0
        };
        Map<Integer, Integer> params = StructDefinition.decode(ByteBuffer.wrap(raw));
        assertEquals(Integer.valueOf(1), params.get(788));
        assertEquals(Integer.valueOf(170), params.get(791));
    }
}
