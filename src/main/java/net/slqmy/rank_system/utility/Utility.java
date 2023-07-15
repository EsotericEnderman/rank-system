package net.slqmy.rank_system.utility;

import net.slqmy.rank_system.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class Utility {
	private static final Logger LOGGER = Main.getPluginLogger();
	private static final String LOG_PREFIX = "[Rank-System] ";
	private static final String CHAT_PREFIX = "[" + ChatColor.BOLD + ChatColor.GREEN + "Rank-System" + ChatColor.RESET
			+ "] ";

	public static String getLogPrefix() {
		return LOG_PREFIX;
	}

	public static String getChatPrefix() {
		return CHAT_PREFIX;
	}

	@Contract("_, _ -> new")
	public static @NotNull Pair<File, YamlConfiguration> initiateYAMLFile(String name, final @NotNull Main plugin)
			throws IOException {
		name += ".yml";

		final File file = new File(plugin.getDataFolder(), name);

		final String message = LOG_PREFIX + "Attempting to create file " + name + ".";
		LOGGER.info(message);

		final boolean fileAlreadyExists = !file.createNewFile();

		if (fileAlreadyExists) {
			LOGGER.info(LOG_PREFIX + "File already exists.");
		}

		return new Pair<>(file, YamlConfiguration.loadConfiguration(file));
	}
}
