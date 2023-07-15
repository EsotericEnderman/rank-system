package net.slqmy.rank_system.managers;

import net.slqmy.rank_system.Main;
import net.slqmy.rank_system.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.UUID;

public final class NameTagManager {
	private final RankManager rankManager;
	private final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

	public NameTagManager(final Main plugin) { rankManager = plugin.getRankManager(); }

	public void setNameTags(final Player player) {
		assert scoreboardManager != null;

		final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
		// Assign the player a new scoreboard.
		player.setScoreboard(scoreboard);

		// Create teams for every rank with the rank prefix as the prefix for the team.
		final List<Rank> ranks = rankManager.getRanksList();
		assert ranks != null;

		for (final Rank rank : ranks) {
			final	Team team = scoreboard.registerNewTeam(rank.getName());
			team.setPrefix(rank.getDisplayName() + " ");
		}

		UUID playerUUID = player.getUniqueId();

		// Add every OTHER player (the current player is managed with the addNewNameTag method) to the player's scoreboard.
		for (final Player target : Bukkit.getOnlinePlayers()) {
			UUID targetUUID = target.getUniqueId();

			if (targetUUID.equals(playerUUID)) {
				final Team targetRankTeam = scoreboard.getTeam(rankManager.getPlayerRank(targetUUID).getName());

				if (targetRankTeam != null) {
					targetRankTeam.addEntry(target.getName());
				}
			}
		}
	}

	public void addNewNameTag(final Player player) {
		final String playerName = player.getName();
		final Rank playerRank = rankManager.getPlayerRank(player.getUniqueId());
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
	public void removeNameTag(final Player player) {
		final String playerName = player.getName();

		for (final Player target : Bukkit.getOnlinePlayers()) {
			final Team playerTeam = target.getScoreboard().getEntryTeam(playerName);

			if (playerTeam != null) {
				playerTeam.removeEntry(playerName);
			}
		}
	}
}
