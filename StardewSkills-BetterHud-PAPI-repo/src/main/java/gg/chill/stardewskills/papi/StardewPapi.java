package gg.chill.stardewskills.papi;

import gg.chill.stardewskills.data.PlayerDataStore;
import gg.chill.stardewskills.model.Skill;
import gg.chill.stardewskills.service.SkillService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StardewPapi extends PlaceholderExpansion {
  private final PlayerDataStore store;

  public StardewPapi(PlayerDataStore store) { this.store = store; }

  @Override public @NotNull String getIdentifier() { return "stardewskills"; }
  @Override public @NotNull String getAuthor() { return "chillgpt"; }
  @Override public @NotNull String getVersion() { return "0.3.0"; }

  @Override
  public String onPlaceholderRequest(Player p, @NotNull String params) {
    if (p == null) return "";
    try {
      return switch (params.toLowerCase()) {
        case "level_farming" -> String.valueOf(SkillService.levelFromTotalXP(store.getXP(p.getUniqueId(), Skill.FARMING)));
        case "level_fishing" -> String.valueOf(SkillService.levelFromTotalXP(store.getXP(p.getUniqueId(), Skill.FISHING)));
        case "level_woodcutting" -> String.valueOf(SkillService.levelFromTotalXP(store.getXP(p.getUniqueId(), Skill.WOODCUTTING)));
        case "level_mining" -> String.valueOf(SkillService.levelFromTotalXP(store.getXP(p.getUniqueId(), Skill.MINING)));
        case "progress_farming_pct" -> progressPct(p, Skill.FARMING);
        case "progress_fishing_pct" -> progressPct(p, Skill.FISHING);
        case "progress_woodcutting_pct" -> progressPct(p, Skill.WOODCUTTING);
        case "progress_mining_pct" -> progressPct(p, Skill.MINING);
        default -> "";
      };
    } catch (Exception ignored) {}
    return "";
  }

  private String progressPct(Player p, Skill s) {
    int xp = store.getXP(p.getUniqueId(), s);
    int lv = SkillService.levelFromTotalXP(xp);
    int cur = xp - SkillService.totalXPForLevel(lv);
    int need = SkillService.totalXPForLevel(lv+1) - SkillService.totalXPForLevel(lv);
    int pct = (need <= 0) ? 100 : (int)Math.round(100.0 * cur / need);
    return String.valueOf(Math.max(0, Math.min(100, pct)));
  }
}
