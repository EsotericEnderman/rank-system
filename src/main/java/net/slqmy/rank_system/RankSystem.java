package net.slqmy.rank_system;

import net.slqmy.rank_system.commands.RankCommand;
import net.slqmy.rank_system.events.listeners.AsyncPlayerChatEventListener;
import net.slqmy.rank_system.events.listeners.PlayerJoinEventListener;
import net.slqmy.rank_system.events.listeners.PlayerQuitEventListener;
import net.slqmy.rank_system.managers.NameTagManager;
import net.slqmy.rank_system.managers.RankManager;
import net.slqmy.rank_system.types.Rank;
import net.slqmy.rank_system.utility.Utility;
import net.slqmy.rank_system.utility.types.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class RankSystem extends JavaPlugin {

	/*
	 * /rank command. /rank <player> <rank name>
	 * Name tags & chat display.
	 * Save in .yml file.
	 * Custom permissions.
	 */

	private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();

	private RankManager rankManager;
	private NameTagManager nameTagManager;
	private File playerRanksFile;
	private YamlConfiguration playerRanksConfig;

	public RankManager getRankManager() {
		return rankManager;
	}

	public NameTagManager getNameTagManager() {
		return nameTagManager;
	}

	public File getPlayerRanksFile() {
		return playerRanksFile;
	}

	public YamlConfiguration getPlayerRanksConfig() {
		return playerRanksConfig;
	}

	@Override
	public void onEnable() {
		// Plugin startup logic.
		// Make sure the plugin folder exists.
		final File dataFolder = getDataFolder();

		if (!dataFolder.exists()) {
			final boolean creationSuccessful = dataFolder.mkdir();

			if (!creationSuccessful) {
				Utility.log("Failed to create plugin directory! Cancelled plugin startup.");
				Utility.log("The plugin needs access to the data folder to function properly.");
				Utility.log("Make sure the server has the needed permissions to modify files.");

				return;
			}
		}

		// Make sure the plugin config exists. If there is no config, a new file is
		// generated.
		final FileConfiguration config = getConfig();

		config.options().copyDefaults();
		saveDefaultConfig();

		// Initiate player-rankManager file if it does not exist yet.
		final Pair<File, YamlConfiguration> playerRanksTuple;

		try {
			playerRanksTuple = Utility.initiateYAMLFile("data/player-ranks", this);

			if (playerRanksTuple == null) {
				Utility.log("Error creating file 'player-ranks.yml' Cancelled plugin startup!");
				return;
			}
		} catch (final IOException exception) {
			Utility.log("Error creating file 'player-ranks.yml'! Cancelled plugin startup.");
			Utility.log(exception.getMessage());
			exception.printStackTrace();
			Utility.log(exception);
			return;
		}

		playerRanksFile = playerRanksTuple.first;
		playerRanksConfig = playerRanksTuple.second;

		// The rankManager class needs playerRanks to be assigned first, or else an
		// error will occur.
		rankManager = new RankManager(this);

		// Check for duplicate rank names.
		final List<Rank> ranksList = rankManager.getRanksList(false);
		final List<String> rankNames = new ArrayList<>();

		for (final Rank rank : ranksList) {
			final String rankName = rank.getName();

			if (rankNames.contains(rankName)) {
				Utility.log("Invalid configuration! Duplicate rank entry for rank '" + rankName + "'!");
				Utility.log("Cancelled plugin startup.");

				return;
			}

			rankNames.add(rankName);
		}

		final String defaultRankName = config.getString("default-rank");
		final Rank defaultRank = rankManager.getDefaultRank(false);

		if (defaultRankName != null && defaultRank == null) {
			Utility.log("Invalid configuration! Default rank not found in rank list.");
			Utility.log("Cancelled plugin startup!");

			return;
		}

		// And the name tag manager needs the rank manager to be assigned.
		nameTagManager = new NameTagManager(this);

		// Initiate rank command and event listener.
		final PluginCommand rank = getCommand("rank");
		assert rank != null;
		rank.setExecutor(new RankCommand(this));
		rank.setTabCompleter(new RankCommand(this));

		PLUGIN_MANAGER.registerEvents(new AsyncPlayerChatEventListener(this), this);
		PLUGIN_MANAGER.registerEvents(new PlayerJoinEventListener(this), this);
		PLUGIN_MANAGER.registerEvents(new PlayerQuitEventListener(this), this);

		for (final Player player : Bukkit.getOnlinePlayers()) {
			nameTagManager.setNameTags(player);
			nameTagManager.addNewNameTag(player);
		}
	}
}
