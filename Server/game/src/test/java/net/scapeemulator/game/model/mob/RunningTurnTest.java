package net.scapeemulator.game.model.mob;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.update.player.descr.RunPlayerDescriptor;
import org.junit.Test;

import static org.junit.Assert.*;

public class RunningTurnTest {
	@Test
	public void tightTurnWalksFirstTileInsteadOfBorrowingThirdTile() {
		Player player = new Player();
		player.setPosition(new Position(3200, 3200));
		player.getMovement().tick();
		player.setRunning(true);
		player.getMovement().addFirstStep(new Position(3201, 3200));
		player.getMovement().addStep(new Position(3201, 3201));
		player.getMovement().addStep(new Position(3202, 3201));
		player.getMovement().tick();
		assertEquals(new Position(3201, 3200), player.getPosition());
		assertNull(player.getRunStep());
		assertEquals(-1, RunPlayerDescriptor.convert(player));
		assertEquals(100, player.getEnergy());
		player.getMovement().tick();
		assertEquals(new Position(3201, 3201), player.getPosition());
		player.getMovement().tick();
		assertEquals(new Position(3202, 3201), player.getPosition());
	}

	@Test
	public void firstMovementTickEncodesRelativeToActualStartingTile() {
		Player player = new Player();
		player.setPosition(new Position(3200, 3200));
		player.setRunning(true);
		player.getMovement().addFirstStep(new Position(3204, 3200));
		player.getMovement().tick();
		assertEquals(new Position(3200, 3200), player.getStartStep());
		assertEquals(8, RunPlayerDescriptor.convert(player));
	}
}
