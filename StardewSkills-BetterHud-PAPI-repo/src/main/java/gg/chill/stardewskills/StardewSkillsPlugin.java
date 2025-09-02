package gg.chill.stardewskills;

import gg.chill.stardewskills.data.PlayerDataStore;
import gg.chill.stardewskills.listener.FarmingMiningWoodListener;
import gg.chill.stardewskills.listener.FishingListener;
import gg.chill.stardewskills.listener.GuiListener;
import gg.chill.stardewskills.papi.StardewPapi;
import gg.chill.stardewskills.service.SkillService;
import gg.chill.stardewskills.ui.SkillsCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class StardewSkillsPlugin extends JavaPlugin {

    private PlayerDataStore dataStore;
    private SkillService skillService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.dataStore = new PlayerDataStore(this);
        this.skillService = new SkillService(this, dataStore);

        Bukkit.getPluginManager().registerEvents(new FarmingMiningWoodListener(this, skillService), this);
        Bukkit.getPluginManager().registerEvents(new FishingListener(this, skillService), this);
        Bukkit.getPluginManager().registerEvents(new GuiListener(), this);

        PluginCommand cmd = getCommand("skills");
        if (cmd != null) {
            cmd.setExecutor(new SkillsCommand(this, skillService, dataStore));
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new StardewPapi(dataStore).register();
            getLogger().info("PlaceholderAPI detected. Registered StardewSkills placeholders.");
        } else {
            getLogger().warning("PlaceholderAPI not found. Placeholders will be unavailable.");
        }

        getLogger().info("StardewSkills (PAPI) enabled.");
    }

    @Override
    public void onDisable() {
        if (dataStore != null) dataStore.close();
        getLogger().info("StardewSkills disabled.");
    }
}
