package net.scapeemulator.game.model.player;

public final class Appearance {

	public static final Appearance DEFAULT_APPEARANCE = new Appearance(Gender.MALE,
		new int[] { 8, 14, 18, 26, 34, 38, 42 },
		new int[] { 2, 5, 8, 11, 14 });

	private final Gender gender;
	private final int[] style;
	private final int[] colors;

	public Appearance(Gender gender, int[] style, int[] colors) {
		this.gender = gender;
		this.style = style;
		this.colors = colors;
	}

	public Gender getGender() {
		return gender;
	}

	public int getStyle(int index) {
		return style[index];
	}

	public int getColor(int index) {
		return colors[index];
	}
}
