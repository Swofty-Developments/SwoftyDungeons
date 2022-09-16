package net.swofty.dungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
    public Location top;
    @Getter
    @Setter
    public ArrayList<ItemStack> kit = new ArrayList<>();
    @Getter
    @Setter
    public HashMap<Integer, Spawner> spawners = new HashMap<>();

    public Dungeon() {
        this.finished = false;
    }

}
