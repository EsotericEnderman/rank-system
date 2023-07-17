package net.slqmy.rank_system.utility;

import net.slqmy.rank_system.RankSystem;
import net.slqmy.rank_system.utility.types.Pair;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utility {
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private static final String LOG_PREFIX = "[Rank-System]";
	private static final String CHAT_PREFIX = "[" + ChatColor.BOLD + ChatColor.GREEN + "Rank-System" + ChatColor.RESET
			+ "]";
	private static final Pattern DIRECTORY_PATTERN = Pattern.compile(".+(?=/.+\\..+$)");
	private static final Pattern FORMAT_PATTERN = Pattern.compile(ChatColor.COLOR_CHAR + "[0-9a-fk-or]");

	public static @Nullable Pair<@NotNull File, @NotNull YamlConfiguration> initiateYAMLFile(@NotNull String name,
			final @NotNull RankSystem plugin)
			throws IOException {
		File pluginDataFolder = plugin.getDataFolder();
		name += ".yml";

		log("Attempting to create file '" + name + "'...");

		Matcher matcher = DIRECTORY_PATTERN.matcher(name);

		if (matcher.find()) {
			String directoryPath = matcher.group(0);

			log("Attempting to create directory '" + directoryPath + "'...");

			File directory = new File(pluginDataFolder, directoryPath);

			if (!directory.exists()) {
				boolean success = directory.mkdirs();

				if (success) {
					log("Successfully created directory.");
				} else {
					log("Failed to create directory!");
					return null;
				}
			} else {
				log("Directory already exists.");
			}
		}

		log("Creating file...");

		final File file = new File(pluginDataFolder, name);

		final boolean fileCreated = file.createNewFile();

		if (fileCreated) {
			log("File successfully created!");
		} else {
			log("File already exists.");
		}

		return new Pair<>(file, YamlConfiguration.loadConfiguration(file));
	}

	public static void sendMessage(@NotNull Player player, @NotNull String message) {
		player.sendMessage(CHAT_PREFIX + " " + message);
	}

	public static void log(@NotNull String message) {
		message = clearFormatting(LOG_PREFIX + " " + message);

		LOGGER.info(message);
	}

	@NotNull
	public static String replaceAll(@NotNull final String input, @NotNull final Pattern pattern,
			@NotNull final String replaceString) {
		Matcher matcher = pattern.matcher(input);
		return matcher.replaceAll(replaceString);
	}

	@NotNull
	public static String clearFormatting(@NotNull final String input) {
		return replaceAll(input, FORMAT_PATTERN, "");
	}
}
