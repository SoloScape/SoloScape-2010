package net.scapeemulator.game.conf;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import net.scapeemulator.worldlist.Country;
import net.scapeemulator.worldlist.World;

public class WorldConfiguration {
	private final static Logger logger = LoggerFactory.getLogger(WorldConfiguration.class);
	private final String activity;
	private final Country country;
	private final boolean members;
	private final boolean quickChat;
	private final boolean lootshare;
	private final boolean highlight;
	private final int flags;

	public WorldConfiguration(String activity, Country country, boolean members, boolean quickChat, boolean lootshare,
			boolean highlight) {
		this.activity = activity;
		this.country = country;
		this.members = members;
		this.quickChat = quickChat;
		this.lootshare = lootshare;
		this.highlight = highlight;
		this.flags = constructFlags();
	}

	@Override
	public String toString() {
		return "WorldConfiguration [activity=" + activity + ", country=" + country + ", members=" + members
				+ ", quickChat=" + quickChat + ", lootshare=" + lootshare + ", highlight=" + highlight + ", flags="
				+ flags + "]";
	}

	public WorldConfiguration() {
		country = new Country(DEFAULT_FLAG, DEFAULT_COUNTRY);
		activity = "";
		members = DEFAULT_MEMBERS.booleanValue();
		quickChat = DEFAULT_QUICKCHAT.booleanValue();
		lootshare = DEFAULT_LOOTSHARE.booleanValue();
		highlight = DEFAULT_HIGHLIGHT.booleanValue();
		this.flags = constructFlags();
	}

	private int constructFlags() {
		int flags = members ? World.FLAG_MEMBERS : 0;
		if (quickChat)
			flags |= World.FLAG_QUICK_CHAT;
		if (lootshare)
			flags |= World.FLAG_LOOT_SHARE;
		if (highlight)
			flags |= World.FLAG_HIGHLIGHT;
		return flags;
	}

	public int getFlags() {
		return flags;
	}

	public String getActivity() {
		return activity;
	}

	public Country getCountry() {
		return country;
	}

	public boolean isMembers() {
		return members;
	}

	public boolean isQuickChat() {
		return quickChat;
	}

	public boolean isLootshare() {
		return lootshare;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public static WorldConfiguration parse(String string) {
		Properties properties = new Properties();
		try (InputStream is = new FileInputStream("data/world.conf")) {
			properties.load(is);
			int flagId = Integer.parseInt(properties.getProperty("flagId", DEFAULT_FLAG.toString()));
			String countryName = properties.getProperty("countryName", DEFAULT_COUNTRY);
			String activity = properties.getProperty("activity", "");
			boolean members = properties.getProperty("members", DEFAULT_MEMBERS.toString()).equalsIgnoreCase("true");
			boolean quickChat = properties.getProperty("quickChat", DEFAULT_QUICKCHAT.toString())
					.equalsIgnoreCase("true");
			boolean lootshare = properties.getProperty("lootshare", DEFAULT_LOOTSHARE.toString())
					.equalsIgnoreCase("true");
			boolean highlight = properties.getProperty("highlight", DEFAULT_HIGHLIGHT.toString())
					.equalsIgnoreCase("true");
			return new WorldConfiguration(activity, new Country(flagId, countryName), members, quickChat, lootshare,
					highlight);
		} catch (Exception e) {
			logger.warn("Failed to parse world configuration. Reverting to defaults. Reason: " + e.getMessage());
			return new WorldConfiguration();
		}
	}

	private static final Integer DEFAULT_FLAG = Country.FLAG_FINLAND;
	private static final String DEFAULT_COUNTRY = "Finland";
	private static final Boolean DEFAULT_MEMBERS = false;
	private static final Boolean DEFAULT_QUICKCHAT = false;
	private static final Boolean DEFAULT_LOOTSHARE = false;
	private static final Boolean DEFAULT_HIGHLIGHT = false;
}