package net.scapeemulator.js5;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import net.scapeemulator.api.Service;
import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.ChecksumTable;

public final class UpdateService implements Service, Runnable {

	private final Queue<UpdateSession> pendingSessions = new ArrayDeque<>();
	private final Cache cache;
	private final ChecksumTable checksumTable;
	private final int version;

	public UpdateService(int version, Cache cache) {
		this.version = version;
		this.cache = cache;
		try {
			checksumTable = cache.createChecksumTable();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create checksum table!");
		}
	}

	public void addPendingSession(UpdateSession session) {
		synchronized (pendingSessions) {
			pendingSessions.add(session);
			pendingSessions.notifyAll();
		}
	}

	@Override
	public void run() {
		for (;;) {
			UpdateSession session;

			synchronized (pendingSessions) {
				while ((session = pendingSessions.poll()) == null) {
					try {
						pendingSessions.wait();
					} catch (InterruptedException e) {
						/* ignore */
					}
				}
			}

			session.processFileQueue();
		}
	}

	public Cache getCache() {
		return cache;
	}

	public ChecksumTable getChecksumTable() {
		return checksumTable;
	}

	public int getVersion() {
		return version;
	}
}
