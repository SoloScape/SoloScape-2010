package net.scapeemulator.game.model.mob.combat;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.game.cache.ItemDefinition;
import net.scapeemulator.game.conf.WorldConfiguration;
import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.mob.Combat;

public class CombatMathsTest {
	private World world;

	@Before
	public void setUp() throws Exception {
		world = World.getWorld(WorldConfiguration.parse("data/world.conf"));
		Cache cache = new Cache(FileStore.open("../resources/cache"));
		ItemDefinition.init(cache, true);
	}

	@Test
	public void test_speeds() {
		Item dark_bow = new Item(11235);
		int attackSpeed = Combat.getAttackSpeed(dark_bow);
		if (world.members()) {
			if (attackSpeed != 5) {
				fail("Invalid attack speed!");
			}
		} else {
			if (attackSpeed != 9) {
				fail("Invalid attack speed!");
			}
		}
	}
}
