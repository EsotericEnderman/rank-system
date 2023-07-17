package net.slqmy.rank_system.commands;

import net.slqmy.rank_system.RankSystem;
import net.slqmy.rank_system.managers.RankManager;
import net.slqmy.rank_system.types.Rank;
import net.slqmy.rank_system.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RankCommand implements CommandExecutor, TabCompleter {
	// rank <player> <rank name>
	private final RankManager rankManager;

	public RankCommand(final @NotNull RankSystem plugin) {
		this.rankManager = plugin.getRankManager();
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
			@NotNull final String label, @NotNull final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;

			if (!player.isOp()) {
				Utility.sendMessage(player, ChatColor.RED + "You must be op to execute this command!");
				return true;
			}

			if (args.length != RankManager.getRankCommandArgumentLength()) {
				return false;
			}

			final String targetName = args[0];

			final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
			final String correctTargetName = target.getName();

			if (correctTargetName == null) {
				Utility.sendMessage(player, ChatColor.RED + "That player does not exist!");
				return true;
			}

			final String rankName = args[1];
			final Rank rank = rankManager.getRank(rankName, false);

			if (rank == null) {
				Utility.sendMessage(player, ChatColor.RED + "That rank does not exist in the configuration file!");

				return true;
			}

			final UUID targetUUID = target.getUniqueId();
			final Rank previousRank = rankManager.getPlayerRank(targetUUID, false);
			final String previousRankName = previousRank.getName();

			final String correctRankName = rank.getName();
			final boolean areSameRank = previousRankName.equals(correctRankName);

			final String rankDisplayName = rank.getDisplayName();

			if (player.getUniqueId() == targetUUID) {
				if (areSameRank) {
					Utility.sendMessage(player,
							ChatColor.RED + "Your rank is already set to " + rankDisplayName + ChatColor.GREEN + "!");
				} else {
					rankManager.setRank(targetUUID, rankName, false);

					Utility.sendMessage(player, ChatColor.GREEN + "Successfully set your rank to " + rankDisplayName
							+ ChatColor.GREEN + "!");
				}
			} else {
				final String previousDisplayName = previousRank.getDisplayName();
				final String previousRankDisplay = "".equals(previousDisplayName) ? "" : previousDisplayName + " ";

				if (areSameRank) {
					Utility.sendMessage(player,
							previousRankDisplay + ChatColor.BOLD + correctTargetName + ChatColor.RED + "'s rank is already set to "
									+ rankDisplayName + ChatColor.RED + "!");
				} else {
					rankManager.setRank(targetUUID, rankName, false);

					Utility.sendMessage(player,
							ChatColor.GREEN + "Successfully set " + previousRankDisplay
									+ ChatColor.BOLD
									+ correctTargetName + ChatColor.GREEN + "'s rank to "
									+ rankDisplayName + ChatColor.GREEN + "!");

					if (target.isOnline()) {
						Utility.sendMessage((Player) target, ChatColor.GREEN + "Your rank has been set to "
								+ rankDisplayName + "!");
					}
				}
			}
		} else if (sender instanceof ConsoleCommandSender) {
			if (args.length != RankManager.getRankCommandArgumentLength()) {
				Utility.log("Incorrect command usage! Please use /rank <player> <rank name>");
				return true;
			}

			final String targetName = args[0];

			final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
			final String correctTargetName = target.getName();

			if (correctTargetName == null) {
				Utility.log("That player does not exist!");
			}

			final String rankName = args[1];
			final Rank rank = rankManager.getRank(rankName, false);

			if (rank == null) {
				Utility.log("That rank does not exist in the configuration file!");
				return true;
			}

			final UUID targetUUID = target.getUniqueId();
			final Rank previousRank = rankManager.getPlayerRank(targetUUID, false);

			final String previousDisplayName = previousRank.getDisplayName();
			final String previousRankDisplay = "".equals(previousDisplayName) ? "" : previousDisplayName + " ";

			final String rankDisplayName = rank.getDisplayName();

			if (previousRank.getName().equals(rank.getName())) {
				Utility.log(previousRankDisplay + correctTargetName + "'s rank is already set to "
						+ rankDisplayName + "!");
			} else {
				rankManager.setRank(targetUUID, rankName, false);

				Utility.log("Successfully set " + previousRankDisplay
						+ correctTargetName + "'s rank to "
						+ rankDisplayName + "!");

				if (target.isOnline()) {
					Utility.sendMessage((Player) target, ChatColor.GREEN + "Your rank has been set to "
							+ rankDisplayName + ChatColor.GREEN + "!");
				}
			}
		}

		return true;
	}

	@Override
	public @NotNull List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command,
			final @NotNull String label, final @NotNull String[] args) {
		final List<String> results = new ArrayList<>();

		if (!(sender instanceof Player) || !sender.isOp()) {
			return results;
		}

		if (args.length == RankManager.getPlayerInputArgumentNumber()) {
			for (final OfflinePlayer player : Bukkit.getOfflinePlayers()) {
				results.add(player.getName());
			}

			return StringUtil.copyPartialMatches(args[0], results, new ArrayList<>());
		} else if (args.length == RankManager.getRankInputArgumentNumber()) {
			for (final Rank rank : rankManager.getRanksList(false)) {
				results.add(rank.getName());
			}

			return StringUtil.copyPartialMatches(args[1], results, new ArrayList<>());
		}

		return results;
	}
}
