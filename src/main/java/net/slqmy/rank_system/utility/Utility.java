package net.slqmy.rank_system.utility;

import net.slqmy.rank_system.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class Utility {
	public static Pair<File, YamlConfiguration> initiateYAMLFile(String name, final Main plugin) throws IOException {
		name += ".yml";

		final File file = new File(plugin.getDataFolder(), name);

		System.out.println("[Rank-System] Attempting to create file " + name + ".");

		final boolean fileAlreadyExists = !file.createNewFile();

		if (fileAlreadyExists) {
			System.out.println("[Rank-System] File already exists.");
		}

		return new Pair<>(file, YamlConfiguration.loadConfiguration(file));
	}
}
