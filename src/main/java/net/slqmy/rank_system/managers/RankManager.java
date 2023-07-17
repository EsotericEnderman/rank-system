package net.slqmy.rank_system.managers;

import net.slqmy.rank_system.RankSystem;
import net.slqmy.rank_system.types.Rank;
import net.slqmy.rank_system.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class RankManager {
	private static final Rank NULL_RANK = Rank.getNullRank();
	private static final Pattern PREFIX_PATTERN = Pattern.compile("^[a-z]+_");

	private static final int PLAYER_INPUT_ARGUMENT_NUMBER = 1;
	private static final int RANK_INPUT_ARGUMENT_NUMBER = 2;
	private static final int RANK_COMMAND_ARGUMENT_LENGTH = 2;

	private static final String[] LETTERS = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
			"n", "o",
			"p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

	private final RankSystem plugin;
	private final YamlConfiguration config;
	private final YamlConfiguration playerRanksConfig;
	private final Map<UUID, PermissionAttachment> permissions = new HashMap<>();

	public RankManager(final @NotNull RankSystem plugin) {
		this.plugin = plugin;
		this.config = (YamlConfiguration) plugin.getConfig();
		this.playerRanksConfig = plugin.getPlayerRanksConfig();
	}

	public static int getPlayerInputArgumentNumber() {
		return PLAYER_INPUT_ARGUMENT_NUMBER;
	}

	public static int getRankInputArgumentNumber() {
		return RANK_INPUT_ARGUMENT_NUMBER;
	}

	public static int getRankCommandArgumentLength() {
		return RANK_COMMAND_ARGUMENT_LENGTH;
	}

	public Map<UUID, PermissionAttachment> getPermissions() {
		return permissions;
	}

	public boolean setRank(@NotNull final UUID uuid, @NotNull final String rankName, final boolean isFirstJoin) {
		final Rank targetRank = getRank(rankName, false);

		if (targetRank == null) {
			return false;
		}

		// Update permissions.
		if (Bukkit.getOfflinePlayer(uuid).isOnline() && !isFirstJoin) {
			final Player player = Bukkit.getPlayer(uuid);
			final PermissionAttachment attachment;

			assert player != null;
			if (permissions.containsKey(uuid)) {
				attachment = permissions.get(uuid);
			} else {
				attachment = player.addAttachment(plugin);
				permissions.put(uuid, attachment);
			}

			// Remove previous permissions (if there are any).
			for (final String permission : plugin.getRankManager().getPlayerRank(uuid, false).getPermissions()) {
				// If-check just to be safe.
				if (player.hasPermission(permission)) {
					attachment.unsetPermission(permission);
				}
			}

			// Set the new permissions.
			for (final String permission : targetRank.getPermissions()) {
				attachment.setPermission(permission, true);
			}
		}

		final String targetRankName = targetRank.getName();

		// If it's not the default rank, save the rank.
		playerRanksConfig.set(uuid.toString(),
				getDefaultRank(false).getName().equals(targetRankName) ? null : targetRankName);

		try {
			playerRanksConfig.save(plugin.getPlayerRanksFile());
		} catch (final IOException exception) {
			Utility.log("Error while saving rank '" + targetRankName + "' to player with UUID " + uuid + "!");
			Utility.log(exception.getMessage());

			exception.printStackTrace();
		}

		// Give the player the rank's name prefix.
		if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			final NameTagManager nameTagManager = plugin.getNameTagManager();

			nameTagManager.removeNameTag(player);
			nameTagManager.addNewNameTag(player);
		}

		return true;
	}

	public @NotNull Rank getPlayerRank(final @NotNull UUID uuid, final boolean includePrefix) {
		String playerRankName = playerRanksConfig.getString(uuid.toString());

		if (playerRankName == null) {
			Rank defaultRank = getDefaultRank(includePrefix);

			if (defaultRank == null) {
				return NULL_RANK;
			} else {
				return defaultRank;
			}
		}

		Rank playerRank = getRank(playerRankName, includePrefix);

		if (playerRank == null) {
			return NULL_RANK;
		}

		return playerRank;
	}

	public Rank getRank(final String rankName, boolean includePrefixes) {
		final List<Rank> ranks = getRanksList(includePrefixes);

		for (final Rank rank : ranks) {
			String targetRankName = rank.getName();

			if ((!includePrefixes && targetRankName.equals(rankName))
					|| (includePrefixes && Utility.replaceAll(targetRankName, PREFIX_PATTERN, "").equals(rankName))) {
				return rank;
			}
		}

		return null;
	}

	public Rank getDefaultRank(boolean includePrefix) {
		return getRank(config.getString("default-rank"), includePrefix);
	}

	public @NotNull List<Rank> getRanksList(final boolean includePrefixes) {
		final List<Rank> results = new ArrayList<>();

		List<LinkedHashMap<String, Object>> ranks = (List<LinkedHashMap<String, Object>>) config.getList("ranks");

		if (ranks == null) {
			ranks = new ArrayList<>();
		}

		if (includePrefixes) {
			int charactersNeeded = (int) Math.ceil(ranks.size() / (double) LETTERS.length);

			for (int i = 0; i < ranks.size(); i++) {
				LinkedHashMap<String, Object> rank = new LinkedHashMap<>(ranks.get(i));

				StringBuilder prefix = new StringBuilder();

				for (int k = 1; k <= charactersNeeded; k++) {
					String letter = LETTERS[(int) Math.floor(i / Math.pow(LETTERS.length, (double) charactersNeeded - k))
							% LETTERS.length];

					prefix.append(letter);
				}

				rank.put("name", prefix + "_" + rank.get("name"));

				results.add(Rank.from(rank));
			}
		} else {
			for (LinkedHashMap<String, Object> rank : ranks) {
				results.add(Rank.from(rank));
			}
		}

		return results;
	}
}
