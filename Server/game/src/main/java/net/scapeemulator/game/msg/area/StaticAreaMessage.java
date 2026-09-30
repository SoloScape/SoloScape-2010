package net.scapeemulator.game.msg.area;

import java.util.Collections;
import java.util.Map;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.game.model.FieldOfView;
import net.scapeemulator.game.model.entity.Position;

public final class StaticAreaMessage implements Message {
	private final Position position;
	private final FieldOfView fov;
	private final boolean forcedRefresh;
	private final int playerId;
	private final Map<Integer, Position> positionBlock;

	public StaticAreaMessage(Position position, FieldOfView fov, boolean forcedRefresh) {
		this(position, fov, -1, Collections.emptyMap(), forcedRefresh);
	}

	public StaticAreaMessage(Position position, FieldOfView fov, int playerId, Map<Integer, Position> positionBlock,
			boolean forcedRefresh) {
		this.positionBlock = positionBlock;
		this.position = position;
		this.playerId = playerId;
		this.fov = fov;
		this.forcedRefresh = forcedRefresh;
	}

	public Position getPosition() {
		return position;
	}

	public int getPlayerId() {
		return playerId;
	}

	public Map<Integer, Position> getPositionBlock() {
		return positionBlock;
	}

	public FieldOfView getFov() {
		return fov;
	}

	public boolean forcedRefresh() {
		return forcedRefresh;
	}
}
