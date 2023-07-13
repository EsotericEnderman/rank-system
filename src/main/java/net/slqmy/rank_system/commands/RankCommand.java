package net.slqmy.rank_system.commands;

import net.slqmy.rank_system.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class RankCommand implements CommandExecutor {
	private final YamlConfiguration configuration;
	private final Main plugin;

	public RankCommand(final YamlConfiguration configuration, final Main plugin) {
		this.configuration = configuration;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
		} else if (sender instanceof ConsoleCommandSender) {

		}

		return true;
	}
}
