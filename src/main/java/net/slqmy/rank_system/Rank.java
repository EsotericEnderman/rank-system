package net.slqmy.rank_system;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Rank {
	private static final String PERMISSIONS_KEY = "permissions";

	private static final Rank nullRank = new Rank("", "", new ArrayList<>());

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

	public static Rank getNullRank() {
		return nullRank;
	}

	public String getName() {
		return name;
	}

	public @NotNull String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName) + ChatColor.RESET;
	}

	public List<String> getPermissions() {
		if (permissions != null) {
			return permissions;
		} else {
			return new ArrayList<>();
		}
	}

	@Contract("_ -> new")
	public static @NotNull Rank from(final @NotNull Map<String, Object> rank) {
		// Check for invalid input.
		if (!rank.containsKey("name") || !rank.containsKey("displayName")) {
			throw new IllegalArgumentException("Object must have properties 'name' & 'displayName'!");
		}

		if (rank.get(PERMISSIONS_KEY) != null && (!(rank.get(PERMISSIONS_KEY) instanceof List))) {
			throw new IllegalArgumentException("Type of property 'permissions' must be List<String>!");
		}

		return new Rank((String) rank.get("name"), (String) rank.get("displayName"),
				(List<String>) rank.get(PERMISSIONS_KEY));
	}
}
