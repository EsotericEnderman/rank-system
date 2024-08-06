package net.slqmy.rank_system_plugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import net.slqmy.rank_system_plugin.RankSystemPlugin;
import net.slqmy.rank_system_plugin.types.Rank;

import java.util.List;
import java.util.UUID;

public final class NameTagManager {
	private final RankManager rankManager;
	private final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

	public NameTagManager(final @NotNull RankSystemPlugin plugin) {
		rankManager = plugin.getRankManager();
	}

	public void setNameTags(final @NotNull Player player) {
		assert scoreboardManager != null;

		final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
		// Give the player a new scoreboard.
		player.setScoreboard(scoreboard);

		// Create teams for every rank with the rank prefix as the prefix for the team.
		final List<Rank> ranksList = rankManager.getRanksList(true);

		for (final Rank rank : ranksList) {
			final Team team = scoreboard.registerNewTeam(rank.getName());
			team.setPrefix(rank.getDisplayName() + " ");
		}

		final UUID playerUUID = player.getUniqueId();

		// Add every OTHER player (the current player is managed with the addNewNameTag
		// method) to the player's scoreboard.
		for (final Player target : Bukkit.getOnlinePlayers()) {
			final UUID targetUUID = target.getUniqueId();

			if (!targetUUID.equals(playerUUID)) {
				final Rank targetRank = rankManager.getPlayerRank(targetUUID, true);

				final Team targetRankTeam = scoreboard.getTeam(targetRank.getName());

				if (targetRankTeam != null) {
					targetRankTeam.addEntry(target.getName());
				}
			}
		}
	}

	public void addNewNameTag(final @NotNull Player player) {
		final Rank playerRank = rankManager.getPlayerRank(player.getUniqueId(), true);

		final String rankName = playerRank.getName();
		final String playerName = player.getName();

		// Add the player to everyone's scoreboard.
		for (final Player target : Bukkit.getOnlinePlayers()) {
			final Team targetRankTeam = target.getScoreboard().getTeam(rankName);

			if (targetRankTeam != null) {
				targetRankTeam.addEntry(playerName);
			}
		}
	}

	// Remove a player's name-tag from everyone's scoreboard.
	public void removeNameTag(final @NotNull Player player) {
		final String playerName = player.getName();

		for (final Player target : Bukkit.getOnlinePlayers()) {
			final Team playerTeam = target.getScoreboard().getEntryTeam(playerName);

			if (playerTeam != null) {
				playerTeam.removeEntry(playerName);
			}
		}
	}
}
