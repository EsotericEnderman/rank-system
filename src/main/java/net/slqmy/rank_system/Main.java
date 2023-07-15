package net.slqmy.rank_system;

import net.slqmy.rank_system.commands.Rank;
import net.slqmy.rank_system.events.EventListener;
import net.slqmy.rank_system.managers.NameTags;
import net.slqmy.rank_system.managers.Ranks;
import net.slqmy.rank_system.tab_completers.RankCommandTabCompleter;
import net.slqmy.rank_system.utility.types.Pair;
import net.slqmy.rank_system.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

	/*
	 * /rank command. /rank <player> <rank name>
	 * Name tags & chat display.
	 * Save in .yml file.
	 * Custom permissions.
	 */

	private static final Logger LOGGER = Logger.getLogger("Minecraft");

	private Ranks ranks;
	private NameTags nameTags;
	private File playerRanksFile;
	private YamlConfiguration playerRanks;

	public static Logger getPluginLogger() {
		return LOGGER;
	}

	public Ranks getRankManager() {
		return ranks;
	}

	public NameTags getNameTagManager() {
		return nameTags;
	}

	public File getPlayerRanksFile() {
		return playerRanksFile;
	}

	public YamlConfiguration getPlayerRanks() {
		return playerRanks;
	}

	@Override
	public void onEnable() {
		// Plugin startup logic.
		// Make sure the plugin folder exists.
		final File dataFolder = getDataFolder();
		final String LOG_PREFIX = Utility.getLogPrefix();

		if (!dataFolder.exists()) {
			final boolean creationSuccessful = dataFolder.mkdir();

			if (!creationSuccessful) {
				final String message = LOG_PREFIX + "Failed to create plugin directory! Cancelling plugin startup.\n"
						+ LOG_PREFIX + "The plugin needs access to the data folder to function properly.\n"
						+ LOG_PREFIX + "Make sure the server has the needed permissions to modify files.";

				LOGGER.info(message);

				return;
			}
		}

		// Make sure the plugin config exists. If there is no config, a new file is
		// generated.
		final FileConfiguration config = getConfig();

		config.options().copyDefaults();
		saveDefaultConfig();

		// Initiate player-ranks file if it does not exist yet.
		final Pair<File, YamlConfiguration> playerRanksTuple;

		try {
			playerRanksTuple = Utility.initiateYAMLFile("player-ranks", this);
		} catch (final IOException exception) {
			LOGGER.info(LOG_PREFIX + "Error while creating file 'player-ranks.yml'! Cancelling plugin startup.");
			LOGGER.info(LOG_PREFIX + exception.getMessage());

			exception.printStackTrace();
			return;
		}

		playerRanksFile = playerRanksTuple.first;
		playerRanks = playerRanksTuple.second;

		// The ranks class needs playerRanks to be assigned first, or else an error will
		// occur.
		ranks = new Ranks(this);

		// Check for duplicate rank names.
		final List<net.slqmy.rank_system.types.Rank> ranksList = this.ranks.getRanksList();
		final List<String> rankNames = new ArrayList<>();

		for (final net.slqmy.rank_system.types.Rank rank : ranksList) {
			final String rankName = rank.getName();

			if (rankNames.contains(rankName)) {
				final String message = LOG_PREFIX + "Invalid configuration! Duplicate rank entry for rank '" + rankName + "'!\n"
						+ LOG_PREFIX + "Cancelling plugin startup.";

				LOGGER.info(message);
				return;
			}

			rankNames.add(rankName);
		}

		// And the name tag manager needs the rank manager to be assigned.
		nameTags = new NameTags(this);

		// Initiate rank command and event listener.
		final PluginCommand rank = getCommand("rank");
		assert rank != null;
		rank.setExecutor(new Rank(this));
		rank.setTabCompleter(new RankCommandTabCompleter(this));

		Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
	}
}
