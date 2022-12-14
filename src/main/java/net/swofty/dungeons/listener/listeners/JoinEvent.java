package net.swofty.dungeons.listener.listeners;

import net.swofty.dungeons.dungeon.DungeonHandler;
import net.swofty.dungeons.listener.PListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinEvent extends PListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (DungeonHandler.getFromPlayer(e.getPlayer()) != null) {
            DungeonHandler.endDungeon(e.getPlayer(), DungeonHandler.EndReason.DEATH);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (DungeonHandler.getFromPlayer(e.getEntity()) != null) {
            DungeonHandler.endDungeon(e.getEntity(), DungeonHandler.EndReason.DEATH);
        }
    }

}
