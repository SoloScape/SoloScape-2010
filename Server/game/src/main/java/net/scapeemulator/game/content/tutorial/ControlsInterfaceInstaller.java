package net.scapeemulator.game.content.tutorial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.Container;
import net.scapeemulator.cache.def.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Installs a native IF3 controls overlay in unused build-592 cache slots.
 *
 * The old tutorial-progress interface 371 belongs over the chatbox and is not
 * appropriate for the centred controls popup. We keep the original cache
 * assets intact: interface group 905 and sprite 2489 were both unallocated.
 */
public final class ControlsInterfaceInstaller {
    private static final Logger logger = LoggerFactory.getLogger(ControlsInterfaceInstaller.class);
    public static final int INTERFACE_ID = 905;
    public static final int SPRITE_ID = 2489;
    public static final int WIDTH = 392;
    public static final int HEIGHT = 410;
    private static final Color ORANGE = new Color(237, 150, 54);
    private static final Color RED = new Color(236, 69, 51);

    private ControlsInterfaceInstaller() {
    }

    /** The game server calls this before constructing the JS5 UpdateService. */
    public static void install(Cache cache) throws IOException {
        Sprite sprite = new Sprite(WIDTH, HEIGHT);
        sprite.setFrame(0, renderPanel());
        byte[] spriteBytes = bytes(sprite.encode());
        ByteBuffer child = createWidget(cache);
        byte[] widgetBytes = bytes(child);

        // Write only when the artwork changes. Rewriting every boot changes
        // reference-table CRCs and forces unnecessary client cache downloads.
        if (!matches(cache, 8, SPRITE_ID, spriteBytes)) {
            cache.write(8, SPRITE_ID,
                    new Container(Container.COMPRESSION_GZIP, ByteBuffer.wrap(spriteBytes), 0));
            logger.info("Installed Controls sprite {}.", SPRITE_ID);
        }
        if (!matchesMember(cache, 3, INTERFACE_ID, 0, widgetBytes)) {
            cache.write(3, INTERFACE_ID, 0, ByteBuffer.wrap(widgetBytes));
            logger.info("Installed centred Controls interface {}.", INTERFACE_ID);
        }
    }

    private static byte[] bytes(ByteBuffer data) {
        ByteBuffer view = data.asReadOnlyBuffer();
        view.position(0);
        byte[] bytes = new byte[view.remaining()];
        view.get(bytes);
        return bytes;
    }

    private static boolean matches(Cache cache, int type, int id, byte[] expected) {
        try {
            if (id >= cache.getFileCount(type))
                return false;
            return Arrays.equals(bytes(cache.read(type, id).getData()), expected);
        } catch (IOException | IndexOutOfBoundsException | java.nio.BufferUnderflowException missing) {
            return false;
        }
    }

    private static boolean matchesMember(Cache cache, int type, int group, int child, byte[] expected) {
        try {
            return Arrays.equals(bytes(cache.read(type, group, child)), expected);
        } catch (IOException | IndexOutOfBoundsException missing) {
            return false;
        }
    }

    /**
     * Copy a stock noninteractive IF3 sprite component, preserving its
     * supported script/listener structure. The only alterations are sprite
     * reference, parent, size, and centred alignment.
     */
    public static ByteBuffer createWidget(Cache cache) throws IOException {
        byte[] data = bytes(cache.read(3, 159, 46));
        if (data.length != 74 || (data[0] & 255) != 255 || data[1] != 5)
            throw new IOException("Unexpected build-592 sprite widget format");
        ByteBuffer widget = ByteBuffer.wrap(data);
        widget.putShort(4, (short) 0); // x offset
        widget.putShort(6, (short) 0); // y offset
        widget.putShort(8, (short) WIDTH);
        widget.putShort(10, (short) HEIGHT);
        widget.put(12, (byte) 0); // absolute width
        widget.put(13, (byte) 0); // absolute height
        widget.put(14, (byte) 1); // centre horizontally
        widget.put(15, (byte) 1); // centre vertically
        widget.putShort(16, (short) 0xFFFF); // top-level child
        widget.put(18, (byte) 0); // visible
        widget.putInt(19, SPRITE_ID);
        widget.position(0);
        return widget;
    }

    /**
     * A single framed panel with icon illustrations, baked into one sprite.
     * Text and icons are part of the same image, so no unsupported outgoing
     * sprite or widget-transform packets are needed.
     */
    public static BufferedImage renderPanel() {
        BufferedImage raw = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = raw.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Shadow, chamfered iron/stone frame and recessed dark-brown backing.
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(6, 7, WIDTH - 12, HEIGHT - 12, 12, 12);
        g.setPaint(new GradientPaint(4, 1, new Color(130, 117, 93), 4, HEIGHT - 4,
                new Color(67, 55, 43)));
        g.fillRoundRect(4, 2, WIDTH - 9, HEIGHT - 9, 10, 10);
        g.setColor(new Color(32, 27, 22));
        g.setStroke(new BasicStroke(3f));
        g.drawRoundRect(5, 3, WIDTH - 11, HEIGHT - 11, 9, 9);
        g.setColor(new Color(188, 170, 130));
        g.drawLine(10, 7, WIDTH - 13, 7);
        g.setColor(new Color(33, 27, 21));
        g.fillRect(11, 35, WIDTH - 22, HEIGHT - 49);
        g.setPaint(new GradientPaint(13, 44, new Color(40, 33, 27),
                WIDTH - 12, HEIGHT - 25, new Color(78, 66, 52)));
        g.fillRect(15, 39, WIDTH - 30, HEIGHT - 58);
        g.setColor(new Color(111, 91, 70));
        g.drawRect(12, 36, WIDTH - 25, HEIGHT - 51);
        g.setColor(new Color(14, 11, 9, 200));
        g.drawRect(15, 39, WIDTH - 31, HEIGHT - 59);

        text(g, "Controls", 0, 25, 23, ORANGE, true);

        // Arrow-key cluster: left, down, right, with the up arrow above.
        key(g, 93, 76, "up");
        key(g, 51, 118, "left");
        key(g, 93, 118, "down");
        key(g, 135, 118, "right");
        lines(g, 214, 100, new String[]{"Arrow keys rotate", "the camera."}, ORANGE, 17);

        mouse(g, 94, 206);
        lines(g, 211, 215, new String[]{"Click on the ground", "or minimap to walk."}, ORANGE, 16);

        key(g, 54, 294, "Ctrl");
        text(g, "+", 120, 325, 22, new Color(240, 182, 77), true);
        mouse(g, 152, 290);
        lines(g, 214, 309, new String[]{"CTRL + click to run."}, ORANGE, 16);

        text(g, "Please rotate the camera to begin.", 0, HEIGHT - 29, 18, RED, true);
        g.dispose();

        // The JS5 sprite codec uses an indexed palette (max 255 colours).
        // Quantise the painted graphic to a themed palette, deterministically.
        return quantise(raw);
    }

    private static void key(Graphics2D g, int x, int y, String symbol) {
        int w = "Ctrl".equals(symbol) ? 43 : 39, h = 40;
        g.setPaint(new GradientPaint(x, y, new Color(239, 217, 160),
                x, y + h, new Color(149, 126, 86)));
        g.fillRoundRect(x + 1, y + 1, w, h, 5, 5);
        g.setColor(new Color(46, 36, 26));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w + 1, h + 1, 5, 5);
        g.setColor(new Color(255, 242, 191));
        g.drawLine(x + 4, y + 4, x + w - 5, y + 4);
        g.setColor(new Color(86, 69, 45));
        if ("Ctrl".equals(symbol)) {
            g.setFont(new Font("Georgia", Font.BOLD, 13));
            g.drawString("Ctrl", x + 7, y + 25);
            return;
        }
        int cx = x + w / 2 + 1, cy = y + 20;
        Polygon arrow = new Polygon();
        if ("up".equals(symbol)) {
            arrow.addPoint(cx, cy - 9);arrow.addPoint(cx - 6, cy + 1);
            arrow.addPoint(cx - 2, cy + 1);arrow.addPoint(cx - 2, cy + 9);
            arrow.addPoint(cx + 2, cy + 9);arrow.addPoint(cx + 2, cy + 1);
            arrow.addPoint(cx + 6, cy + 1);
        } else if ("down".equals(symbol)) {
            arrow.addPoint(cx, cy + 9);arrow.addPoint(cx - 6, cy - 1);
            arrow.addPoint(cx - 2, cy - 1);arrow.addPoint(cx - 2, cy - 9);
            arrow.addPoint(cx + 2, cy - 9);arrow.addPoint(cx + 2, cy - 1);
            arrow.addPoint(cx + 6, cy - 1);
        } else if ("left".equals(symbol)) {
            arrow.addPoint(cx - 9, cy);arrow.addPoint(cx + 1, cy - 6);
            arrow.addPoint(cx + 1, cy - 2);arrow.addPoint(cx + 9, cy - 2);
            arrow.addPoint(cx + 9, cy + 2);arrow.addPoint(cx + 1, cy + 2);
            arrow.addPoint(cx + 1, cy + 6);
        } else {
            arrow.addPoint(cx + 9, cy);arrow.addPoint(cx - 1, cy - 6);
            arrow.addPoint(cx - 1, cy - 2);arrow.addPoint(cx - 9, cy - 2);
            arrow.addPoint(cx - 9, cy + 2);arrow.addPoint(cx - 1, cy + 2);
            arrow.addPoint(cx - 1, cy + 6);
        }
        g.fillPolygon(arrow);
    }

    private static void mouse(Graphics2D g, int x, int y) {
        g.setPaint(new GradientPaint(x, y, new Color(224, 223, 217),
                x + 28, y + 58, new Color(83, 85, 83)));
        g.fillRoundRect(x, y, 30, 58, 18, 18);
        g.setColor(new Color(29, 28, 25));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, 30, 58, 18, 18);
        g.setColor(new Color(73, 75, 69));
        g.drawLine(x + 15, y + 2, x + 15, y + 25);
        g.setColor(new Color(166, 59, 16));
        g.fillRoundRect(x + 10, y + 5, 11, 21, 6, 6);
        g.setColor(new Color(43, 36, 24));
        g.fillRoundRect(x + 14, y + 6, 4, 13, 3, 3);
    }

    private static void lines(Graphics2D g, int x, int baseline, String[] lines, Color color, int size) {
        for (int i = 0; i < lines.length; i++)
            text(g, lines[i], x, baseline + i * (size + 3), size, color, false);
    }

    private static void text(Graphics2D g, String text, int x, int baseline,
            int size, Color color, boolean centered) {
        g.setFont(new Font("Georgia", Font.BOLD, size));
        FontMetrics fm = g.getFontMetrics();
        int left = centered ? (WIDTH - fm.stringWidth(text)) / 2 : x;
        g.setColor(new Color(16, 11, 8));
        g.drawString(text, left + 1, baseline + 1);
        g.setColor(color);
        g.drawString(text, left, baseline);
    }

    private static BufferedImage quantise(BufferedImage source) {
        List<Integer> palette = new ArrayList<>();
        for (int i = 0; i < 45; i++)
            palette.add(rgb(22 + i * 2, 17 + i * 2, 12 + i));
        for (int i = 0; i < 40; i++)
            palette.add(rgb(74 + i * 3, 62 + i * 3, 47 + i * 2));
        for (int i = 0; i < 35; i++)
            palette.add(rgb(128 + i * 3, 106 + i * 3, 77 + i * 3));
        for (int i = 0; i < 32; i++)
            palette.add(rgb(126 + i * 4, 58 + i * 3, 19 + i));
        for (int i = 0; i < 28; i++)
            palette.add(rgb(135 + i * 4, 37 + i * 2, 29 + i));
        for (int i = 0; i < 24; i++)
            palette.add(rgb(90 + i * 7, 93 + i * 7, 91 + i * 7));
        // 204 candidate shades (+ transparent pixel = < 256 palette entries).
        BufferedImage result = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Map<Integer, Integer> cache = new HashMap<>();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int pixel = source.getRGB(x, y);
                int alpha = (pixel >>> 24) & 255;
                if (alpha == 0) {
                    result.setRGB(x, y, 0);
                    continue;
                }
                Integer nearest = cache.get(pixel & 0xFFFFFF);
                if (nearest == null) {
                    int red = (pixel >> 16) & 255;
                    int green = (pixel >> 8) & 255;
                    int blue = pixel & 255;
                    long best = Long.MAX_VALUE;
                    int colour = palette.get(0);
                    for (int candidate : palette) {
                        int dr = red - ((candidate >> 16) & 255);
                        int dg = green - ((candidate >> 8) & 255);
                        int db = blue - (candidate & 255);
                        long distance = dr * dr * 3L + dg * dg * 4L + db * db * 2L;
                        if (distance < best) {
                            best = distance;
                            colour = candidate;
                        }
                    }
                    nearest = colour;
                    cache.put(pixel & 0xFFFFFF, nearest);
                }
                result.setRGB(x, y, (alpha << 24) | nearest);
            }
        }
        return result;
    }

    private static int rgb(int r, int g, int b) {
        return ((Math.min(255, r) & 255) << 16)
                | ((Math.min(255, g) & 255) << 8)
                | (Math.min(255, b) & 255);
    }
}
