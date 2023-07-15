package net.slqmy.rank_system.tab_completers;

import net.slqmy.rank_system.Main;
import net.slqmy.rank_system.Rank;
import net.slqmy.rank_system.managers.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class RankCommandTabCompleter implements TabCompleter {
	private final RankManager rankManager;

	public RankCommandTabCompleter(Main plugin) { this.rankManager = plugin.getRankManager(); }

	@Override
	public @NotNull List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String label, final @NotNull String[] args) {
		final List<String> results = new ArrayList<>();

		if (sender instanceof Player && sender.isOp()) {
			if (args.length == 1) {
				for (final OfflinePlayer player : Bukkit.getOfflinePlayers()) {
					results.add(player.getName());
				}

				return StringUtil.copyPartialMatches(args[0], results, new ArrayList<>());
			} else if (args.length == 2) {
				for (final Rank rank : rankManager.getRanksList()) {
					results.add(rank.getName());
				}

				return StringUtil.copyPartialMatches(args[1], results, new ArrayList<>());
			}
		}

		return results;
	}
}
