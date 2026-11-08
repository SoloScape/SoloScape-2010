package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.tools.ControlsInterfaceInstaller;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.cache.def.Sprite;
import net.scapeemulator.game.model.hud.DisplayMode;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public final class ControlsInterfaceInstallerTest {
    @Test
    public void nativeControlsArtworkEncodesAsValidBuild592Sprite() throws Exception {
        BufferedImage image = ControlsInterfaceInstaller.renderPanel();
        assertEquals(ControlsInterfaceInstaller.DISPLAY_WIDTH, image.getWidth());
        assertEquals(ControlsInterfaceInstaller.DISPLAY_HEIGHT, image.getHeight());
        assertEquals("Artwork centre must be opaque", 255, image.getRGB(155, 164) >>> 24);
        Sprite sprite = new Sprite(image.getWidth(), image.getHeight());
        sprite.setFrame(0, image);
        Sprite reloaded = Sprite.decode(sprite.encode());
        assertEquals(1, reloaded.size());
        assertEquals(image.getRGB(130, 250), reloaded.getFrame(0).getRGB(130, 250));
        assertEquals(image.getRGB(160, 20), reloaded.getFrame(0).getRGB(160, 20));
    }

    @Test
    public void controlWidgetUsesUnusedNativeInterfaceSlotAndCentredSprite() throws Exception {
        try (Cache cache = new Cache(FileStore.open("../data/cache"))) {
            ByteBuffer root = ControlsInterfaceInstaller.createRootWidget(cache);
            assertEquals(0, root.get(1));
            assertEquals(512, root.getShort(8) & 65535);
            assertEquals(334, root.getShort(10) & 65535);
            assertEquals(65535, root.getShort(16) & 65535);

            ByteBuffer widget = ControlsInterfaceInstaller.createWidget(cache);
            assertEquals(74, widget.limit());
            assertEquals(255, widget.get(0) & 255);
            assertEquals(5, widget.get(1));
            assertEquals(ControlsInterfaceInstaller.DISPLAY_WIDTH, widget.getShort(8) & 65535);
            assertEquals(ControlsInterfaceInstaller.DISPLAY_HEIGHT, widget.getShort(10) & 65535);
            assertEquals(1, widget.get(14));
            assertEquals(1, widget.get(15));
            assertEquals(0, widget.getShort(16) & 65535);
            assertEquals(ControlsInterfaceInstaller.SPRITE_ID, widget.getInt(19));
        }
        assertEquals(DisplayMode.FIXED.getCenterRoot(),
                ControlsTutorial.INSTANCE.getDisplaySlot(DisplayMode.FIXED));
        assertEquals(DisplayMode.RESIZED.getCenterRoot(),
                ControlsTutorial.INSTANCE.getDisplaySlot(DisplayMode.RESIZED));
        assertNotEquals(371, ControlsTutorial.INSTANCE.getId());
    }
}
