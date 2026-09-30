package net.scapeemulator.game.model.tele;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.task.Action;

public abstract class Teleport extends Action<Player> {
	protected final Position target;

	public static Teleport getTeleport(Player player, String area) {
		switch (area) {
		case "varrock":
			return new ModernTeleport(player, new Position(3211, 3423, 0));// Tol2
		case "lumbridge":
			return new ModernTeleport(player, new Position(3221, 3218, 0));// Tol1
		case "falador":
			return new ModernTeleport(player, new Position(2965, 3378, 0));// Tol2
		}
		return null;
	}

	public Teleport(Player player, Position position, int delay) {
		super(player, delay, true);
		this.target = position;
	}

	@Override
	public boolean blocking() {
		return true;
	}

	@Override
	public abstract void execute();
}
