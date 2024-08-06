package net.slqmy.rank_system_plugin.events.listeners;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;

import net.slqmy.rank_system_plugin.RankSystemPlugin;
import net.slqmy.rank_system_plugin.managers.NameTagManager;

public final class PlayerQuitEventListener implements Listener {
  private final NameTagManager nameTagManager;
  private final Map<UUID, PermissionAttachment> permissions;

  public PlayerQuitEventListener(@NotNull final RankSystemPlugin plugin) {
    this.nameTagManager = plugin.getNameTagManager();
    this.permissions = plugin.getRankManager().getPermissions();
  }

  @EventHandler
  public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    final UUID playerUUID = player.getUniqueId();

    // Remove player from other player's scoreboard and from the hashmap.
    nameTagManager.removeNameTag(player);
    permissions.remove(playerUUID, permissions.get(playerUUID));
  }
}
