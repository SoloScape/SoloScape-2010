package net.scapeemulator.asm.bundler.input;

import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

/** Bridges AWT events to the game thread without touching camera state on AWT. */
public final class MiddleMouseCamera {

	private static boolean dragging;
	private static int lastX, lastY, deltaX, deltaY;
	private static final float SENSITIVITY = 6.0f;
	private static final float SMOOTHING = 0.4f;
	private static float pendingYaw, pendingPitch;

	private MiddleMouseCamera() {
	}

	public static synchronized boolean handle(MouseEvent event) {
		int id = event.getID();
		if (event.getButton() == MouseEvent.BUTTON2) {
			if (id == MouseEvent.MOUSE_PRESSED) {
				dragging = true;
				lastX = event.getX();
				lastY = event.getY();
			} else if (id == MouseEvent.MOUSE_RELEASED) {
				if (dragging) {
					move(event);
				}
				dragging = false;
			}
			event.consume();
			return true;
		}
		if (id == MouseEvent.MOUSE_EXITED || id == MouseEvent.MOUSE_MOVED) {
			dragging = false;
		} else if (id == MouseEvent.MOUSE_DRAGGED) {
			if ((event.getModifiersEx() & InputEvent.BUTTON2_DOWN_MASK) == 0) {
				dragging = false;
			} else if (dragging) {
				move(event);
				event.consume();
				return true;
			}
		}
		return false;
	}

	private static void move(MouseEvent event) {
		deltaX += event.getX() - lastX;
		deltaY += event.getY() - lastY;
		lastX = event.getX();
		lastY = event.getY();
	}

	// The 592 client uses 16384 angle units per revolution (8 per classic unit).
	public static synchronized float yaw(float current) {
		pendingYaw -= deltaX * SENSITIVITY;
		deltaX = 0;
		float step = smooth(pendingYaw);
		pendingYaw -= step;
		return current + step;
	}

	public static synchronized float pitch(float current) {
		pendingPitch += deltaY * SENSITIVITY;
		deltaY = 0;
		float step = smooth(pendingPitch);
		pendingPitch -= step;
		return current + step;
	}

	private static float smooth(float remaining) {
		// Ease out over a few game updates, then drain tiny remainders completely.
		float magnitude = Math.abs(remaining);
		float step = Math.min(magnitude, Math.max(0.001f, magnitude * SMOOTHING));
		return remaining < 0 ? -step : step;
	}

	public static synchronized void reset() {
		dragging = false;
		deltaX = deltaY = 0;
		pendingYaw = pendingPitch = 0;
	}
}
