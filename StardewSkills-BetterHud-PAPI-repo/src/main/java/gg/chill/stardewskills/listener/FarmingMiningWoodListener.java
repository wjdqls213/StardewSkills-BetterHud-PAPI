package gg.chill.stardewskills.listener;

import gg.chill.stardewskills.service.SkillService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class FarmingMiningWoodListener implements Listener {
    private final SkillService service;
    public FarmingMiningWoodListener(gg.chill.stardewskills.StardewSkillsPlugin plugin, SkillService service) {
        this.service = service;
    }
    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        service.handleBlockBreak(e);
    }
}
