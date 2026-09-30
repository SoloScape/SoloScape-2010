package net.scapeemulator.game.model.inventory;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.game.cache.ItemDefinition;
import net.scapeemulator.game.conf.WorldConfiguration;
import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.inventory.Inventory.StackMode;

public class InventoryTest {
	private World world;

	@Before
	public void setUp() throws Exception {
		world = World.getWorld(WorldConfiguration.parse("data/world.conf"));
		Cache cache = new Cache(FileStore.open("../resources/cache"));
		ItemDefinition.init(cache, true);
	}

	@Test
	public void test_public_inventory() {
		InventoryInfo info = new InventoryInfo(93, "test_shop", 620, 24, 40, StackMode.ALWAYS);
		Inventory main = new Inventory(null, info);
		Inventory copy = main.shaleCopy(null);
		main.add(new Item(4151, 5));
		if (copy.get(0) != null) {
			Item test = copy.get(0);
			if (test.getId() != 4151 || test.getAmount() != 5) {
				fail("Inventory is not a duplicate");
			} else {
				System.out.println("Succesfully tested duplicate inventory modification.");
			}
		}

	}
}
