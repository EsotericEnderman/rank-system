package net.slqmy.rank_system;

import net.slqmy.rank_system.commands.RankCommand;
import net.slqmy.rank_system.managers.NameTagManager;
import net.slqmy.rank_system.managers.RankManager;
import net.slqmy.rank_system.tab_completers.RankCommandTabCompleter;
import net.slqmy.rank_system.utility.Pair;
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
	 /rank command. /rank <player> <rank name>
	 Name tags & chat display.
	 Save in .yml file.
	 Custom permissions.
	*/

	private static final Logger LOGGER = Logger.getLogger("Minecraft");

	private RankManager rankManager;
	private NameTagManager nameTagManager;
	private File playerRanksFile;
	private YamlConfiguration playerRanks;

	public static Logger getPluginLogger() { return LOGGER; }

	public RankManager getRankManager() { return rankManager; }
	public NameTagManager getNameTagManager() { return nameTagManager; }
	public File getPlayerRanksFile() { return playerRanksFile; }
	public YamlConfiguration getPlayerRanks() { return playerRanks; }

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

		// Make sure the plugin config exists. If there is no config, a new file is generated.
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

		// The rankManager needs playerRanks to be assigned first, or else an error will occur.
		rankManager = new RankManager(this);

		// Check for duplicate rank names.
		final List<Rank> ranks = rankManager.getRanksList();
		final List<String> rankNames = new ArrayList<>();

		assert ranks != null;
		for (final Rank rank : ranks) {
			final	String rankName = rank.getName();

			if (rankNames.contains(rankName)) {
				final String message = LOG_PREFIX + "Invalid configuration! Duplicate rank entry for rank '" + rankName + "'!\n"
						+ LOG_PREFIX + "Cancelling plugin startup.";

				LOGGER.info(message);
				return;
			}

			rankNames.add(rankName);
		}

		// And the name tag manager needs the rank manager to be assigned.
		nameTagManager = new NameTagManager(this);

		// Initiate rank command and event listener.
		final PluginCommand rank = getCommand("rank");
		assert rank != null;
		rank.setExecutor(new RankCommand(this));
		rank.setTabCompleter(new RankCommandTabCompleter(this));

		Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
	}
}
