package net.slqmy.rank_system.tab_completers;

import net.slqmy.rank_system.Main;
import net.slqmy.rank_system.types.Rank;
import net.slqmy.rank_system.managers.Ranks;
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
	private final Ranks ranks;

	public RankCommandTabCompleter(@NotNull Main plugin) {
		this.ranks = plugin.getRankManager();
	}

	@Override
	public @NotNull List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command,
			final @NotNull String label, final @NotNull String[] args) {
		final List<String> results = new ArrayList<>();

		if (sender instanceof Player && sender.isOp()) {
			if (args.length == Ranks.getPlayerInputArgumentNumber()) {
				for (final OfflinePlayer player : Bukkit.getOfflinePlayers()) {
					results.add(player.getName());
				}

				return StringUtil.copyPartialMatches(args[0], results, new ArrayList<>());
			} else if (args.length == Ranks.getRankInputArgumentNumber()) {
				for (final Rank rank : ranks.getRanksList()) {
					results.add(rank.getName());
				}

				return StringUtil.copyPartialMatches(args[1], results, new ArrayList<>());
			}
		}

		return results;
	}
}
