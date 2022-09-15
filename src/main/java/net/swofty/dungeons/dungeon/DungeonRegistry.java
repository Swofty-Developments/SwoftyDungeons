package net.swofty.dungeons.dungeon;

import lombok.Getter;
import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.data.Config;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class DungeonRegistry {

    @Getter
    public static ArrayList<Dungeon> dungeonRegistry = new ArrayList<>();

    public static void loadFromConfig(Config config) {
        if (!config.contains("dungeons")) return;

        config.getConfigurationSection("dungeons").getKeys(false).forEach(key -> {
            ConfigurationSection section = config.getConfigurationSection("dungeons").getConfigurationSection(key);
            Dungeon dungeon = new Dungeon();

            dungeon.setFinished(section.getBoolean("finished"));
            dungeon.setName(section.getString("name"));
            dungeon.setSpawnLocation((Location) section.get("spawnlocation"));

            HashMap<String, Spawner> spawners = new HashMap<>();
            if (section.getConfigurationSection("spawners") != null) {
                section.getConfigurationSection("spawners").getKeys(true).stream().sorted().forEach(s -> {
                    spawners.put(s, (Spawner) section.getConfigurationSection("spawners").get(s));
                });
            }
            dungeon.setSpawners(spawners);

            dungeonRegistry.add(dungeon);
        });
    }

    public static Dungeon getFromName(String name) {
        for (Dungeon dungeon : dungeonRegistry) {
            if (dungeon.getName().equalsIgnoreCase(name)) {
                return dungeon;
            }
        }

        return null;
    }

    public static void updateDungeon(Dungeon dungeon) {
        dungeonRegistry.removeIf(dungeon2 -> dungeon2.getName().equalsIgnoreCase(dungeon.getName()));
        dungeonRegistry.add(dungeon);

        saveDungeon(dungeon, SwoftyDungeons.getPlugin().getDungeons());
    }

    public static void removeDungeon(String name, Config config) {
        dungeonRegistry.removeIf(dungeon2 -> dungeon2.getName().equalsIgnoreCase(name));
        config.getConfigurationSection("dungeons").set(name, null);
        config.save();

        SwoftyDungeons.getPlugin().getSql().deleteDungeon(name);
    }

    private static void saveDungeon(Dungeon dungeon, Config config) {
        if (config.getConfigurationSection("dungeons") == null) {
            config.createSection("dungeons");
        }
        ConfigurationSection section = config.getConfigurationSection("dungeons").createSection(dungeon.getName());

        section.set("finished", dungeon.getFinished());
        section.set("name", dungeon.getName());
        section.set("spawnlocation", dungeon.getSpawnLocation());

        section.set("spawners", "");
        section.createSection("spawners");
        if (dungeon.spawners != null) {
            for (Map.Entry<String, Spawner> spawner : dungeon.spawners.entrySet()) {
                section.getConfigurationSection("checkpoints").set(spawner.getKey(), spawner.getValue());
            }
        }
        config.save();
    }
}
