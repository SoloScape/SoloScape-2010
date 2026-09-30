package net.scapeemulator.worldlist;

public final class Country {
	public static final int FLAG_UK          = 77;
	public static final int FLAG_USA         = 225;
	public static final int FLAG_CANADA      = 38;
	public static final int FLAG_NETHERLANDS = 161;
	public static final int FLAG_AUSTRALIA   = 16;
	public static final int FLAG_SWEDEN      = 191;
	public static final int FLAG_FINLAND     = 69;
	public static final int FLAG_IRELAND     = 101;
	public static final int FLAG_BELGIUM     = 22;
	public static final int FLAG_NORWAY      = 162;
	public static final int FLAG_DENMARK     = 58;
	public static final int FLAG_BRAZIL      = 31;
	public static final int FLAG_MEXICO      = 152;

	private final int flag;
	private final String name;

	public Country(int flag, String name) {
		this.flag = flag;
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + flag;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Country other = (Country) obj;
		if (flag != other.flag)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Country [flag=" + flag + ", name=" + name + "]";
	}

	public int getFlag() {
		return flag;
	}

	public String getName() {
		return name;
	}

}
