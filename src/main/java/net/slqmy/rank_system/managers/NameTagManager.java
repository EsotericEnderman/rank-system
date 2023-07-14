package net.slqmy.rank_system.managers;

import net.slqmy.rank_system.Main;
import net.slqmy.rank_system.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.LinkedHashMap;
import java.util.List;

public final class NameTagManager {
	private final Main plugin;

	public NameTagManager(Main plugin) {
		this.plugin = plugin;
	}

	public void setNameTags(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

		List<LinkedHashMap<String, Object>> ranks = (List<LinkedHashMap<String, Object>>) plugin.getConfig().getList("ranks");

		assert ranks != null;
		for (LinkedHashMap<String, Object> rank : ranks) {
			Rank currentRank = Rank.from(rank);

			Team team = player.getScoreboard().registerNewTeam(currentRank.getName());
			team.setPrefix(currentRank.getDisplayName() + " ");
		}

		for (Player target : Bukkit.getOnlinePlayers()) {
			if (target.getUniqueId() != player.getUniqueId()) {
				player.getScoreboard().getTeam(plugin.getRankManager().getRank(target.getUniqueId()).getName()).addEntry(target.getName());
			}
		}
	}

	public void addNewNameTag(Player player) {
		Rank rank = plugin.getRankManager().getRank(player.getUniqueId());

		for (Player target : Bukkit.getOnlinePlayers()) {
			target.getScoreboard().getTeam(rank.getName()).addEntry(player.getName());
		}
	}

	public void removeNameTag(Player player) {
		for (Player target : Bukkit.getOnlinePlayers()) {
			target.getScoreboard().getEntryTeam(player.getName()).removeEntry(player.getName());
		}
	}
}
