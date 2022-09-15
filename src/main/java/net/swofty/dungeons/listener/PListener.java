package net.swofty.dungeons.listener;

import net.swofty.dungeons.SwoftyDungeons;
import org.bukkit.event.Listener;

public class PListener implements Listener {
    private static int amount = 0;

    protected PListener() {
        SwoftyDungeons.getPlugin().getServer().getPluginManager().registerEvents(this, SwoftyDungeons.getPlugin());
        amount++;
    }

    public static int getAmount() {
        return amount;
    }
}