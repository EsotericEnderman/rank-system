package net.slqmy.rank_system.commands;

import net.slqmy.rank_system.Main;
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

public final class RankCommand implements CommandExecutor {
	// rank <player> <rank name>

	private final Main plugin;

	public RankCommand(final Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (player.isOp()) {
				if (args.length != 2) {
					return false;
				} else {
					final String targetName = args[0];

					final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
					final UUID targetUUID = target.getUniqueId();
					final OfflinePlayer realTarget = Bukkit.getOfflinePlayer(targetUUID);

					if (realTarget != null) {
						final String rank = args[1];
						boolean success =	plugin.getRankManager().setRank(target.getUniqueId(), rank, false);

						if (success) {
							player.sendMessage(ChatColor.GREEN + "Successfully set " + target.getName() + "'s rank to " + rank + "!");
						} else {
							player.sendMessage(ChatColor.RED + "That rank does not exist in the configuration file!");
						}
					} else {
						player.sendMessage(ChatColor.RED + "That player does not exist!");
					}
				}
			} else {
				player.sendMessage(ChatColor.RED + "You must be op to execute this command!");
			}
		} else if (sender instanceof ConsoleCommandSender) {
			if (args.length != 2) {
				System.out.println("[Rank-System] Incorrect command usage! Please use /rank <player> <rank name>");
			} else {
				final String targetName = args[0];

				final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
				final UUID targetUUID = target.getUniqueId();
				final OfflinePlayer realTarget = Bukkit.getOfflinePlayer(targetUUID);

				if (realTarget != null) {
					final String rank = args[1];
					boolean success =	plugin.getRankManager().setRank(target.getUniqueId(), rank, false);

					if (success) {
						System.out.println("[Rank-System] Successfully set " + target.getName() + "'s rank to " + rank + "!");
					} else {
						System.out.println("[Rank-System] That rank does not exist in the configuration file!");
					}
				} else {
					System.out.println("[Rank-System] That player does not exist!");
				}
			}
		}

		return true;
	}
}
