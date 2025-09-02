package gg.chill.stardewskills.data;

import java.io.File;
import java.sql.*;
import java.util.UUID;

import gg.chill.stardewskills.StardewSkillsPlugin;
import gg.chill.stardewskills.model.Skill;

public class PlayerDataStore implements AutoCloseable {
    private final StardewSkillsPlugin plugin;
    private final Connection conn;

    public PlayerDataStore(StardewSkillsPlugin plugin) {
        this.plugin = plugin;
        try {
            File dataDir = new File(plugin.getDataFolder(), "data");
            if (!dataDir.exists()) dataDir.mkdirs();
            File dbFile = new File(dataDir, "stardewskills.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            conn = DriverManager.getConnection(url);
            initSchema();
        } catch (Exception e) {
            throw new RuntimeException("Failed to init SQLite", e);
        }
    }

    private void initSchema() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS player_skills(" +
                    "uuid TEXT NOT NULL," +
                    "skill TEXT NOT NULL," +
                    "xp INTEGER NOT NULL DEFAULT 0," +
                    "PRIMARY KEY (uuid, skill))");
        }
    }

    public int getXP(UUID uuid, Skill skill) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT xp FROM player_skills WHERE uuid=? AND skill=?")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, skill.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("getXP failed: " + e.getMessage());
        }
        return 0;
    }

    public void setXP(UUID uuid, Skill skill, int xp) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO player_skills(uuid, skill, xp) VALUES(?,?,?) " +
                        "ON CONFLICT(uuid, skill) DO UPDATE SET xp=excluded.xp")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, skill.name());
            ps.setInt(3, xp);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("setXP failed: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException ignored) {}
    }
}
