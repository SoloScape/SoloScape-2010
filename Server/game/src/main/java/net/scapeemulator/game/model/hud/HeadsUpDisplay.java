package net.scapeemulator.game.model.hud;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.InterfaceCloseMessage;
import net.scapeemulator.game.msg.interf.InterfaceMoveMessage;
import net.scapeemulator.game.msg.interf.InterfaceOpenMessage;
import net.scapeemulator.game.msg.interf.InterfaceRootMessage;

public final class HeadsUpDisplay {
	private final Map<Integer, Interface> interfaces = new HashMap<>();
	private final Map<Integer, Integer> pointers = new HashMap<>();

	// Unsure if this is required outside our display.
	private DisplayMode mode;
	private final Player player;
	private Interface root;
	private boolean hasTransient;

	public HeadsUpDisplay(Player player) {
		this.player = player;
	}

	public void initialize(DisplayMode mode) {
		setRoot(mode.getRoot(), true);
		this.mode = mode;
	}

	public void changeDisplayMode(DisplayMode target) {
		if (mode.getRoot() != target.getRoot()) {
			synchronized (pointers) {
				Collection<Interface> inters = new HashSet<>(interfaces.values());
				for (Interface inter : inters) {
					if ((pointers.get(inter.getId()) >> 16) != mode.getRoot().getId())
						continue;
					switchPointer(inter, target);
				}
			}
			if (root.getId() == mode.getRoot().getId())
				setRoot(target.getRoot(), false);
		}
		mode = target;
	}

	public boolean open(Interface inter, boolean forced) {
		try {
			openInterface(inter, forced);
			return true;
		} catch (InterfaceOpenException e) {
			return false;
		}
	}

	public boolean open(Interface inter, int pointer, boolean force) {
		try {
			openInterface(inter, pointer, force);
			return true;
		} catch (InterfaceOpenException e) {
			return false;
		}
	}

	public boolean openRootInterface(Interface inter, boolean force) {
		return open(inter, DisplayMode.FULLSCREEN_INTERFACE, force);
	}

	public void openInterface(Interface inter, boolean force) throws InterfaceOpenException {
		openInterface(inter, inter.getDisplaySlot(mode) | (mode.getRoot().getId() << 16), force);
	}

	public boolean openInterface(Interface inter, int rootInterId, int rootChildId, boolean force) {
		try {
			openInterface(inter, rootChildId | rootInterId << 16, force);
			return true;
		} catch (InterfaceOpenException e) {
			return false;
		}
	}

	public void openInterface(Interface inter, int pointer, boolean force) throws InterfaceOpenException {
		if (pointers.get(inter.getId()) != null)
			return;
		synchronized (pointers) {
			Interface other = interfaces.get(pointer);
			if (other != null) {
				if (pointer == DisplayMode.FULLSCREEN_INTERFACE) {
					if (root.getId() != mode.getRoot().getId()) {
						if (!force)
							throw new InterfaceOpenException();
						root.dispatch(player, InterfaceAction.CLOSE_ACTION);
					}
				} else {
					if (!force)
						throw new InterfaceOpenException();
					root.dispatch(player, InterfaceAction.CLOSE_ACTION);
				}
				pointers.remove(other.getId());
			}
			if (!force && hasTransient) {
				throw new InterfaceOpenException();
			}
			if (inter.getPersistence() == InterfacePersistence.TRANSIENT) {
				hasTransient = true;
			}
			pointers.put(inter.getId(), pointer);
			interfaces.put(pointer, inter);
			if (pointer != -1) {
				player.send(new InterfaceOpenMessage(pointer, inter));
			} else {
				setRoot(inter, false);
			}
		}
		inter.dispatch(player, InterfaceAction.OPEN_ACTION);
	}

	private void setRoot(Interface root, boolean refresh) {
		synchronized (pointers) {
			if (this.root != null)
				pointers.remove(this.root.getId());
			pointers.put(root.getId(), DisplayMode.FULLSCREEN_INTERFACE);
			interfaces.put(DisplayMode.FULLSCREEN_INTERFACE, root);
			player.send(new InterfaceRootMessage(root.getId(), refresh));
			this.root = root;
		}
	}

	public void closeInterfaces() {
		hasTransient = false;
		synchronized (pointers) {
			Collection<Interface> inters = new HashSet<>(interfaces.values());
			for (Interface inter : inters) {
				if (inter.getPersistence() != InterfacePersistence.PERSISTENT) {
					if (pointers.containsKey(inter.getId())) {
						int pointer = pointers.remove(inter.getId());
						Interface removed = interfaces.remove(pointer);
						player.send(new InterfaceCloseMessage(pointer));
						removed.dispatch(player, InterfaceAction.CLOSE_ACTION);
						if (inter.getId() == root.getId()) {
							setRoot(mode.getRoot(), true);
						}
					}
				}
			}
		}
	}

	public boolean close(Interface inter) {
		return close(inter, true);
	}

	public boolean close(Interface inter, boolean send) {
		synchronized (pointers) {
			if (pointers.containsKey(inter.getId())) {
				int pointer = pointers.remove(inter.getId());
				Interface removed = interfaces.remove(pointer);
				if (send)
					player.send(new InterfaceCloseMessage(pointer));
				removed.dispatch(player, InterfaceAction.CLOSE_ACTION);
				if (inter.getId() == root.getId()) {
					setRoot(mode.getRoot(), true);
				}
				return true;
			}
		}
		return false;
	}

	private void switchPointer(Interface inter, DisplayMode targetMode) {
		int source = pointers.remove(inter.getId());
		int target = (targetMode.getRoot().getId() << 16) | inter.getDisplaySlot(targetMode);
		pointers.put(inter.getId(), target);
		Interface other = interfaces.remove(source);
		if (other != inter) {
			throw new NullPointerException("Switched interface was not the interface found!");
		}
		interfaces.put(target, inter);
		player.send(new InterfaceMoveMessage(source, target));
	}

	public Interface getInterface(int interfaceId) {
		synchronized (pointers) {
			Integer pointer = pointers.get(interfaceId);
			if (pointer == null)
				return null;
			return interfaces.get(pointer);
		}
	}
}