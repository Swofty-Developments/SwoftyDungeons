package net.swofty.dungeons.listener.listeners;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.dungeon.DungeonHandler;
import net.swofty.dungeons.dungeon.DungeonSession;
import net.swofty.dungeons.listener.PListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;

public class DungeonListeners extends PListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getWorlds().forEach(world -> {
            world.getEntities().forEach(entity -> {
                if (!entity.getPersistentDataContainer().has(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING)) return;
                String playerUuid = entity.getPersistentDataContainer().get(new NamespacedKey(SwoftyDungeons.getPlugin(), "specMob"), PersistentDataType.STRING);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getUniqueId().equals(UUID.fromString(playerUuid))) continue;
                    ((CraftPlayer) player).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(entity.getEntityId()));
                }
            });
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (DungeonHandler.getFromPlayer(e.getPlayer()) != null) {
            DungeonHandler.endDungeon(e.getPlayer(), DungeonHandler.EndReason.DEATH);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent e) {
        Entity entity = e.getEntity();

        if (!entity.getPersistentDataContainer().has(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING)) return;
        if (!e.getTarget().getUniqueId().equals(UUID.fromString(entity.getPersistentDataContainer().get(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING)))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        Entity entity = e.getEntity();
        if (!entity.getPersistentDataContainer().has(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING)) return;
        String playerUuid = entity.getPersistentDataContainer().get(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(UUID.fromString(playerUuid))) continue;
            ((CraftPlayer) player).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(entity.getEntityId()));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Entity entity = e.getEntity();
            if (!entity.getPersistentDataContainer().has(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING)) return;

            UUID playerUuid = UUID.fromString(entity.getPersistentDataContainer().get(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING));
            DungeonSession session = DungeonHandler.playerDungeonCache.get(playerUuid).getValue();
            session.damageDealt += (long) e.getFinalDamage();

            DungeonHandler.playerDungeonCache.put(playerUuid, Map.entry(DungeonHandler.playerDungeonCache.get(playerUuid).getKey(), session));
            return;
        }
        if (e.getEntity() instanceof Player) {
            Entity entity = e.getDamager();
            if (!entity.getPersistentDataContainer().has(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING)) return;

            UUID playerUuid = UUID.fromString(entity.getPersistentDataContainer().get(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING));
            DungeonSession session = DungeonHandler.playerDungeonCache.get(playerUuid).getValue();
            session.damageRecieved += (long) e.getFinalDamage();

            DungeonHandler.playerDungeonCache.put(playerUuid, Map.entry(DungeonHandler.playerDungeonCache.get(playerUuid).getKey(), session));
            return;
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if (!entity.getPersistentDataContainer().has(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING)) return;

        UUID playerUuid = UUID.fromString(entity.getPersistentDataContainer().get(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING));
        DungeonSession session = DungeonHandler.playerDungeonCache.get(playerUuid).getValue();
        session.entitiesKilled += 1;

        DungeonHandler.playerDungeonCache.put(playerUuid, Map.entry(DungeonHandler.playerDungeonCache.get(playerUuid).getKey(), session));
    }
}
