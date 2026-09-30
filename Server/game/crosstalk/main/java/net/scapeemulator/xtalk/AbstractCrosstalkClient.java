package net.scapeemulator.xtalk;

import net.scapeemulator.api.Service;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.js5.UpdateService;

public abstract class AbstractCrosstalkClient implements Runnable, Service {
	private final UpdateService updateService;
	private final int version, worldId;

	public AbstractCrosstalkClient(int worldId, int version, UpdateService updateService) {
		this.worldId = worldId;
		this.version = version;
		this.updateService = updateService;
	}

	public UpdateService getJs5() {
		return updateService;
	}

	public int getVersion() {
		return version;
	}

	public int getWorldId() {
		return worldId;
	}
	
	public abstract CodecRepository getRepo();
}
