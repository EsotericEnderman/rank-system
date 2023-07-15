package net.slqmy.rank_system.managers;

import net.slqmy.rank_system.Main;
import net.slqmy.rank_system.Rank;
import net.slqmy.rank_system.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class RankManager {
	private final Main plugin;
	private final YamlConfiguration config;
	private final YamlConfiguration playerRanks;
	private final HashMap<UUID, PermissionAttachment> permissions = new HashMap<>();
	public HashMap<UUID, PermissionAttachment> getPermissions() { return permissions; }

	public RankManager(final Main plugin) {
		this.plugin = plugin;
		this.config = (YamlConfiguration) plugin.getConfig();
		this.playerRanks = plugin.getPlayerRanks();
	}

	public boolean setRank(final UUID uuid, final String rankName, final boolean isFirstJoin) {
		final Rank targetRank = getRank(rankName);

		if (targetRank == null) {
			return false;
		}

		// Update permissions.
		if (Bukkit.getOfflinePlayer(uuid).isOnline() && !isFirstJoin) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;
			final PermissionAttachment attachment;

			if (permissions.containsKey(uuid)) {
				attachment = permissions.get(uuid);
			} else {
				attachment = player.addAttachment(plugin);
				permissions.put(uuid, attachment);
			}

			// Remove previous permissions (if there are any).
			for (final String permission : plugin.getRankManager().getPlayerRank(uuid).getPermissions()) {
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
		playerRanks.set(uuid.toString(), Objects.equals(config.getString("defaultRank"), targetRankName) ? null : targetRankName);

		try {
			playerRanks.save(plugin.getPlayerRanksFile());
		} catch (final IOException exception) {
			Logger logger = Logger.getLogger("Minecraft");

			logger.info(Utility.getLogPrefix() + "Error while saving rank " + targetRankName + " to player with UUID " + uuid + "!");
			logger.info(Utility.getLogPrefix() + exception.getMessage());

			exception.printStackTrace();
		}


		// Give the player the rank's name prefix.
		if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
			final	Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			final	NameTagManager nameTagManager = plugin.getNameTagManager();

			nameTagManager.removeNameTag(player);
			nameTagManager.addNewNameTag(player);
		}

		return true;
	}

	public Rank getPlayerRank(final UUID uuid) {
		String playerRankName = playerRanks.getString(uuid.toString());

		if (playerRankName == null) {
			return getDefaultRank();
		}

		return getRank(playerRankName);
	}

	public Rank getRank(final String rankName) {
		final List<Rank> ranks = getRanksList();

		assert ranks != null;
		for (final Rank rank : ranks) {
			if (rank.getName().equalsIgnoreCase(rankName)) {
				return rank;
			}
		}

		return null;
	}

	public Rank getDefaultRank() { return getRank(config.getString("defaultRank")); }

	public List<Rank> getRanksList() {
		final List<Rank> results = new ArrayList<>();

		final List<LinkedHashMap<String, Object>> ranks = (List<LinkedHashMap<String, Object>>) config.getList("ranks");
		assert ranks != null;

		for (LinkedHashMap<String, Object> rank : ranks) {
			results.add(Rank.from(rank));
		}

		return results;
	}
}
