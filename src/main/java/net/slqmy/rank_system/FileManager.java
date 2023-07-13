package net.slqmy.rank_system;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class FileManager {

	public static YamlConfiguration initiateYMLFile(String fileName, final Main plugin) {
		fileName += ".yml";

		System.out.println("Attempting to create new file: '" + fileName + "'.");

		final File file = new File(plugin.getDataFolder(), fileName);

		try {
			final boolean fileAlreadyExists = file.createNewFile();

			if (fileAlreadyExists) {
				System.out.println("File creation cancelled, file '" + fileName + "' already exists!");
			}
		} catch (final IOException exception) {
			System.out.println("Couldn't create file '" + fileName + "' due to an error:");

			System.out.println(exception.getMessage());
			exception.printStackTrace();
			return null;
		}

		return YamlConfiguration.loadConfiguration(file);
	}
}
