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

	private RankManager rankManager;
	public RankManager getRankManager() { return rankManager; }

	private NameTagManager nameTagManager;
	public NameTagManager getNameTagManager() { return nameTagManager; }

	private File playerRanksFile;
	public File getPlayerRanksFile() { return playerRanksFile; }

	private YamlConfiguration playerRanks;
	public YamlConfiguration getPlayerRanks() { return playerRanks; }

	@Override
	public void onEnable() {
		// Plugin startup logic.
		// Make sure the plugin folder exists.
		final File dataFolder = getDataFolder();

		final Logger logger = Logger.getLogger("Minecraft");

		if (!dataFolder.exists()) {
			final boolean creationSuccessful = dataFolder.mkdir();

			if (!creationSuccessful) {
				logger.info(Utility.getLogPrefix() + "Failed to create plugin directory! Cancelling plugin startup.");
				logger.info(Utility.getLogPrefix() + "The plugin needs access to the data folder to function properly.");
				logger.info(Utility.getLogPrefix() + "Make sure the server has the required permissions to create files and folders.");

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
			logger.info(Utility.getLogPrefix() + "Error while creating file 'player-ranks.yml'! Cancelling plugin startup.");
			logger.info(Utility.getLogPrefix() + exception.getMessage());

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
				logger.info(Utility.getLogPrefix() + "Invalid configuration! Duplicate rank entry for rank '" + rankName + "'!");
				logger.info(Utility.getLogPrefix() + "Cancelling plugin startup.");
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
