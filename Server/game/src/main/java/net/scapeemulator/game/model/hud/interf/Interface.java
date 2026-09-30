package net.scapeemulator.game.model.hud.interf;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceAction;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.child.InterfaceChild;
import net.scapeemulator.game.model.hud.interf.child.InventoryChild;
import net.scapeemulator.game.model.hud.interf.child.SpriteChild;
import net.scapeemulator.game.model.hud.interf.child.TextChild;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.child.InterfaceSpriteMessage;
import net.scapeemulator.game.msg.interf.child.InterfaceTextMessage;

public abstract class Interface {
	private final InterfacePersistence persistence;
	private final Map<Integer, ChildInterface> childs = new HashMap<>();
	protected final int id;

	public Interface(int id, InterfacePersistence persistence) {
		this.id = id;
		this.persistence = persistence;
	}

	public void addChild(ChildInterface child) {
		childs.put(child.getChildId(), child);
	}

	//Should this be a default?
	public abstract int getDisplaySlot(DisplayMode mode);

	public InterfacePersistence getPersistence() {
		return persistence;
	}

	public void dispatch(Player player, InterfaceAction action) {
		player.sendMessage(action.toString(), 99);
		switch (action.getType()) {
		case CHILD_ACTION:
			ChildInterfaceAction childAction = (ChildInterfaceAction) action;
			ChildInterface inter = childs.get(childAction.getChild());
			if (inter != null) {
				inter.handle(player, childAction);
			}
			break;
		case CLOSE_INTER:
			Collection<ChildInterface> inters = new HashSet<>(childs.values());
			for (ChildInterface subinter : inters) {
				switch (subinter.getType()) {
				case INTERFACE:
					InterfaceChild interChild = (InterfaceChild) subinter;
					player.getDisplay().close(interChild.getInter());
					break;
				default:
					break;
				}
			}
			handleAction(player, action);
			break;
		case OPEN_INTER:
			handleAction(player, action);
			onOpen(player);
			break;
		default:
			handleAction(player, action);
			break;
		}
	}

	private final void onOpen(Player player) {
		Collection<ChildInterface> inters = new HashSet<>(childs.values());
		for (ChildInterface child : inters) {
			refresh(player, child);
		}
	}

	public void refresh(Player player, ChildInterface child) {
		switch (child.getType()) {// TODO: InterfaceMediaMessage
		case INTERFACE:
			InterfaceChild interChild = (InterfaceChild) child;
			player.getDisplay().open(interChild.getInter(), (getId() << 16) | child.getChildId(), true);
			break;
		case INVENTORY:
			InventoryChild inventory = (InventoryChild) child;
			player.getInventories().get(inventory.getAssignedId()).refresh();
			break;
		case ITEM:
			break;
		case MODEL:
			break;
		case NPC:
			break;
		case PLAYER:
			break;
		case SPRITE:
			SpriteChild sprite = (SpriteChild) child;
			player.sendMessage(sprite.toString(), 99);
			player.send(new InterfaceSpriteMessage(sprite.getSpriteId(), getId(), sprite.getChildId()));
			break;
		case TEXT:
			TextChild text = (TextChild) child;
			player.send(new InterfaceTextMessage(getId(), text.getChildId(), text.getText(player)));
			break;
		default:
			break;
		}
		for (Object obj : child.settings()) {
			player.send((Message) obj);
		}
	}

	public ChildInterface getChild(int i) {
		return childs.get(i);
	}

	protected void handleAction(Player player, InterfaceAction action) {

	}

	public int getId() {
		return id;
	}
}
