package net.slqmy.rank_system_plugin.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import net.slqmy.rank_system_plugin.RankSystemPlugin;
import net.slqmy.rank_system_plugin.managers.RankManager;

public final class AsyncPlayerChatEventListener implements Listener {
	private final RankManager rankManager;

	public AsyncPlayerChatEventListener(@NotNull final RankSystemPlugin plugin) {
		this.rankManager = plugin.getRankManager();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAsyncPlayerChat(final @NotNull AsyncPlayerChatEvent event) {
		// Custom chat message:
		// (rank formatting) <rank name> (white) <player> » (grey) <message>
		event.setCancelled(true);

		final Player player = event.getPlayer();

		final String rankDisplayName = rankManager.getPlayerRank(player.getUniqueId(), false).getDisplayName();

		// Note: colour codes that represent colours actually reset the previous colour
		// codes.

		// (If the display name is blank)
		Bukkit.broadcastMessage(
				(rankDisplayName.equals(ChatColor.RESET.toString() + ChatColor.RESET) ? "" : rankDisplayName + " ")
						+ player.getName()
						+ " » " + ChatColor.GRAY + event.getMessage());
	}
}
