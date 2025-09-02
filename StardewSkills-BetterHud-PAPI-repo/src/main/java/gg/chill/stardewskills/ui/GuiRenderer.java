package gg.chill.stardewskills.ui;

import gg.chill.stardewskills.data.PlayerDataStore;
import gg.chill.stardewskills.model.Skill;
import gg.chill.stardewskills.service.SkillService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuiRenderer {
    public static String GUI_TITLE_CACHE = ChatColor.translateAlternateColorCodes('&', "&a&lStardewSkills");
    public static Inventory buildGui(gg.chill.stardewskills.StardewSkillsPlugin plugin,
                                     PlayerDataStore store, UUID uuid) {
        FileConfiguration cfg = plugin.getConfig();
        String title = cfg.getString("gui.title", "&a&lStardewSkills &7(힐링 서버)");
        GUI_TITLE_CACHE = ChatColor.translateAlternateColorCodes('&', title);
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE_CACHE);

        Material filler = Material.matchMaterial(cfg.getString("gui.filler", "GRAY_STAINED_GLASS_PANE"));
        if (filler == null) filler = Material.GRAY_STAINED_GLASS_PANE;
        ItemStack fill = new ItemStack(filler);
        ItemMeta fm = fill.getItemMeta();
        if (fm != null) { fm.setDisplayName(" "); fill.setItemMeta(fm); }
        for (int i=0;i<27;i++) inv.setItem(i, fill);

        int[] slots = {10, 12, 14, 16};
        Skill[] skills = {Skill.FARMING, Skill.FISHING, Skill.WOODCUTTING, Skill.MINING};

        for (int i=0;i<skills.length;i++) {
            Skill s = skills[i];
            int xp = store.getXP(uuid, s);
            int lvl = SkillService.levelFromTotalXP(xp);
            int prev = SkillService.totalXPForLevel(lvl);
            int next = SkillService.totalXPForLevel(lvl+1);
            int cur = xp - prev;
            int need = next - prev;
            double pct = need <= 0 ? 1.0 : Math.min(1.0, cur / (double)need);
            String bar = progressBar(pct, 20);

            String def = switch (s) {
                case FARMING -> "WHEAT";
                case FISHING -> "FISHING_ROD";
                case WOODCUTTING -> "OAK_LOG";
                case MINING -> "IRON_PICKAXE";
            };
            Material mat = Material.matchMaterial(cfg.getString("gui.icons."+s.name(), def));
            if (mat == null) mat = Material.BOOK;

            ItemStack it = new ItemStack(mat);
            ItemMeta im = it.getItemMeta();
            if (im != null) {
                im.setDisplayName(ChatColor.GOLD + s.getKoreanName() + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Lv." + lvl);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "총 XP: " + ChatColor.WHITE + xp + ChatColor.GRAY + " (다음 레벨까지 " + Math.max(0, next - xp) + " XP)");
                lore.add(ChatColor.DARK_GREEN + bar + ChatColor.GRAY + String.format("  %.0f%%", pct*100));
                im.setLore(lore);
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                it.setItemMeta(im);
            }
            inv.setItem(slots[i], it);
        }
        return inv;
    }
    private static String progressBar(double pct, int len) {
        int filled = (int)Math.round(len * pct);
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<len;i++) sb.append(i < filled ? ChatColor.GREEN + "|" : ChatColor.DARK_GRAY + "|");
        return sb.toString();
    }
}
