package net.slqmy.rank_system.types;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Rank {
	private static final Rank NULL_RANK = new Rank("", "", new ArrayList<>());
	private static final String PERMISSIONS_KEY = "permissions";

	private final String name;
	private final String displayName;
	private List<String> permissions;

	public Rank(@NotNull final String name, @NotNull final String displayName, final List<@NotNull String> permissions) {
		this.name = name;
		this.displayName = displayName;

		if (permissions != null) {
			this.permissions = permissions;
		}
	}

	public static Rank getNullRank() {
		return NULL_RANK;
	}

	@SuppressWarnings("unchecked")
	@Contract("_ -> new")
	public static @NotNull Rank from(final @NotNull Map<@NotNull String, Object> rank) {
		// Check for invalid input.
		if (!rank.containsKey("name") || !rank.containsKey("display-name")) {
			throw new IllegalArgumentException("Object must have properties 'name' & 'display-name'!");
		}

		if (rank.get(PERMISSIONS_KEY) != null && (!(rank.get(PERMISSIONS_KEY) instanceof List))) {
			throw new IllegalArgumentException("Type of property 'permissions' must be List<String>!");
		}

		return new Rank((String) rank.get("name"), (String) rank.get("display-name"),
				(List<String>) rank.get(PERMISSIONS_KEY));
	}

	public @NotNull String getName() {
		return name;
	}

	public @NotNull String getDisplayName() {
		return ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', displayName) + ChatColor.RESET;
	}

	public @NotNull List<@NotNull String> getPermissions() {
		if (permissions != null) {
			return permissions;
		} else {
			return new ArrayList<>();
		}
	}
}
