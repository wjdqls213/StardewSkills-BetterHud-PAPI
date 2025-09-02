package gg.chill.stardewskills.listener;

import gg.chill.stardewskills.service.SkillService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingListener implements Listener {
    private final SkillService service;
    public FishingListener(gg.chill.stardewskills.StardewSkillsPlugin plugin, SkillService service) {
        this.service = service;
    }
    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent e) {
        service.handleFish(e);
    }
}
