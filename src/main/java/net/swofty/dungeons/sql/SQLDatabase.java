package net.swofty.dungeons.sql;

import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.dungeon.Dungeon;
import net.swofty.dungeons.dungeon.DungeonRegistry;
import net.swofty.dungeons.dungeon.DungeonSession;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class SQLDatabase {
    private static final String DATABASE_FILENAME = "database.db";
    private File file;

    public SQLDatabase() {
        File file = new File(SwoftyDungeons.getPlugin().getDataFolder(), DATABASE_FILENAME);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                SwoftyDungeons.getPlugin().saveResource(DATABASE_FILENAME, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.file = file;
    }

    public Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            if (connection != null) {
                return connection;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Map<DungeonSession, Long> getSessions(UUID uuid) {
        try (Connection connection = SwoftyDungeons.getPlugin().sql.getConnection()) {
            HashMap<DungeonSession, Long> map = new HashMap();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `dungeon_sessions` WHERE uuid=?");
            statement.setString(1, uuid.toString());
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                map.put(new DungeonSession(set.getLong("timeSpent"), set.getLong("entitiesKilled"), set.getLong("damageDealt"), set.getLong("damageRecieved"), set.getString("dungeon"), set.getLong("time")),
                        set.getLong("time"));
            }

            LinkedHashMap<DungeonSession, Long> reverseSortedMap = new LinkedHashMap<>();

            map.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

            return reverseSortedMap;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void deleteDungeon(String dungeonId) {
        try (Connection connection = SwoftyDungeons.getPlugin().sql.getConnection()) {
            HashMap<UUID, Long> map = new HashMap();

            PreparedStatement statement = connection.prepareStatement("DELETE FROM `dungeon_sessions` WHERE dungeon=?");
            statement.setString(1, dungeonId);
            statement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Map<Dungeon, Long> getTimesForPlayer(UUID uuid) {
        Map<Dungeon, Long> toReturn = new HashMap<>();
        DungeonRegistry.dungeonRegistry.forEach(dungeon2 -> {
            toReturn.put(dungeon2, getDungeonTop(dungeon2.getName()).get(uuid));
        });
        //if (toReturn.values().stream().allMatch(Objects::isNull)) return null;
        return toReturn;
    }

    public Map<UUID, Long> getDungeonTop(String dungeonId) {
        try (Connection connection = SwoftyDungeons.getPlugin().sql.getConnection()) {
            HashMap<UUID, Long> map = new HashMap();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `dungeon_sessions` WHERE dungeon=?");
            statement.setString(1, dungeonId);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                if (map.containsKey(UUID.fromString(set.getString("uuid")))) {
                    if (map.get(UUID.fromString(set.getString("uuid"))) > set.getLong("timeSpent")) {
                        continue;
                    }
                }

                map.put(UUID.fromString(set.getString("uuid")),
                        set.getLong("timeSpent"));
            }

            LinkedHashMap<UUID, Long> reverseSortedMap = new LinkedHashMap<>();

            map.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

            return reverseSortedMap;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int getPosition(UUID uuid, Dungeon dungeon) {
        Map<UUID, Long> map = getDungeonTop(dungeon.getName());

        for (int x = 0; x < map.size(); x++) {
            if (new ArrayList<>(map.entrySet()).get(x).getKey().toString().equals(uuid.toString())) {
                return x + 1;
            }
        }

        return 0;
    }
}