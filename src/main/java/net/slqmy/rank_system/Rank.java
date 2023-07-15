package net.slqmy.rank_system;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Rank {
	private static final String PERMISSIONS_KEY = "permissions";

	private final String name;
	private final String displayName;
	private List<String> permissions;

	public Rank(final String name, final String displayName, final List<String> permissions) {
		this.name = name;
		this.displayName = displayName;

		if (permissions != null) {
			this.permissions = permissions;
		}
	}

	public String getName() { return name; }
	public String getDisplayName() { return ChatColor.translateAlternateColorCodes('&', displayName) + ChatColor.RESET; }
	public List<String> getPermissions() {
		if (permissions != null) {
			return permissions;
		} else {
			return new ArrayList<>();
		}
	}

	public static Rank nullRank = new Rank("", "", new ArrayList<>());

	public static Rank from(final Map<String, Object> rank) {
		// Check for invalid input.
		if (!rank.containsKey("name") || !rank.containsKey("displayName")) {
			throw new IllegalArgumentException("Object must have properties 'name' & 'displayName'!");
		}

		if (rank.get(PERMISSIONS_KEY) != null && (!(rank.get(PERMISSIONS_KEY) instanceof List))) {
			throw new IllegalArgumentException("Type of property 'permissions' must be List<String>!");
		}

		return new Rank((String) rank.get("name"), (String) rank.get("displayName"), (List<String>) rank.get(PERMISSIONS_KEY));
	}
}
