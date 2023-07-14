package net.slqmy.rank_system;

import org.bukkit.ChatColor;

import java.util.LinkedHashMap;
import java.util.List;

public final class Rank {
	private final String name;
	public String getName() { return name; }

	private final String displayName;
	public String getDisplayName() { return ChatColor.translateAlternateColorCodes('&', displayName) + ChatColor.RESET; }

	private final List<String> permissions;
	public List<String> getPermissions() { return permissions; }

	public Rank(final String name, final String displayName, final List<String> permissions) {
		this.name = name;
		this.displayName = displayName;
		this.permissions = permissions;
	}

	public static Rank from(final LinkedHashMap<String, Object> rank) {
		// Check for invalid input.
		if (!rank.containsKey("name") || !rank.containsKey("displayName") || !rank.containsKey("permissions")) {
			throw new IllegalArgumentException("Object must have fields 'name', 'displayName', and 'permissions'!");
		}

		return new Rank((String) rank.get("name"), (String) rank.get("displayName"), (List<String>) rank.get("permissions"));
	}
}
