package gg.chill.stardewskills.listener;

import gg.chill.stardewskills.ui.GuiRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GuiListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if (inv != null && e.getView().getTitle().equals(GuiRenderer.GUI_TITLE_CACHE)) {
            e.setCancelled(true);
        }
    }
}
