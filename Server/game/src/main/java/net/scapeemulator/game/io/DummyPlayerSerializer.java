package net.scapeemulator.game.io;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.net.login.LoginResponse;

public final class DummyPlayerSerializer extends PlayerSerializer {

	@Override
	public SerializeResult load(String username, String password) {
		Player player = new Player();
		player.setUsername(username);
		player.setPassword(password);
		player.setRights(2);
		player.getStatus().setVar(406, 22);// Tutorial
		player.getStatus().defaults();
		// TODO Defaults NOT in load, but initial account creation!
		player.setPosition(new Position(3200, 3400));
		Inventory samples = player.getInventories().get(Inventory.GENERAL_STORE_FREEBIES);
		if (samples != null) {
			samples.add(new Item(4151, 50));
			samples.add(new Item(11694, 50));
		}
		return new SerializeResult(LoginResponse.STATUS_OK, player);
	}

	@Override
	public void save(Player player) {
		/* discard player */
	}
}
