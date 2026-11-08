package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.cache.def.GameConstantContainer;
import net.scapeemulator.game.cache.GameConstants;
import net.scapeemulator.game.cache.StructDefinition;
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
    private Map<Integer, Map<Integer, Integer>> originalStructs;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        int[] enums = {2338, 703, 690, 711, 749, 1586, 1136,
                2341, 1591, 693, 751, 1607, 1137,
                2345, 2349, 2351, 1589, 748};
        for (int id : enums) {
            previous.put(id, GameConstants.getDefMap().get(id));
            Map<Integer, Integer> options = new HashMap<>();
            options.put(0, id == 1589 ? 3680 : 10 + id % 20);
            options.put(1, id == 1589 ? 3683 : 11 + id % 20);
            GameConstants.getDefMap().put(id, new GameConstantContainer<>(0, options));
        }
        Field structField = StructDefinition.class.getDeclaredField("definitions");
        structField.setAccessible(true);
        originalStructs = (Map<Integer, Map<Integer, Integer>>) structField.get(null);
        Map<Integer, Map<Integer, Integer>> structs = new HashMap<>(originalStructs);
        structs.put(28, hairStruct(8, 600));
        structs.put(29, hairStruct(12, 601));
        structs.put(11, hairStruct(223, 700));
        structs.put(12, hairStruct(224, 701));
        structField.set(null, structs);

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

    private Map<Integer, Integer> hairStruct(int kit, int chathead) {
        Map<Integer, Integer> params = new HashMap<>();
        params.put(788, kit);
        params.put(791, chathead);
        return params;
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
        Field structField = StructDefinition.class.getDeclaredField("definitions");
        structField.setAccessible(true);
        structField.set(null, originalStructs);
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
        assertEquals(0, player.getStatus().getVarC(1020)); // Do not force the client back to Skin.
        click(player, 1, 66, 5); // Second hairstyle (3 children per option)
        assertEquals(224, player.getAppearance().getStyle(0)); // enum 2341 -> struct 12 -> param 788
        assertEquals(224, player.getStatus().getVarC(1008));
        assertEquals(1, player.getCharacterCreationCategory());
        click(player, 1, 73, 3); // Second hair colour
        assertEquals(16, player.getAppearance().getColor(0)); // enum 2345: 11 + (2345 % 20)
        assertEquals(1, player.getCharacterCreationCategory());

        click(player, 1, 41, -1); // Female characters have no beard category.
        assertEquals(1, player.getCharacterCreationCategory());

        click(player, 1, 61, -1); // Footwear colour enum 1589 uses IDs offset by 3680.
        click(player, 1, 73, 3);
        assertEquals(3, player.getAppearance().getColor(3));
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
    public void skinSelectionDoesNotAlterClothingAndModelsKeepTheirCategory() {
        Player player = new Player();
        player.setCharacterCreationRequired(true);
        player.getDisplay().initialize(DisplayMode.FIXED);
        CharacterCreation.openIfRequired(player);
        int originalTop = player.getAppearance().getColor(1);
        int originalLegs = player.getAppearance().getColor(2);

        click(player, 0, 33, -1); // Skin tab
        click(player, 1, 73, 3); // Select a different skin colour
        assertEquals(19, player.getAppearance().getColor(4));
        assertEquals(originalTop, player.getAppearance().getColor(1));
        assertEquals(originalLegs, player.getAppearance().getColor(2));

        click(player, 0, 45, -1); // Top model category
        click(player, 1, 66, 5); // Choose model two
        assertEquals(3, player.getCharacterCreationCategory());
        assertEquals(21, player.getAppearance().getStyle(2));
        click(player, 0, 49, -1); // Sleeves model category
        click(player, 1, 66, 5);
        assertEquals(4, player.getCharacterCreationCategory());
        assertEquals(22, player.getAppearance().getStyle(3));
        assertEquals(originalTop, player.getAppearance().getColor(1));
    }

    @Test
    public void selectingEveryMaleModelCategoryRetainsTheChosenTab() {
        Player player = new Player();
        player.setCharacterCreationRequired(true);
        player.getDisplay().initialize(DisplayMode.FIXED);
        CharacterCreation.openIfRequired(player);

        int[] buttons = {37, 41, 45, 49, 53, 57, 61};
        int[] kits = {12, 14, 21, 22, 20, 17, 27};
        for (int i = 0; i < buttons.length; i++) {
            click(player, 0, buttons[i], -1);
            assertEquals(i + 1, player.getCharacterCreationCategory());
            click(player, 1, 66, 5);
            assertEquals(i + 1, player.getCharacterCreationCategory());
            assertEquals(kits[i], player.getAppearance().getStyle(i));
            assertEquals(kits[i], player.getStatus().getVarC(1008 + i));
        }
        assertEquals(0, player.getStatus().getVarC(1020));
    }

    @Test
    public void openAfterPlayerUpdateIsIdempotentWhileChangingAppearance() {
        Player player = new Player();
        player.setCharacterCreationRequired(true);
        player.getDisplay().initialize(DisplayMode.FIXED);
        CharacterCreation.openIfRequired(player);
        click(player, 0, 37, -1); // Hair
        click(player, 1, 66, 5);
        int hair = player.getAppearance().getStyle(0);

        // Called on every player update; an already-open interface must not
        // normalize appearance again or jump the client back to Skin.
        CharacterCreation.openIfRequired(player);
        assertSame(CharacterCreation.INSTANCE, player.getDisplay().getInterface(900));
        assertEquals(1, player.getCharacterCreationCategory());
        assertEquals(hair, player.getAppearance().getStyle(0));
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
