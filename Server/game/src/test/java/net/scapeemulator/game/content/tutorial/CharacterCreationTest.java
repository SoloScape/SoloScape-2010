package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.cache.def.GameConstantContainer;
import net.scapeemulator.game.cache.GameConstants;
import net.scapeemulator.game.cache.VarBitDefinition;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.player.Gender;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CharacterCreationTest {
    private final Map<Integer, GameConstantContainer<?>> previous = new HashMap<>();
    private Map<Integer, VarBitDefinition> originalVarbits;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        int[] enums = {2338, 703, 690, 711, 749, 1586, 1136,
                2341, 1591, 693, 751, 1607, 1137,
                2345, 2349, 2351, 753, 748};
        for (int id : enums) {
            previous.put(id, GameConstants.getDefMap().get(id));
            Map<Integer, Integer> options = new HashMap<>();
            options.put(0, id == 753 ? 5783592 : 10 + id % 20);
            options.put(1, id == 753 ? 4210688 : 11 + id % 20);
            GameConstants.getDefMap().put(id, new GameConstantContainer<>(0, options));
        }
        Field field = VarBitDefinition.class.getDeclaredField("definitions");
        field.setAccessible(true);
        originalVarbits = (Map<Integer, VarBitDefinition>) field.get(null);
        Map<Integer, VarBitDefinition> definitions = new HashMap<>();
        if (originalVarbits != null) definitions.putAll(originalVarbits);
        // varbit 6501 is stored in varp 1158 and has two bits.
        definitions.put(6501, VarBitDefinition.decode(6501,
                ByteBuffer.wrap(new byte[]{1, 4, (byte) 134, 0, 2, 0})));
        field.set(null, definitions);
    }

    @After
    public void tearDown() throws Exception {
        for (Map.Entry<Integer, GameConstantContainer<?>> entry : previous.entrySet()) {
            if (entry.getValue() == null) GameConstants.getDefMap().remove(entry.getKey());
            else GameConstants.getDefMap().put(entry.getKey(), entry.getValue());
        }
        Field field = VarBitDefinition.class.getDeclaredField("definitions");
        field.setAccessible(true);
        field.set(null, originalVarbits);
    }

    private void click(Player player, int option, int child, int slot) {
        CharacterCreation.INSTANCE.dispatch(player, new ChildInterfaceClickAction(
                option, 900, child, slot, -1));
    }

    @Test
    public void firstLoginShowsCreationAndTracksGenderCategoryStylesAndColours() {
        Player player = new Player();
        player.setUsername("newplayer");
        player.setCharacterCreationRequired(true);
        player.getDisplay().initialize(DisplayMode.FIXED);
        CharacterCreation.openIfRequired(player);
        assertSame(CharacterCreation.INSTANCE, player.getDisplay().getInterface(900));
        assertEquals(Gender.MALE, player.getAppearance().getGender());
        assertEquals(0, player.getStatus().getVarC(1020));

        // Plain-click buttons use option 0; generated choice grids use option 1.
        click(player, 0, 20, -1);
        assertEquals(Gender.FEMALE, player.getAppearance().getGender());
        assertEquals(1, player.getStatus().getWaitingVar(1158));

        click(player, 1, 37, -1); // Hair category
        assertEquals(1, player.getCharacterCreationCategory());
        assertEquals(1, player.getStatus().getVarC(1020));
        click(player, 1, 66, 5); // Second hairstyle (3 children per option)
        assertEquals(12, player.getAppearance().getStyle(0)); // enum 2341: 11 + (2341 % 20)
        assertEquals(12, player.getStatus().getVarC(1008));
        click(player, 1, 73, 3); // Second hair colour
        assertEquals(16, player.getAppearance().getColor(0)); // enum 2345: 11 + (2345 % 20)

        click(player, 1, 41, -1); // Female characters have no beard category.
        assertEquals(1, player.getCharacterCreationCategory());

        click(player, 1, 61, -1); // Footwear colour uses its index, not its RGB value.
        click(player, 1, 73, 3);
        assertEquals(1, player.getAppearance().getColor(3));
        click(player, 0, 15, -1);
        assertEquals(Gender.MALE, player.getAppearance().getGender());
        assertEquals(0, player.getCharacterCreationCategory());
        assertEquals(0, player.getStatus().getVarC(1020));

        // Do not mark a character confirmed if no session is available to persist it.
        click(player, 0, 81, -1);
        assertTrue(player.isCharacterCreationRequired());
        assertSame(CharacterCreation.INSTANCE, player.getDisplay().getInterface(900));
    }

    @Test
    public void returningPlayersDoNotOpenCharacterCreationOrAcceptCreationClicks() {
        Player player = new Player();
        player.setUsername("existing");
        player.getDisplay().initialize(DisplayMode.FIXED);
        CharacterCreation.openIfRequired(player);
        assertNull(player.getDisplay().getInterface(900));
        click(player, 0, 20, -1);
        assertEquals(Gender.MALE, player.getAppearance().getGender());
    }
}
