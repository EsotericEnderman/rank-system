package net.slqmy.rank_system.events;

import net.slqmy.rank_system.Main;
import net.slqmy.rank_system.managers.NameTags;
import net.slqmy.rank_system.managers.Ranks;
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
	private static final Logger LOGGER = Main.getPluginLogger();

	private final Main plugin;
	private final YamlConfiguration config;
	private final Ranks ranks;
	private final NameTags nameTags;
	private final Map<UUID, PermissionAttachment> permissions;

	public EventListener(final @NotNull Main plugin) {
		this.plugin = plugin;
		this.config = (YamlConfiguration) plugin.getConfig();
		this.ranks = plugin.getRankManager();
		this.nameTags = plugin.getNameTagManager();
		this.permissions = ranks.getPermissions();
	}

	@EventHandler
	public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
		final String defaultRank = config.getString("default-rank");
		final Player player = event.getPlayer();
		final UUID playerUUID = player.getUniqueId();

		// Attempt to assign the player the default rank, if it exists.
		if (defaultRank != null && !player.hasPlayedBefore()) {
			final boolean success = ranks.setRank(playerUUID, defaultRank, true);

			if (!success) {
				LOGGER.info(Utility.getLogPrefix() + "Invalid configuration! Default rank does not exist in rank list.");
				return;
			}
		}

		// Give the player a scoreboard with name tags of other players.
		nameTags.setNameTags(player);
		// Add the player to everyone else's scoreboard.
		nameTags.addNewNameTag(player);

		// Add permissions to the player.
		final PermissionAttachment attachment;

		if (permissions.containsKey(playerUUID)) {
			attachment = permissions.get(playerUUID);
		} else {
			attachment = player.addAttachment(plugin);
			permissions.put(playerUUID, attachment);
		}

		for (final String permission : ranks.getPlayerRank(playerUUID).getPermissions()) {
			attachment.setPermission(permission, true);
		}
	}

	@EventHandler
	public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final UUID playerUUID = player.getUniqueId();

		// Remove player from other player's scoreboard and from the hashmap.
		nameTags.removeNameTag(player);
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
		Bukkit.broadcastMessage(ranks.getPlayerRank(player.getUniqueId()).getDisplayName() + " " + player.getName()
				+ " » " + ChatColor.GRAY + event.getMessage());
	}
}
