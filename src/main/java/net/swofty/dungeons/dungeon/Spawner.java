package net.swofty.dungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spawner implements ConfigurationSerializable {

    @Getter
    @Setter
    public Location location;
    @Getter
    @Setter
    public List<String> spawnConditions = new ArrayList<>();
    @Getter
    @Setter
    public List<Map.Entry<Condition, Integer>> spawnConditionsProgrammable = new ArrayList<>();
    @Getter
    @Setter
    public Map.Entry<EntityType, Map.Entry<Integer, Integer>> spawnAmountProgrammable = Map.entry(EntityType.ZOMBIE, Map.entry(1, 1));
    @Getter
    @Setter
    public String spawnAmount = "";

    public Spawner(Location location) {
        this.location = location;
        this.spawnConditions = new ArrayList<>();
        this.spawnConditionsProgrammable = new ArrayList<>();
        this.spawnAmount = "";
    }

    public void convertSpawnAmount() {
        Integer one = Integer.parseInt(spawnAmount.split(";")[0]);
        Integer two = Integer.parseInt(spawnAmount.split(";")[1]);
        EntityType type = EntityType.valueOf(spawnAmount.split(";")[2]);

        spawnAmountProgrammable = Map.entry(type, Map.entry(one, two));
    }

    public void convertSpawnConditions() {
        List<Map.Entry<Condition, Integer>> toSet = new ArrayList<>();

        if (spawnConditions.isEmpty()) {
            throw new RuntimeException();
        }

        for (String s : spawnConditions) {
            Condition condition = Condition.valueOf(s.split(";")[0]);
            toSet.add(Map.entry(condition, Integer.valueOf(s.split(";")[1])));
        }

        spawnConditionsProgrammable = toSet;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return Map.ofEntries(
                Map.entry("amount", spawnAmount),
                Map.entry("conditions", spawnConditions),
                Map.entry("conditionsProgrammable", spawnConditionsProgrammable),
                Map.entry("location", location)
        );
    }

    public static Spawner deserialize(Map<String, Object> map) {
        Spawner spawner = new Spawner((Location) map.get("location"));

        spawner.setSpawnConditions((List<String>) map.get("conditions"));
        spawner.setSpawnAmount((String) map.get("amount"));
        spawner.setSpawnConditionsProgrammable((List<Map.Entry<Condition, Integer>>) map.get("conditionsProgrammable"));

        return spawner;
    }

    public enum Condition {
        MINIMUM_KILLS(),
        MINIMUM_TIME(),
        HEALTH_ABOVE(),
        HEALTH_BELOW(),
        ;
    }
}
