package net.slqmy.rank_system.managers;

import net.slqmy.rank_system.Main;
import net.slqmy.rank_system.types.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class NameTags {
	private final Ranks ranks;
	private final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

	public NameTags(final @NotNull Main plugin) {
		ranks = plugin.getRankManager();
	}

	public void setNameTags(final @NotNull Player player) {
		assert scoreboardManager != null;

		final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
		// Assign the player a new scoreboard.
		player.setScoreboard(scoreboard);

		// Create teams for every rank with the rank prefix as the prefix for the team.
		final List<Rank> ranksList = this.ranks.getRanksList();

		for (final Rank rank : ranksList) {
			final Team team = scoreboard.registerNewTeam(rank.getName());
			team.setPrefix(rank.getDisplayName() + " ");
		}

		UUID playerUUID = player.getUniqueId();

		// Add every OTHER player (the current player is managed with the addNewNameTag
		// method) to the player's scoreboard.
		for (final Player target : Bukkit.getOnlinePlayers()) {
			UUID targetUUID = target.getUniqueId();

			if (targetUUID.equals(playerUUID)) {
				final Team targetRankTeam = scoreboard.getTeam(this.ranks.getPlayerRank(targetUUID).getName());

				if (targetRankTeam != null) {
					targetRankTeam.addEntry(target.getName());
				}
			}
		}
	}

	public void addNewNameTag(final @NotNull Player player) {
		final String playerName = player.getName();
		final Rank playerRank = ranks.getPlayerRank(player.getUniqueId());
		final String rankName = playerRank.getName();

		// Add a player to everyone's scoreboard.
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
