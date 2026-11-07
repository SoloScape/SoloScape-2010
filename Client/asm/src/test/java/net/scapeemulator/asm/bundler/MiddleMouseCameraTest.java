package net.scapeemulator.asm.bundler;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;

/** Run against the bundled jar to verify the actual injected input hooks. */
public final class MiddleMouseCameraTest {
    private static final Canvas canvas = new Canvas();
    private static Object mouse;
    private static Class<?> mouseType, helper;

    public static void main(String[] args) throws Exception {
        try (URLClassLoader loader = new URLClassLoader(new URL[] {new File(args[0]).toURI().toURL()}, null)) {
            for (String name : new String[] {"gf", "no", "o"}) {
                Class.forName(name, false, loader).getDeclaredMethods();
            }
            mouseType = loader.loadClass("gf");
            helper = loader.loadClass("net.scapeemulator.asm.bundler.input.MiddleMouseCamera");
            Constructor<?> ctor = mouseType.getDeclaredConstructor(Component.class, boolean.class);
            ctor.setAccessible(true);
            mouse = ctor.newInstance(canvas, false);
            event("mousePressed", MouseEvent.MOUSE_PRESSED, 100, 100, MouseEvent.BUTTON2, InputEvent.BUTTON2_DOWN_MASK);
            event("mouseDragged", MouseEvent.MOUSE_DRAGGED, 110, 105, MouseEvent.NOBUTTON, InputEvent.BUTTON2_DOWN_MASK);
            event("mouseReleased", MouseEvent.MOUSE_RELEASED, 112, 106, MouseEvent.BUTTON2, 0);
            float yaw = angle("yaw", 2000f), pitch = angle("pitch", 2000f);
            equal(1971.2f, yaw);
            equal(2014.4f, pitch);
            float previousYawStep = 28.8f, previousPitchStep = 14.4f;
            for (int i = 0; i < 30; i++) {
                float nextYaw = angle("yaw", yaw), nextPitch = angle("pitch", pitch);
                float yawStep = yaw - nextYaw, pitchStep = nextPitch - pitch;
                if (yawStep < 0 || yawStep > previousYawStep + 0.001f
                        || pitchStep < 0 || pitchStep > previousPitchStep + 0.001f) {
                    throw new AssertionError("Camera must ease out monotonically after release");
                }
                previousYawStep = yawStep;
                previousPitchStep = pitchStep;
                yaw = nextYaw;
                pitch = nextPitch;
            }
            equal(1928f, yaw); // 12 pixels at 6 units per pixel, with no lost motion.
            equal(2036f, pitch);
            equal(2000f, angle("yaw", 2000f));
            equal(2000f, angle("pitch", 2000f));
            equal(0, buttons());
            event("mouseMoved", MouseEvent.MOUSE_MOVED, 200, 200, MouseEvent.NOBUTTON, 0);
            equal(2000f, angle("yaw", 2000f));

            event("mousePressed", MouseEvent.MOUSE_PRESSED, 0, 0, MouseEvent.BUTTON1, InputEvent.BUTTON1_DOWN_MASK);
            equal(1, buttons());
            event("mousePressed", MouseEvent.MOUSE_PRESSED, 0, 0, MouseEvent.BUTTON2, InputEvent.BUTTON2_DOWN_MASK);
            event("mouseReleased", MouseEvent.MOUSE_RELEASED, 0, 0, MouseEvent.BUTTON2, 0);
            equal(1, buttons()); // Middle release must not clear a held left button.
            event("mouseReleased", MouseEvent.MOUSE_RELEASED, 0, 0, MouseEvent.BUTTON1, 0);
            equal(0, buttons());
            event("mousePressed", MouseEvent.MOUSE_PRESSED, 0, 0, MouseEvent.BUTTON3, InputEvent.BUTTON3_DOWN_MASK);
            equal(4, buttons());
            event("mouseReleased", MouseEvent.MOUSE_RELEASED, 0, 0, MouseEvent.BUTTON3, 0);
            equal(0, buttons());

            event("mousePressed", MouseEvent.MOUSE_PRESSED, 0, 0, MouseEvent.BUTTON2, InputEvent.BUTTON2_DOWN_MASK);
            event("mouseDragged", MouseEvent.MOUSE_DRAGGED, 10, 10, MouseEvent.NOBUTTON, InputEvent.BUTTON2_DOWN_MASK);
            angle("yaw", 2000f);
            angle("pitch", 2000f);
            helper.getMethod("reset").invoke(null);
            event("mouseDragged", MouseEvent.MOUSE_DRAGGED, 20, 20, MouseEvent.NOBUTTON, InputEvent.BUTTON2_DOWN_MASK);
            equal(2000f, angle("yaw", 2000f));
            equal(2000f, angle("pitch", 2000f));
            event("mousePressed", MouseEvent.MOUSE_PRESSED, 0, 0, MouseEvent.BUTTON2, InputEvent.BUTTON2_DOWN_MASK);
            event("mouseExited", MouseEvent.MOUSE_EXITED, 10, 10, MouseEvent.NOBUTTON, 0);
            event("mouseDragged", MouseEvent.MOUSE_DRAGGED, 30, 30, MouseEvent.NOBUTTON, InputEvent.BUTTON2_DOWN_MASK);
            equal(2000f, angle("yaw", 2000f));
            System.out.println("PASS: bundled bytecode verification, reduced sensitivity, smooth settling, release, normal clicks, reset and exit");
        }
    }

    private static void event(String method, int id, int x, int y, int button, int modifiers) throws Exception {
        Method m = mouseType.getDeclaredMethod(method, MouseEvent.class);
        m.setAccessible(true);
        m.invoke(mouse, new MouseEvent(canvas, id, 0L, modifiers, x, y, 1, false, button));
    }

    private static float angle(String method, float current) throws Exception {
        return (Float) helper.getMethod(method, float.class).invoke(null, current);
    }

    private static int buttons() throws Exception {
        Field field = mouseType.getDeclaredField("M");
        field.setAccessible(true);
        return field.getInt(mouse);
    }

    private static void equal(float expected, float actual) {
        if (Math.abs(expected - actual) > 0.002f) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
}
