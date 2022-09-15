package net.swofty.dungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;

public class Spawner {

    @Getter
    @Setter
    public Location location;
    @Getter
    @Setter
    public List<String> spawnConditions;
    @Getter
    @Setter
    public Integer spawnAmount;

    public Spawner() {}

}
