package net.scapeemulator.xtalk.server.job;

import net.scapeemulator.api.Job;
import net.scapeemulator.worldlist.World;
import net.scapeemulator.xtalk.server.GameServerService;
import net.scapeemulator.xtalk.server.XTalkWorld;
import net.scapeemulator.xtalk.server.message.world.XCountryTypeRefreshMessage;

public class CountryRefreshJob extends Job<GameServerService> {

	@Override
	public void perform(GameServerService service) {
		World[] worlds = service.getWorlds();
		for (World world : worlds) {
			if (world instanceof XTalkWorld) {
				((XTalkWorld) world).getSession().send(new XCountryTypeRefreshMessage(service.getCountryList()));
			}
		}
	}
}
