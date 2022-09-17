package net.swofty.dungeons.dungeon;

import lombok.Getter;
import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DungeonHandler {

    @Getter
    public static HashMap<UUID, Map.Entry<Dungeon, DungeonSession>> playerDungeonCache = new HashMap<>();
    @Getter
    public static HashMap<UUID, List<BukkitTask>> bukkitTaskList = new HashMap<>();
    @Getter
    public static HashMap<UUID, Map.Entry<ItemStack[], Location>> playerCache = new HashMap<>();

    public static void startDungeon(Player player, Dungeon dungeon) {
        player.sendMessage("§8Creating dungeon session instance");
        playerCache.put(player.getUniqueId(), Map.entry(player.getInventory().getContents(), player.getLocation()));
        playerDungeonCache.put(player.getUniqueId(), Map.entry(dungeon, new DungeonSession(dungeon.getName())));

        player.sendMessage("§8Setting player inventory");
        player.getInventory().clear();
        player.getInventory().setContents(dungeon.getKit().toArray(new ItemStack[0]));

        player.sendMessage("§8Loading dungeon requirements");
        player.teleport(dungeon.getSpawnLocation());

        player.sendMessage("§8Starting dungeon tasks");
        List<BukkitTask> dungeonTasks = new ArrayList<>();
        dungeon.getSpawners().forEach((integer, spawner) -> {
            spawner.convertSpawnAmount();
            spawner.convertSpawnConditions();

            dungeonTasks.add(new BukkitRunnable() {
                @Override
                public void run() {
                    for (Map.Entry<Spawner.Condition, Integer> entry : spawner.getSpawnConditionsProgrammable()) {
                        DungeonSession session = playerDungeonCache.get(player.getUniqueId()).getValue();

                        switch (entry.getKey()) {
                            case HEALTH_ABOVE -> {
                                if (player.getHealth() < entry.getValue()) {
                                    return;
                                }
                            }

                            case HEALTH_BELOW -> {
                                if (player.getHealth() > entry.getValue()) {
                                    return;
                                }
                            }

                            case MINIMUM_TIME -> {
                                if ((System.currentTimeMillis() - session.getTimeStarted()) < entry.getValue()) {
                                    return;
                                }
                            }

                            case MINIMUM_KILLS -> {
                                if (session.getEntitiesKilled() < entry.getValue()) {
                                    return;
                                }
                            }
                        }
                    }

                    for (int x = 0; x < spawner.getSpawnAmountProgrammable().getValue().getKey(); x++) {
                        Entity entity = spawner.getLocation().getWorld().spawnEntity(spawner.getLocation(), spawner.getSpawnAmountProgrammable().getKey());
                        entity.getPersistentDataContainer().set(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING, player.getUniqueId().toString());
                    }
                }
            }.runTaskTimer(SwoftyDungeons.getPlugin(), (spawner.getSpawnAmountProgrammable().getValue().getValue() * 20), (spawner.getSpawnAmountProgrammable().getValue().getValue() * 20)));
        });
        bukkitTaskList.put(player.getUniqueId(), dungeonTasks);

        SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getStringList("messages.command.started-dungeon-message")),
                Arrays.asList(Map.entry("$NAME", dungeon.getName()))
        ).forEach(player::sendMessage);
        player.sendTitle(SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getString("messages.command.started-dungeon-title")),
                Arrays.asList(Map.entry("$NAME", dungeon.getName()))
        ), SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getString("messages.command.started-dungeon-subtitle")),
                Arrays.asList(Map.entry("$NAME", dungeon.getName()))
        ));
    }

    public static void endDungeon(Player player, EndReason reason) {
        DungeonSession session = playerDungeonCache.get(player.getUniqueId()).getValue();
        session.timeSpent = System.currentTimeMillis() - session.getTimeStarted();
        playerDungeonCache.remove(player.getUniqueId());

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        player.getInventory().clear();
        player.getInventory().setContents(playerCache.get(player.getUniqueId()).getKey());
        player.teleport(playerCache.get(player.getUniqueId()).getValue());
        playerCache.remove(player.getUniqueId());

        bukkitTaskList.remove(player.getUniqueId()).forEach(BukkitTask::cancel);

        player.getWorld().getEntities().forEach(entity -> {
            if (!entity.getPersistentDataContainer().has(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING)) return;

            String owner = entity.getPersistentDataContainer().get(new NamespacedKey(SwoftyDungeons.getPlugin(), "dungeonMob"), PersistentDataType.STRING);
            if (player.getUniqueId().toString().equals(owner)) {
                entity.remove();
            }
        });

        switch (reason) {
            case DUNGEON_DELETE -> {
                player.sendMessage("§aYou have been exited from your dungeon as it has been deleted by an administrator");
            }
            case DUNGEON_UPDATE -> {
                player.sendMessage("§aYou have been exited from your dungeon as it is being edited by an administrator");
            }
            case DEATH -> {
                player.sendMessage("§aYou have died inside of a dungeon");
            }
        }

        player.sendMessage("§aStatistics are as follows");
        player.sendMessage("§8- §eDungeon Name§7: §f" + session.getDungeon());
        player.sendMessage("§8- §eTime Spent§7: §f" + new SimpleDateFormat("mm:ss.SSS").format(session.getTimeSpent()));
        player.sendMessage("§8- §eEntities Killed§7: §f" + session.getEntitiesKilled());
        player.sendMessage("§8- §eDamage Dealt§7: §f" + session.getDamageDealt() + " hearts");
        player.sendMessage("§8- §eDamage Recieved§7: §f" + session.getDamageRecieved() + " hearts");

        try {
            PreparedStatement statement = SwoftyDungeons.getPlugin().getSql().getConnection().prepareStatement("INSERT INTO `dungeon_sessions` " +
                    "(`uuid`, `dungeon`, `timeSpent`, `time`, `entitiesKilled`, `damageDealt`, `damageRecieved`) VALUES (?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, session.dungeon);
            statement.setLong(3, session.timeSpent);
            statement.setLong(4, session.timeStarted);
            statement.setLong(5, session.entitiesKilled);
            statement.setLong(6, session.damageDealt);
            statement.setLong(6, session.damageRecieved);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Dungeon getFromPlayer(Player player) {
        for (Map.Entry<UUID, Map.Entry<Dungeon, DungeonSession>> entry : playerDungeonCache.entrySet()) {
            UUID uuid = entry.getKey();

            if (uuid == player.getUniqueId()) {
                return entry.getValue().getKey();
            }
        }
        return null;
    }

    public static void dungeonEdit(Dungeon dungeon) {
        getSessionsFromDungeon(dungeon).forEach((uuid, dungeonSession) -> {
            endDungeon(Bukkit.getPlayer(uuid), EndReason.DUNGEON_UPDATE);
        });
    }

    public static void dungeonDelete(Dungeon dungeon) {
        getSessionsFromDungeon(dungeon).forEach((uuid, dungeonSession) -> {
            endDungeon(Bukkit.getPlayer(uuid), EndReason.DUNGEON_DELETE);
        });
    }

    private static HashMap<UUID, DungeonSession> getSessionsFromDungeon(Dungeon dungeon) {
        HashMap<UUID, DungeonSession> toReturn = new HashMap<>();

        for (Map.Entry<UUID, Map.Entry<Dungeon, DungeonSession>> entry : playerDungeonCache.entrySet()) {
            UUID uuid = entry.getKey();
            Map.Entry<Dungeon, DungeonSession> dungeonDungeonSessionEntry = entry.getValue();

            if (dungeonDungeonSessionEntry.getKey() == dungeon) {
                toReturn.put(uuid, dungeonDungeonSessionEntry.getValue());
            }
        }

        return toReturn;
    }

    public enum EndReason {
        DEATH(),
        DUNGEON_UPDATE(),
        DUNGEON_DELETE()
    }
}
