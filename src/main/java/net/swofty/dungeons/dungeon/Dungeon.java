package net.swofty.dungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.HashMap;

public class Dungeon {

    @Getter
    @Setter
    public String name;
    @Getter
    @Setter
    public Location spawnLocation;
    @Getter
    @Setter
    public Boolean finished;
    @Getter
    @Setter
    public HashMap<String, Spawner> spawners;

    public Dungeon() {
        this.finished = false;
    }

}
