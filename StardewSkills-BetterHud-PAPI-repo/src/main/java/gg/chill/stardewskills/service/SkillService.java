package gg.chill.stardewskills.service;

import gg.chill.stardewskills.StardewSkillsPlugin;
import gg.chill.stardewskills.data.PlayerDataStore;
import gg.chill.stardewskills.model.Skill;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class SkillService {
    private final StardewSkillsPlugin plugin;
    private final PlayerDataStore store;
    private final FileConfiguration cfg;

    private final Map<Material, Integer> farmingXp = new EnumMap<>(Material.class);
    private final Map<Material, Integer> logXp = new EnumMap<>(Material.class);
    private final Map<Material, Integer> oreXp = new EnumMap<>(Material.class);
    private final int baseFarm;
    private final int baseFish;
    private final int baseWood;
    private final int baseMine;

    public SkillService(StardewSkillsPlugin plugin, PlayerDataStore store) {
        this.plugin = plugin;
        this.store = store;
        this.cfg = plugin.getConfig();

        this.baseFarm = cfg.getInt("xp.farming.base", 5);
        this.baseFish = cfg.getInt("xp.fishing.base", 10);
        this.baseWood = cfg.getInt("xp.woodcutting.base", 5);
        this.baseMine = cfg.getInt("xp.mining.base", 4);

        loadSection("xp.farming.crops", farmingXp);
        loadSection("xp.woodcutting.logs", logXp);
        loadSection("xp.mining.ores", oreXp);
    }

    private void loadSection(String path, Map<Material,Integer> map) {
        if (cfg.isConfigurationSection(path)) {
            for (String key : cfg.getConfigurationSection(path).getKeys(false)) {
                Material m = Material.matchMaterial(key);
                if (m != null) {
                    map.put(m, cfg.getInt(path + "." + key));
                }
            }
        }
    }

    public void handleBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Material type = e.getBlock().getType();

        if (isCrop(type)) {
            if (e.getBlock().getBlockData() instanceof Ageable age && age.getAge() == age.getMaximumAge()) {
                addXP(p.getUniqueId(), Skill.FARMING, farmingXp.getOrDefault(type, baseFarm));
            }
            return;
        }

        if (logXp.containsKey(type) || isLog(type)) {
            addXP(p.getUniqueId(), Skill.WOODCUTTING, logXp.getOrDefault(type, baseWood));
            return;
        }

        if (oreXp.containsKey(type) || isOre(type)) {
            addXP(p.getUniqueId(), Skill.MINING, oreXp.getOrDefault(type, baseMine));
        }
    }

    public void handleFish(PlayerFishEvent e) {
        if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player p = (Player)e.getPlayer();
            addXP(p.getUniqueId(), Skill.FISHING, baseFish);
        }
    }

    private boolean isCrop(Material m) {
        return switch (m) {
            case WHEAT, CARROTS, POTATOES, BEETROOTS, NETHER_WART, PUMPKIN, MELON -> true;
            default -> false;
        };
    }
    private boolean isLog(Material m) {
        String n = m.name();
        return n.endsWith("_LOG") || n.equals("MANGROVE_LOG") || n.equals("CHERRY_LOG");
    }
    private boolean isOre(Material m) {
        String n = m.name();
        return n.endsWith("_ORE") || (n.startsWith("DEEPSLATE_") && n.endsWith("_ORE"));
    }

    public void addXP(UUID uuid, Skill skill, int amount) {
        int before = store.getXP(uuid, skill);
        int after = before + Math.max(0, amount);
        store.setXP(uuid, skill, after);

        int beforeLvl = levelFromTotalXP(before);
        int afterLvl  = levelFromTotalXP(after);
        if (afterLvl > beforeLvl) {
            Player p = plugin.getServer().getPlayer(uuid);
            if (p != null) {
                p.sendMessage("§a§l[StardewSkills] §f" + skill.getKoreanName() + " §7레벨 업! §eLv." + afterLvl);
            }
        }
    }

    public static int totalXPForLevel(int level) {
        if (level <= 0) return 0;
        return 10 * level * (level + 1);
    }

    public static int levelFromTotalXP(int xp) {
        double a = 1.0, b = 1.0, c = -xp / 10.0;
        int L = (int)Math.floor((-b + Math.sqrt(b*b - 4*a*c)) / (2*a));
        return Math.max(0, L);
    }
}
