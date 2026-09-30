package net.scapeemulator.game.msg.entity;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.update.Descriptor;
import net.scapeemulator.game.update.player.PlayerDescriptor;

import java.util.List;

public final class PlayerUpdateMessage implements Message {
	private final Position lastKnownRegion;
	private final Position position;
	private int localPlayerCount;
	private PlayerDescriptor selfDescriptor;
	private final List<Descriptor<PlayerUpdateMessage>> descriptors;

	public PlayerUpdateMessage(Position lastKnownRegion, Position position, int localPlayerCount,
			PlayerDescriptor selfDescriptor, List<Descriptor<PlayerUpdateMessage>> descriptors) {
		this.lastKnownRegion = lastKnownRegion;
		this.position = position;
		this.localPlayerCount = localPlayerCount;
		this.selfDescriptor = selfDescriptor;
		this.descriptors = descriptors;
	}

	public PlayerUpdateMessage(Position lastKnownRegion, Position position, List<Descriptor<PlayerUpdateMessage>> descriptors) {
		this.lastKnownRegion = lastKnownRegion;
		this.position = position;
		this.descriptors = descriptors;
	}

	public Position getLastKnownRegion() {
		return lastKnownRegion;
	}

	public Position getPosition() {
		return position;
	}

	public int getLocalPlayerCount() {
		return localPlayerCount;
	}

	public PlayerDescriptor getSelfDescriptor() {
		return selfDescriptor;
	}

	public List<Descriptor<PlayerUpdateMessage>> getDescriptors() {
		return descriptors;
	}

}
