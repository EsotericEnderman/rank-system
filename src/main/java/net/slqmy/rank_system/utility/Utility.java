package net.slqmy.rank_system.utility;

import net.slqmy.rank_system.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class Utility {
	private static final String logPrefix = "[Rank-System] ";
	public static String getLogPrefix() { return logPrefix; }

	private static final String chatPrefix = "[" + ChatColor.BOLD + ChatColor.GREEN + "Rank-System" + ChatColor.RESET + "] ";
	public static String getChatPrefix() { return chatPrefix; }

	public static Pair<File, YamlConfiguration> initiateYAMLFile(String name, final Main plugin) throws IOException {
		name += ".yml";

		final Logger logger = Logger.getLogger("Minecraft");

		final File file = new File(plugin.getDataFolder(), name);

		logger.info(getLogPrefix() + "Attempting to create file " + name + ".");

		final boolean fileAlreadyExists = !file.createNewFile();

		if (fileAlreadyExists) {
			logger.info(getLogPrefix() + "File already exists.");
		}

		return new Pair<>(file, YamlConfiguration.loadConfiguration(file));
	}
}
