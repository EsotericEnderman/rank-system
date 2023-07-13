package net.slqmy.rank_system;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class EventListener implements Listener {
	private final YamlConfiguration configuration;
	private final Main plugin;

	public EventListener(YamlConfiguration configuration, Main plugin) {
		this.configuration = configuration;
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();



		if (!player.hasPlayedBefore()) {

		}
	}
}
