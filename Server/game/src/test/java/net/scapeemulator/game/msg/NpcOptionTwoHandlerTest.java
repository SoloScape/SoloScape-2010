package net.scapeemulator.game.msg;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.game.cache.SceneryDefinition;
import net.scapeemulator.game.conf.WorldConfiguration;
import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.map.WorldMap;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.NpcOptionTwoMessage;
import net.scapeemulator.game.task.Action;
import net.scapeemulator.game.util.LandscapeKeyTable;
import org.junit.*;

import java.io.File;

import static org.junit.Assert.*;

public class NpcOptionTwoHandlerTest {
	private World world;
	private Player player;
	private Npc npc;
	private int[][] flags;
	private final int[][] saved = new int[15][15];

	@BeforeClass
	public static void loadCollision() throws Exception {
		File server = new File("Server").isDirectory() ? new File("Server") : new File("..");
		try (Cache cache = new Cache(FileStore.open(new File(server, "data/cache").getPath()))) {
			SceneryDefinition.init(cache);
			WorldMap.init(cache, LandscapeKeyTable.open(new File(server, "game/data/landscape-keys")));
		}
	}

	@Before
	public void setUp() {
		world = World.getWorld(new WorldConfiguration());
		player = new Player();
		player.setPosition(new Position(3220, 3440));
		npc = new Npc(8349);
		npc.setAttackable(true);
		npc.setPosition(new Position(3225, 3440));
		assertTrue(world.getNpcs().add(npc));
		player.getLocalNpcs().add(npc);
		flags = WorldMap.getRegion(50, 53).getCollisionMaps()[0].getFlags();
		for (int x = 0; x < 15; x++) for (int y = 0; y < 15; y++) {
			saved[x][y] = flags[16 + x][44 + y];
			flags[16 + x][44 + y] = x == 0 || y == 0 || x == 14 || y == 14 ? 0x40000000 : 0;
		}
	}

	@After
	public void tearDown() {
		player.stopAction();
		world.getTaskScheduler().tick();
		world.getNpcs().remove(npc);
		for (int x = 0; x < 15; x++) for (int y = 0; y < 15; y++)
			flags[16 + x][44 + y] = saved[x][y];
	}

	@Test
	public void npcOptionApproachesBeforeAttackingAndHonorsRunFlag() throws Exception {
		click(true);
		assertNull(player.getCombat().getTarget());
		world.getTaskScheduler().tick();
		assertTrue(player.getMovement().isRunningQueue());
		player.getMovement().tick();
		for (int i = 0; i < 10 && player.getCombat().getTarget() == null; i++) tick();
		assertSame(npc, player.getCombat().getTarget());
		assertSame(npc, player.getInteractingTarget());
		assertEquals(new Position(3224, 3440), player.getPosition());
	}

	@Test
	public void routesAroundObstacleAndDoesNotAttackThroughWall() throws Exception {
		for (int y = 45; y < 56; y++) flags[23][y] = 0x40000000;
		click(false);
		for (int i = 0; i < 40 && player.getCombat().getTarget() == null; i++) tick();
		assertSame(npc, player.getCombat().getTarget());
		assertFalse(player.getMovement().isRunningQueue());
		assertNotEquals(npc.getPosition(), player.getPosition());
	}

	@Test
	public void unreachableNpcNeverBecomesCombatTarget() throws Exception {
		for (int y = 45; y < 58; y++) flags[23][y] = 0x40000000;
		click(false);
		for (int i = 0; i < 6; i++) tick();
		assertNull(player.getCombat().getTarget());
		assertEquals(new Position(3220, 3440), player.getPosition());
	}

	@Test
	public void rejectsUnknownDeadHiddenDistantOtherPlaneAndNonAttackableNpcs() throws Exception {
		for (int id : new int[] { 0, 65535 }) {
			NpcAndWorldMapPacketTest.dispatcher().dispatch(player, new NpcOptionTwoMessage(id, false));
			tick();
			assertNull(player.getCombat().getTarget());
		}
		npc.setPosition(new Position(3221, 3440));
		npc.setDead(true); click(false); tick(); assertNull(player.getCombat().getTarget());
		npc.setDead(false);
		npc.setAttackable(false); click(false); tick(); assertNull(player.getCombat().getTarget());
		npc.setAttackable(true);
		player.getLocalNpcs().clear(); click(false); tick(); assertNull(player.getCombat().getTarget());
		player.getLocalNpcs().add(npc);
		npc.setPosition(new Position(3221, 3440, 1)); click(false); tick(); assertNull(player.getCombat().getTarget());
		npc.setPosition(new Position(3300, 3440)); click(false); tick(); assertNull(player.getCombat().getTarget());
	}

	@Test
	public void cancelledApproachCannotRestartCombat() throws Exception {
		click(false);
		player.stopAction();
		npc.setPosition(new Position(3221, 3440));
		tick();
		assertNull(player.getCombat().getTarget());
	}

	@Test
	public void deadPlayerAndBlockingActionCannotStartAttack() throws Exception {
		npc.setPosition(new Position(3221, 3440));
		player.setDead(true); click(false); tick(); assertNull(player.getCombat().getTarget());
		player.setDead(false);
		player.startAction(new Action<Player>(player, 1, true) {
			@Override public void execute() { }
			@Override public boolean blocking() { return true; }
		});
		click(false); tick(); assertNull(player.getCombat().getTarget());
		assertTrue(player.blockingAction());
	}

	@Test
	public void targetDespawnDuringApproachCancelsAttack() throws Exception {
		click(false); tick();
		npc.unlist(); tick();
		assertNull(player.getCombat().getTarget());
	}

	private void click(boolean running) throws Exception {
		NpcAndWorldMapPacketTest.dispatcher().dispatch(player, new NpcOptionTwoMessage(npc.getId(), running));
	}

	private void tick() {
		world.getTaskScheduler().tick();
		player.getMovement().tick();
	}
}
