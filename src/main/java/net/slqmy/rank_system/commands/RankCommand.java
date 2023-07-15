package net.slqmy.rank_system.commands;

import net.slqmy.rank_system.Main;
import net.slqmy.rank_system.Rank;
import net.slqmy.rank_system.managers.RankManager;
import net.slqmy.rank_system.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Logger;

public final class RankCommand implements CommandExecutor {
	// rank <player> <rank name>

	private final RankManager rankManager;

	public RankCommand(final Main plugin) { this.rankManager = plugin.getRankManager(); }

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;

			if (player.isOp()) {
				if (args.length != 2) {
					return false;
				} else {
					final String targetName = args[0];

					final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
					final UUID targetUUID = target.getUniqueId();
					final OfflinePlayer realTarget = Bukkit.getOfflinePlayer(targetUUID);

					if (realTarget != null) {
						final String rankName = args[1];
						final Rank rank = rankManager.getRank(rankName);
						final UUID realTargetUUID = realTarget.getUniqueId();
						final boolean success =	rankManager.setRank(realTargetUUID, rankName, false);

						if (success) {
							String rankDisplayName = rank.getDisplayName();

							if (player.getUniqueId() == realTargetUUID) {
								player.sendMessage(Utility.getChatPrefix() + ChatColor.GREEN + "Successfully set your rank to " + rankDisplayName + ChatColor.GREEN + "!");
							} else {
								player.sendMessage(Utility.getChatPrefix() + ChatColor.GREEN + "Successfully set " + ChatColor.BOLD + realTarget.getName() + ChatColor.RESET + ChatColor.GREEN + "'s rank to " + rankDisplayName + ChatColor.GREEN + "!");

								if (target.isOnline()) {
									((Player) realTarget).sendMessage(Utility.getChatPrefix() + ChatColor.GREEN + "Your rank has been set to " + rankDisplayName + ChatColor.GREEN + "!");
								}
							}
						} else {
							player.sendMessage(Utility.getChatPrefix() + ChatColor.RED + "That rank does not exist in the configuration file!");
						}
					} else {
						player.sendMessage(Utility.getChatPrefix() + ChatColor.RED + "That player does not exist!");
					}
				}
			} else {
				player.sendMessage(Utility.getChatPrefix() + ChatColor.RED + "You must be op to execute this command!");
			}
		} else if (sender instanceof ConsoleCommandSender) {
			Logger logger = Logger.getLogger("Minecraft");

			if (args.length != 2) {
				logger.info(Utility.getLogPrefix() + "Incorrect command usage! Please use /rank <player> <rank name>");
			} else {
				final String targetName = args[0];

				final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
				final UUID targetUUID = target.getUniqueId();
				final OfflinePlayer realTarget = Bukkit.getOfflinePlayer(targetUUID);

				if (realTarget != null) {
					final String rank = args[1];
					boolean success =	rankManager.setRank(target.getUniqueId(), rank, false);

					if (success) {
						logger.info(Utility.getLogPrefix() + "Successfully set " + target.getName() + "'s rank to " + rank + "!");
					} else {
						logger.info(Utility.getLogPrefix() + "That rank does not exist in the configuration file!");
					}
				} else {
					logger.info(Utility.getLogPrefix() + "That player does not exist!");
				}
			}
		}

		return true;
	}
}
