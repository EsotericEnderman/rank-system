package net.slqmy.rank_system;

import net.slqmy.rank_system.commands.RankCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic.

        PluginCommand rank = getCommand("rank");
        assert rank != null;
        rank.setExecutor(new RankCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic.
    }
}
