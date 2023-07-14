package net.slqmy.rank_system.managers;

import net.slqmy.rank_system.Main;
import net.slqmy.rank_system.Rank;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.IOException;
import java.util.*;

public class RankManager {
	private final Main plugin;
	private final YamlConfiguration playerRanks;
	private final HashMap<UUID, PermissionAttachment> permissions = new HashMap<>();
	public HashMap<UUID, PermissionAttachment> getPermissions() { return permissions; }

	public RankManager(final Main plugin) {
		this.plugin = plugin;
		this.playerRanks = plugin.getPlayerRanks();
	}

	public boolean setRank(final UUID uuid, final String rankName, final boolean isFirstJoin) {
		final YamlConfiguration config = (YamlConfiguration) plugin.getConfig();
		final List<LinkedHashMap<String, Object>> ranks = (List<LinkedHashMap<String, Object>>) config.getList("ranks");
		Rank targetRank = null;

		// Find rank from file.
		assert ranks != null;
		for (final LinkedHashMap<String, Object> rank : ranks) {
			final Rank currentRank = Rank.from(rank);

			if (currentRank.getName().equalsIgnoreCase(rankName)) {
				targetRank = currentRank;
			}
		}

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
			for (final String permission : plugin.getRankManager().getRank(uuid).getPermissions()) {
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

		// If it's not the default rank, give the rank to the player.
		if (!Objects.equals(config.getString("defaultRank"), targetRank.getName())) {
			playerRanks.set(uuid.toString(), targetRank.getName());

			try {
				playerRanks.save(plugin.getPlayerRanksFile());
			} catch (final IOException exception) {
				System.out.println("[Rank-System] Error while saving rank " + rankName + " to player with UUID " + uuid + "!");
				System.out.println(exception.getMessage());

				exception.printStackTrace();
			}

			if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
				Player player = Bukkit.getPlayer(uuid);

				NameTagManager nameTagManager = plugin.getNameTagManager();
				nameTagManager.removeNameTag(player);
				assert player != null;
				nameTagManager.addNewNameTag(player);
			}
		}

		return true;
	}

	public Rank getRank(final UUID uuid) {
		final String rankName = playerRanks.getString(uuid.toString());

		final List<LinkedHashMap<String, Object>> ranks = (List<LinkedHashMap<String, Object>>) plugin.getConfig().getList("ranks");

		assert ranks != null;
		for (final LinkedHashMap<String, Object> rank : ranks) {
			final Rank currentRank = Rank.from(rank);

			if (currentRank.getName().equalsIgnoreCase(rankName)) {
				return currentRank;
			}
		}

		return null;
	}
}
