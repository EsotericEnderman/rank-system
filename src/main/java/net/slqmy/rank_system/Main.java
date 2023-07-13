package net.slqmy.rank_system;

import net.slqmy.rank_system.commands.RankCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {
	private final YamlConfiguration configuration = FileManager.initiateYMLFile("configuration", this);
	public YamlConfiguration getConfiguration() { return configuration; }

	private final YamlConfiguration playerRanksFile = FileManager.initiateYMLFile("player-ranks", this);
	public YamlConfiguration getPlayerRanksFile() { return playerRanksFile; }

	private RankManager rankManager;
	public RankManager getRankManager() { return rankManager; }

	@Override
	public void onEnable() {
		// Plugin startup logic.

		rankManager = new RankManager(playerRanksFile, this);

		// Make sure that the plugin folder exists.
		final File pluginDataFolder = getDataFolder();

		if (!pluginDataFolder.exists()) {
			final boolean folderCreationSuccessful = pluginDataFolder.mkdir();

			if (!folderCreationSuccessful) {
				System.out.println("Failed to create plugin directory!");
				return;
			}
		}

		Bukkit.getPluginManager().registerEvents(new EventListener(configuration, this), this);

		final PluginCommand rankCommand = getCommand("rank");
		assert rankCommand != null;
		rankCommand.setExecutor(new RankCommand(configuration, this));
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic.
	}
}
