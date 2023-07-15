package net.slqmy.rank_system;

import net.slqmy.rank_system.managers.NameTagManager;
import net.slqmy.rank_system.managers.RankManager;
import net.slqmy.rank_system.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public final class EventListener implements Listener {
	private final Main plugin;
	private final YamlConfiguration config;
	private final RankManager rankManager;
	private final NameTagManager nameTagManager;
	private final Map<UUID, PermissionAttachment> permissions;

	EventListener(final @NotNull Main plugin) {
		this.plugin = plugin;
		this.config = (YamlConfiguration) plugin.getConfig();
		this.rankManager = plugin.getRankManager();
		this.nameTagManager = plugin.getNameTagManager();
		this.permissions = rankManager.getPermissions();
	}

	@EventHandler
	public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
		final String defaultRank = config.getString("defaultRank");
		final Player player = event.getPlayer();
		final UUID playerUUID = player.getUniqueId();

		// Attempt to assign the player the default rank, if it exists.
		if (defaultRank != null && !player.hasPlayedBefore()) {
			final boolean success = rankManager.setRank(playerUUID, defaultRank, true);

			if (!success) {
				Logger.getLogger("Minecraft")
						.info(Utility.getLogPrefix() + "Invalid configuration! Default rank does not exist in rank list.");
				return;
			}
		}

		// Give the player a scoreboard with name tags of other players.
		nameTagManager.setNameTags(player);
		// Add the player to everyone else's scoreboard.
		nameTagManager.addNewNameTag(player);

		// Add permissions to the player.
		final PermissionAttachment attachment;

		if (permissions.containsKey(playerUUID)) {
			attachment = permissions.get(playerUUID);
		} else {
			attachment = player.addAttachment(plugin);
			permissions.put(playerUUID, attachment);
		}

		for (final String permission : rankManager.getPlayerRank(playerUUID).getPermissions()) {
			attachment.setPermission(permission, true);
		}
	}

	@EventHandler
	public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final UUID playerUUID = player.getUniqueId();

		// Remove player from other player's scoreboard and from the hashmap.
		nameTagManager.removeNameTag(player);
		permissions.remove(playerUUID, permissions.get(playerUUID));
	}

	@EventHandler
	public void onAsyncPlayerChat(final @NotNull AsyncPlayerChatEvent event) {
		// Custom chat message:
		// (rank formatting) <rank name> (white) <player> » (grey) <message>
		event.setCancelled(true);

		final Player player = event.getPlayer();

		// Note: colour codes that represent colours actually reset the previous colour
		// codes.
		Bukkit.broadcastMessage(rankManager.getPlayerRank(player.getUniqueId()).getDisplayName() + " " + player.getName()
				+ " » " + ChatColor.GRAY + event.getMessage());
	}
}
