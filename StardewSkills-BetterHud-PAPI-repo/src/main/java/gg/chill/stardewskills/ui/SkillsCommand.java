package gg.chill.stardewskills.ui;

import gg.chill.stardewskills.data.PlayerDataStore;
import gg.chill.stardewskills.service.SkillService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SkillsCommand implements CommandExecutor {
    private final gg.chill.stardewskills.StardewSkillsPlugin plugin;
    private final SkillService service;
    private final PlayerDataStore store;
    public SkillsCommand(gg.chill.stardewskills.StardewSkillsPlugin plugin, SkillService service, PlayerDataStore store) {
        this.plugin = plugin; this.service = service; this.store = store;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Player only."); return true; }
        Inventory inv = GuiRenderer.buildGui(plugin, store, p.getUniqueId());
        p.openInventory(inv);
        return true;
    }
}
