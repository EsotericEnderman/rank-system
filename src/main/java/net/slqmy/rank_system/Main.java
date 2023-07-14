package net.slqmy.rank_system;

import net.slqmy.rank_system.autocompleters.RankCommandTabCompleter;
import net.slqmy.rank_system.commands.RankCommand;
import net.slqmy.rank_system.managers.NameTagManager;
import net.slqmy.rank_system.managers.RankManager;
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
import java.util.LinkedHashMap;
import java.util.List;

public final class Main extends JavaPlugin {
	/*
	 /rank command. /rank <player> <rank name>
	 Name tags & chat display.
	 Save in .yml file.
	 Custom permissions.
	*/

	private final RankManager rankManager = new RankManager(this);
	public RankManager getRankManager() { return rankManager; }

	private final NameTagManager nameTagManager = new NameTagManager(this);
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

		if (!dataFolder.exists()) {
			final boolean creationSuccessful = dataFolder.mkdir();

			if (!creationSuccessful) {
				System.out.println("[Rank-System] Failed to create plugin directory! Cancelling plugin startup.");
				System.out.println("[Rank-System] Make sure the server has the required permissions to create files and folders.");

				return;
			}
		}

		// Make sure the plugin config exists. If there is no config, a new file is generated.
		final FileConfiguration config = getConfig();

		config.options().copyDefaults();
		saveDefaultConfig();

		// Check for duplicate rank names.
		final List<LinkedHashMap<String, Object>> ranks = (List<LinkedHashMap<String, Object>>) config.getList("ranks");
		final List<String> rankNames = new ArrayList<>();

		assert ranks != null;
		for (LinkedHashMap<String, Object> rank : ranks) {
			Rank currentRank = Rank.from(rank);

			String rankName = currentRank.getName();

			if (rankNames.contains(rankName)) {
				System.out.println("[Rank-System] Invalid configuration! Duplicate rank entry for rank '" + rankName + "'!");
				System.out.println("[Rank-System] Cancelling plugin startup.");
				return;
			}

			rankNames.add(currentRank.getName());
		}

		// Initiate player-ranks file if it does not exist yet.
		final Pair<File, YamlConfiguration> playerRanksTuple;

		try {
			playerRanksTuple =	Utility.initiateYAMLFile("player-ranks", this);
		} catch (final IOException exception) {
			System.out.println("[Rank-System] Error while creating file 'player-ranks.yml'! Cancelling plugin startup.");
			System.out.println(exception.getMessage());

			exception.printStackTrace();
			return;
		}

		playerRanksFile = playerRanksTuple.first;
		playerRanks = playerRanksTuple.second;

		// Initiate rank command and event listener.
		final PluginCommand rank = getCommand("rank");
		assert rank != null;
		rank.setExecutor(new RankCommand(this));
		rank.setTabCompleter(new RankCommandTabCompleter());

		Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
	}
}
