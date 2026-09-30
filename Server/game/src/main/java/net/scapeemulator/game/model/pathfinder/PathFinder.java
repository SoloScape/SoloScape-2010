package net.scapeemulator.game.model.pathfinder;

import net.scapeemulator.game.model.FieldOfView;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.map.WorldMap;
import net.scapeemulator.game.model.player.Player;

public class PathFinder {
	
	/*
	 * TODO: Max steps, so forth
	 */

	// An actionwalk means we clicked a player, object, npc, ... so we can end
	// next to the target location instead of on it TODO implement
	// Offsets, the size of this npc, object, ... TODO implement
	public static void findPath(Player mob, int tX, int tY, int offsetX, int offsetY, boolean nearby, boolean run) {
		if (mob.getPosition().getX() == tX && mob.getPosition().getY() == tY) {
			return;
		}
		// Gets the map we are going to use
		CollisionMap map = null;
		if (map == null)
			map = WorldMap.getArea(mob.getPosition(), FieldOfView.REGULAR);
		// Calculates our positions in this map
		int cx = mob.getPosition().getChunkX(), cy = mob.getPosition().getChunkY();
		int startX = mob.getPosition().getX() - ((cx - 6) << 3);
		int startY = mob.getPosition().getY() - ((cy - 6) << 3);
		int targetX = startX + (tX - mob.getPosition().getX());
		int targetY = startY + (tY - mob.getPosition().getY());
		cx -= 6;
		cy -= 6;

		int[][] distances = new int[104][104]; // Only used to determine the
												// size of our path
		int[][] parentDir = new int[104][104]; // Direction to parent, used for
												// backtracking & marking tiles
												// as visited
		parentDir[startX][startY] = -1; // Mark it as visited, the number
										// doesn't matter much
		// The tile we are currently doing a calculation for
		int currentX = startX;
		int currentY = startY;
		int[] queueX = new int[4096]; // Although there are 10816 tiles in a
										// 104x104 mapregion it will
		int[] queueY = new int[4096]; // just overwrite the array again when the
										// last index is reached
		int queueRead = 0;
		queueX[0] = startX;
		queueY[0] = startY;
		int queueWrite = 1;
		boolean routeExists = false;

		// The queueRead will always increase by 1 every iteration
		// The queueWrite will increase by 0 to 8 every iteration, the amount of
		// unvisited, reachable neighboring tiles
		// If the queueInsert doesn't increase anymore it means there is no
		// route, queueRead will still increase and the loop will break because
		// of condition
		while (queueRead != queueWrite) {
			currentX = queueX[queueRead];
			currentY = queueY[queueRead];
			queueRead = (queueRead + 1) & 0xfff; // When the queueRead gets over
													// 4095 it is reset and we
													// will recycle the start
													// indexes, the queueWrite
													// will also do this

			if (currentX == targetX && currentY == targetY) {
				// Path is found, break the loop
				routeExists = true;
				break;
			}
			// Todo check offsets & actionWalk stuff...

			// The distance to the next square will be the distance to this
			// square +1
			int distance = distances[currentX][currentY] + 1;

			// Checks if you can move to adjacent tiles, and adds them to the
			// queue if needed
			// west, checks if we can move to the west
			if (currentX > 0 && parentDir[currentX - 1][currentY] == 0
					&& (map.getFlags()[currentX - 1][currentY] & 0x42240000) == 0) {
				queueX[queueWrite] = currentX - 1;
				queueY[queueWrite] = currentY;
				distances[currentX - 1][currentY] = distance;
				parentDir[currentX - 1][currentY] = 0x2;
				queueWrite = (queueWrite + 1) & 0xfff;
			}
			// east
			if (currentX < 103 && parentDir[currentX + 1][currentY] == 0
					&& (map.getFlags()[currentX + 1][currentY] & 0x60240000) == 0) {
				queueX[queueWrite] = currentX + 1;
				queueY[queueWrite] = currentY;
				distances[currentX + 1][currentY] = distance;
				parentDir[currentX + 1][currentY] = 0x1;
				queueWrite = (queueWrite + 1) & 0xfff;
			}
			// south
			if (currentY > 0 && parentDir[currentX][currentY - 1] == 0
					&& (map.getFlags()[currentX][currentY - 1] & 0x40a40000) == 0) {
				queueX[queueWrite] = currentX;
				queueY[queueWrite] = currentY - 1;
				distances[currentX][currentY - 1] = distance;
				parentDir[currentX][currentY - 1] = 0x4;
				queueWrite = (queueWrite + 1) & 0xfff;
			}
			// north
			if (currentY < 103 && parentDir[currentX][currentY + 1] == 0
					&& (map.getFlags()[currentX][currentY + 1] & 0x48240000) == 0) {
				queueX[queueWrite] = currentX;
				queueY[queueWrite] = currentY + 1;
				distances[currentX][currentY + 1] = distance;
				parentDir[currentX][currentY + 1] = 0x8;
				queueWrite = (queueWrite + 1) & 0xfff;
			}

			// southwest, in these directions we must check if its possible to
			// go south AND west AND southwest
			if (currentX > 0 && currentY > 0 && parentDir[currentX - 1][currentY - 1] == 0
					&& (map.getFlags()[currentX][currentY - 1] & 0x40a40000) == 0
					&& (map.getFlags()[currentX - 1][currentY] & 0x42240000) == 0
					&& (map.getFlags()[currentX - 1][currentY - 1] & 0x43a40000) == 0) {
				queueX[queueWrite] = currentX - 1;
				queueY[queueWrite] = currentY - 1;
				distances[currentX - 1][currentY - 1] = distance;
				parentDir[currentX - 1][currentY - 1] = 0x6;
				queueWrite = (queueWrite + 1) & 0xfff;
			}

			// southeast
			if (currentX < 103 && currentY > 0 && parentDir[currentX + 1][currentY - 1] == 0
					&& (map.getFlags()[currentX][currentY - 1] & 0x40a40000) == 0
					&& (map.getFlags()[currentX + 1][currentY] & 0x60240000) == 0
					&& (map.getFlags()[currentX + 1][currentY - 1] & 0x60e40000) == 0) {
				queueX[queueWrite] = currentX + 1;
				queueY[queueWrite] = currentY - 1;
				distances[currentX + 1][currentY - 1] = distance;
				parentDir[currentX + 1][currentY - 1] = 0x5;
				queueWrite = (queueWrite + 1) & 0xfff;
			}

			// northwest
			if (currentX > 0 && currentY < 103 && parentDir[currentX - 1][currentY + 1] == 0
					&& (map.getFlags()[currentX][currentY + 1] & 0x48240000) == 0
					&& (map.getFlags()[currentX - 1][currentY] & 0x42240000) == 0
					&& (map.getFlags()[currentX - 1][currentY + 1] & 0x4e240000) == 0) {
				queueX[queueWrite] = currentX - 1;
				queueY[queueWrite] = currentY + 1;
				distances[currentX - 1][currentY + 1] = distance;
				parentDir[currentX - 1][currentY + 1] = 0xA;
				queueWrite = (queueWrite + 1) & 0xfff;
			}
			// northeast
			if (currentX < 103 && currentY < 103 && parentDir[currentX + 1][currentY + 1] == 0
					&& (map.getFlags()[currentX][currentY + 1] & 0x48240000) == 0
					&& (map.getFlags()[currentX + 1][currentY] & 0x60240000) == 0
					&& (map.getFlags()[currentX + 1][currentY + 1] & 0x78240000) == 0) {
				queueX[queueWrite] = currentX + 1;
				queueY[queueWrite] = currentY + 1;
				distances[currentX + 1][currentY + 1] = distance;
				parentDir[currentX + 1][currentY + 1] = 0x9;
				queueWrite = (queueWrite + 1) & 0xfff;
			}

		}
		if (!routeExists) {
			if (!nearby) {
				return;
			}
			// This happens when you click in unreachable places (such as just
			// black void, rivers, closed buildings...)
			// TODO: Should calculate an alternative path here
		}

		// Backtracking our path

		int size = distances[currentX][currentY]; // the number of steps in the
													// path equals the distance
													// to the target location
		queueWrite = 0;
		queueX[size] = currentX;
		queueY[size] = currentY;

		// Backtrack the path
		while (currentX != startX || currentY != startY) {
			int dir = parentDir[currentX][currentY];
			if ((dir & 0x2) != 0) {
				currentX++;
			} else if ((dir & 0x1) != 0) {
				currentX--;
			}
			if ((dir & 0x4) != 0) {
				currentY++;
			} else if ((dir & 0x8) != 0) {
				currentY--;
			}
			// Invert the path because we are backtracking it
			queueX[size - 1 - queueWrite] = currentX;
			queueY[size - 1 - queueWrite++] = currentY;
		}
		// Actually add it to the walking queue
		mob.getMovement().addFirstStep(
				new Position(((cx << 3) + queueX[1]), ((cy << 3) + queueY[1]), mob.getPosition().getHeight()));
		mob.getMovement().setRunningQueue(run);
		for (int i = 2; i <= size; i++) {
			mob.getMovement().addStep(
					new Position(((cx << 3) + queueX[i]), ((cy << 3) + queueY[i]), mob.getPosition().getHeight()));
		}
	}
}