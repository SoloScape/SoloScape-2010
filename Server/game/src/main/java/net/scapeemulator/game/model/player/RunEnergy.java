package net.scapeemulator.game.model.player;

/**
 * Reconstructed post-June-2009, pre-2013 unladen run-energy rates.
 * Values are percentage points per 600 ms game tick. See docs/run-energy.md
 * for sources, assumptions and the missing carried-weight calculation.
 */
public final class RunEnergy {
	private RunEnergy() {
	}

	public static double drainPerTick(int agility) {
		// Interpolate unladen running duration: approximately 62 s at 1, 95 s at 99.
		double durationSeconds = 62.0 + (level(agility) - 1) * (33.0 / 98.0);
		return 60.0 / durationSeconds;
	}

	public static double recoveryPerTick(int agility) {
		// Period player testing: 2260 - (Agility * 10) ms to recover one percent.
		return 600.0 / (2260.0 - level(agility) * 10.0);
	}

	public static double restingRecoveryPerTick(int agility) {
		// Reconstruct resting duration from the reported 35 s / 15 s endpoints.
		double durationSeconds = 35.0 - (level(agility) - 1) * (20.0 / 98.0);
		return 60.0 / durationSeconds;
	}

	private static int level(int agility) {
		return Math.max(1, Math.min(99, agility));
	}
}
