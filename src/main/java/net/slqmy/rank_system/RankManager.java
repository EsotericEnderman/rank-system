package net.slqmy.rank_system;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

public final class RankManager {
	private final YamlConfiguration playerRanks;
	private final Main plugin;

	public RankManager(YamlConfiguration playerRanks, Main plugin) {
		this.playerRanks = playerRanks;
		this.plugin = plugin;
	}

	public void setRank(UUID uuid, String rankName) {
		playerRanks.set(uuid.toString(), rankName);
	}

	public String getRankName(UUID uuid) {
		return playerRanks.getString(uuid.toString());
	}
}
