package net.swofty.dungeons.listener.listeners;

import net.swofty.dungeons.dungeon.DungeonHandler;
import net.swofty.dungeons.listener.PListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DungeonListeners extends PListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (DungeonHandler.getFromPlayer(e.getPlayer()) != null) {
            DungeonHandler.endDungeon(e.getPlayer(), DungeonHandler.EndReason.DEATH);
        }
    }

}
