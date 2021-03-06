package me.aleksilassila.islands.utils;

import me.aleksilassila.islands.Islands;
import me.aleksilassila.islands.IslandsConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigMigrator {
    private final FileConfiguration islandsConfig;

    public ConfigMigrator() {
        this.islandsConfig = IslandsConfig.getConfig();

        boolean doTrustedMigraton = false;
        boolean doProtectionMigration = false;

        // Validate trusted
        for (String islandId : islandsConfig.getKeys(false)) {
            if (islandsConfig.get(islandId + ".trusted") instanceof List) {
                doTrustedMigraton = true;
            }

            if (!islandsConfig.contains(islandId + ".protect")) {
                doProtectionMigration = true;
            }
        }

        if (doTrustedMigraton) migrateTrustedPlayers();
        if (doProtectionMigration) migrateIslandProtection();
    }

    private void migrateIslandProtection() {
        Islands.instance.getLogger().warning("ISLANDS.YML IS USING OLD SYNTAX FOR ISLAND PROTECTION. MIGRATING CONFIG...");

        for (String islandId : islandsConfig.getKeys(false)) {
            if (islandsConfig.contains(islandId + ".protect")) continue;

            islandsConfig.set(islandId + ".protect.building", true);
            islandsConfig.set(islandId + ".protect.containers", true);
            islandsConfig.set(islandId + ".protect.doors", true);
            islandsConfig.set(islandId + ".protect.utility", true);
        }

        IslandsConfig.saveIslandsConfig();
    }

    private void migrateTrustedPlayers() {
        Islands.instance.getLogger().warning("ISLANDS.YML IS USING OLD SYNTAX FOR TRUSTED PLAYERS. MIGRATING CONFIG...");

        for (String islandId : islandsConfig.getKeys(false)) {
            if (!islandsConfig.contains(islandId + ".trusted")) continue;
            if (!(islandsConfig.get(islandId + ".trusted") instanceof List)) continue;

            List<String> trustedList = islandsConfig.getStringList(islandId + ".trusted");
            islandsConfig.set(islandId + ".trusted", null);

            for (String uuid : trustedList) {
                islandsConfig.set(islandId + ".trusted." + uuid + ".build", true);
                islandsConfig.set(islandId + ".trusted." + uuid + ".accessContainers", false);
                islandsConfig.set(islandId + ".trusted." + uuid + ".accessDoors", false);
                islandsConfig.set(islandId + ".trusted." + uuid + ".accessUtility", false);
            }
        }

        IslandsConfig.saveIslandsConfig();
    }
}
