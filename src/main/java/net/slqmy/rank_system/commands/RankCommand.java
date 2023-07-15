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

	private static final Logger LOGGER = Main.getPluginLogger();

	private final RankManager rankManager;

	private final String chatPrefix = Utility.getChatPrefix();
	private final String logPrefix = Utility.getLogPrefix();

	public RankCommand(final @NotNull Main plugin) {
		this.rankManager = plugin.getRankManager();
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
			@NotNull final String label, @NotNull final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;

			if (player.isOp()) {
				if (args.length != RankManager.getRankCommandArgumentLength()) {
					return false;
				} else {
					final String targetName = args[0];

					final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

					if (target.getName() != null) {
						final String rankName = args[1];
						final Rank rank = rankManager.getRank(rankName);
						final UUID targetUUID = target.getUniqueId();
						final boolean success = rankManager.setRank(targetUUID, rankName, false);

						if (success) {
							String rankDisplayName = rank.getDisplayName();

							if (player.getUniqueId() == targetUUID) {
								player.sendMessage(chatPrefix + ChatColor.GREEN + "Successfully set your rank to " + rankDisplayName
										+ ChatColor.GREEN + "!");
							} else {
								player.sendMessage(chatPrefix + ChatColor.GREEN + "Successfully set " + ChatColor.BOLD
										+ target.getName() + ChatColor.RESET + ChatColor.GREEN + "'s rank to " + ChatColor.RESET
										+ rankDisplayName + ChatColor.GREEN + "!");

								if (target.isOnline()) {
									((Player) target).sendMessage(chatPrefix + ChatColor.GREEN + "Your rank has been set to "
											+ rankDisplayName + ChatColor.GREEN + "!");
								}
							}
						} else {
							player.sendMessage(chatPrefix + ChatColor.RED + "That rank does not exist in the configuration file!");
						}
					} else {
						player.sendMessage(chatPrefix + ChatColor.RED + "That player does not exist!");
					}
				}
			} else {
				player.sendMessage(chatPrefix + ChatColor.RED + "You must be op to execute this command!");
			}
		} else if (sender instanceof ConsoleCommandSender) {
			if (args.length != RankManager.getRankCommandArgumentLength()) {
				final String message = logPrefix + "Incorrect command usage! Please use /rank <player> <rank name>";

				LOGGER.info(message);
			} else {
				final String targetName = args[0];

				final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

				if (target.getName() != null) {
					final String rank = args[1];
					boolean success = rankManager.setRank(target.getUniqueId(), rank, false);

					final String message;

					if (success) {
						message = logPrefix + "Successfully set " + target.getName() + "'s rank to " + rank + "!";
					} else {
						message = logPrefix + "That rank does not exist in the configuration file!";
					}

					LOGGER.info(message);
				} else {
					final String message = logPrefix + "That player does not exist!";

					LOGGER.info(message);
				}
			}
		}

		return true;
	}
}
