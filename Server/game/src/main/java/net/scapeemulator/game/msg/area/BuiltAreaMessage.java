package net.scapeemulator.game.msg.area;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.game.model.FieldOfView;
import net.scapeemulator.game.model.entity.Position;

public class BuiltAreaMessage implements Message {
	private final Position position;
	private final FieldOfView fov;
	private final boolean forcedRefresh;
	private final Object[][][] pallette;

	public BuiltAreaMessage(Object[][][] pallette, Position position, FieldOfView fov,
			boolean forcedRefresh) {
		this.pallette = pallette;
		this.position = position;
		this.fov = fov;
		this.forcedRefresh = forcedRefresh;
	}

	public Position getPosition() {
		return position;
	}

	public FieldOfView getFov() {
		return fov;
	}

	public boolean isForcedRefresh() {
		return forcedRefresh;
	}

	public Object[][][] getPallette() {
		return pallette;
	}
}
