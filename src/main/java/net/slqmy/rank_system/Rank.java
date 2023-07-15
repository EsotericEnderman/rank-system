package net.slqmy.rank_system;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class Rank {
	private final String name;
	public String getName() { return name; }

	private final String displayName;
	public String getDisplayName() { return ChatColor.translateAlternateColorCodes('&', displayName) + ChatColor.RESET; }

	private List<String> permissions;
	public List<String> getPermissions() {
		if (permissions != null) {
			return permissions;
		} else {
			return new ArrayList<>();
		}
	}

	public Rank(@NotNull final String name, @NotNull final String displayName, final List<String> permissions) {
		this.name = name;
		this.displayName = displayName;

		if (permissions != null) {
			this.permissions = permissions;
		}
	}

	public static Rank from(@NotNull final LinkedHashMap<String, Object> rank) {
		// Check for invalid input.
		if (!rank.containsKey("name") || !rank.containsKey("displayName")) {
			throw new IllegalArgumentException("Object must have properties 'name' & 'displayName'!");
		}

		return new Rank((String) rank.get("name"), (String) rank.get("displayName"), (List<String>) rank.get("permissions"));
	}
}
