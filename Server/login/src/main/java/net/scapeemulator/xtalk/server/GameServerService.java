package net.scapeemulator.xtalk.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import io.netty.buffer.ByteBuf;
import net.scapeemulator.api.Job;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.handler.MessageDispatcher;
import net.scapeemulator.worldlist.*;
import net.scapeemulator.xtalk.net.autoworld.AutoWorldRequest;
import net.scapeemulator.xtalk.net.autoworld.AutoWorldResponse;
import net.scapeemulator.xtalk.net.autoworld.AutoWorldSession;
import net.scapeemulator.xtalk.net.gameserver.GameServerSession;
import net.scapeemulator.xtalk.net.login.*;
import net.scapeemulator.xtalk.server.job.CountryRefreshJob;
import net.scapeemulator.xtalk.server.job.PingJob;
import net.scapeemulator.xtalk.server.job.PlayerCountRefreshJob;
import net.scapeemulator.xtalk.server.job.WorldListRefreshJob;
import net.scapeemulator.xtalk.server.message.Messages;

public class GameServerService extends WorldListService implements Runnable {
	private final BlockingQueue<Job<GameServerService>> jobs = new LinkedBlockingDeque<>();
	private final List<World> worlds = new ArrayList<>();
	private final CodecRepository repo = new CodecRepository();
	private final MessageDispatcher<GameServerSession> dispatcher = new MessageDispatcher<>();
	private final int version;
	private int countryCodeIndex = 0;

	public GameServerService(int version) {
		Messages.set(repo, dispatcher);
		this.version = version;
		countries.put(countryCodeIndex++, new Country(Country.FLAG_UK, "UK"));
		countries.put(countryCodeIndex++, new Country(Country.FLAG_FINLAND, "Finland"));
		worlds.add(new OfflineWorld(55, World.FLAG_MEMBERS, 1, ""));
		worlds.add(new OfflineWorld(144, World.FLAG_MEMBERS, 1, ""));
	}

	public void addLoginRequest(WorldLoginSession session, WorldLoginRequest request) {
		jobs.add(new LoginJob(session, request));
	}

	public int getVersion() {
		return version;
	}

	private int matchCountry(Country country) {
		Iterator<Entry<Integer, Country>> iterator = countries.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, Country> next = iterator.next();
			if (next.getValue().equals(country)) {
				return next.getKey();
			}
		}
		countries.put(countryCodeIndex, country);
		jobs.add(new CountryRefreshJob());
		return countryCodeIndex++;
	}

	private class LoginJob extends Job<GameServerService> {
		private final WorldLoginSession session;
		private final WorldLoginRequest request;

		public LoginJob(WorldLoginSession session, WorldLoginRequest request) {
			this.session = session;
			this.request = request;
		}

		@Override
		public void perform(GameServerService service) {
			int worldId = request.getWorldId();
			if (worldId < 1) {
				session.sendFailure(WorldLoginMessage.INVALID_WORLD);
			} else if (worlds.get(worldId) instanceof OnlineWorld) {
				session.sendFailure(WorldLoginMessage.WORLD_ALREADY_ONLINE);
			} else {
				GameServerSession session = new GameServerSession(service, this.session.getChannel());
				int id = matchCountry(request.getCountry());
				XTalkWorld world = new XTalkWorld(session, request.getWorldId(), request.getFlags(), id,
						request.getActivity(), this.session.getAdress());
				session.setWorld(world);
				this.session.sendSuccess(WorldLoginMessage.SUCCESS, session);
				merge(world);
				jobs.add(new WorldListRefreshJob());
			}
		}

		private void merge(XTalkWorld xWorld) {
			for (World world : worlds.toArray(new World[0])) {
				if (world.getId() == xWorld.getId()) {
					worlds.remove(world);
					break;
				}
			}
			worlds.add(xWorld);
			Collections.sort(worlds);
		}
	}

	private class LogoutJob extends Job<GameServerService> {
		private XTalkWorld world;

		public LogoutJob(XTalkWorld world) {
			this.world = world;
		}

		@Override
		public void perform(GameServerService service) {
			worlds.remove(world);
			worlds.add(world.offline());
			Collections.sort(worlds);
			jobs.add(new WorldListRefreshJob());
		}
	}

	@Override
	public void run() {
		for (;;) {
			try {
				jobs.take().perform(this);
			} catch (Exception e) {
				e.printStackTrace();
				/* ignore */
			}
		}
	}

	public CodecRepository getCodecRepository() {
		return getRepo();
	}

	public MessageDispatcher<GameServerSession> getDispatcher() {
		return dispatcher;
	}

	public CodecRepository getRepo() {
		return repo;
	}

	/**
	 * TODO: members, etc.. Check world, pvp is a last-resort etc.
	 */
	public void matchWorld(AutoWorldSession session, AutoWorldRequest message) {
		// TargetID is actual world id!!!
		int target = 1;
		ByteBuf payload = session.channel().alloc().buffer(2);
		payload.writeShort(target);
		AutoWorldResponse response = new AutoWorldResponse(AutoWorldResponse.STATUS_SWITCH_WORLD_AND_RETRY, payload);
		session.sendResponse(response);
	}

	public void ping() {
		jobs.add(new PingJob());
	}

	public void logout(XTalkWorld world) {
		jobs.add(new LogoutJob(world));
	}

	public void refreshPlayerCounts() {
		jobs.add(new PlayerCountRefreshJob());
	}

	@Override
	public World[] getWorlds() {
		return worlds.toArray(new World[0]);
	}

	public Map<Integer, Country> getCountryList() {
		return countries;
	}

	public List<World> worlds() {
		return worlds;
	}
}
