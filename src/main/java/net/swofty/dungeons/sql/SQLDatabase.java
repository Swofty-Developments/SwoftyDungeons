package net.swofty.dungeons.sql;

import net.swofty.dungeons.SwoftyDungeons;
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
                map.put(new DungeonSession(set.getLong("timeSpent"), set.getLong("entitiesKilled"), set.getLong("damageDealt"), set.getLong("damageRecieved"), set.getString("dungeon")),
                        set.getLong("time"));
            }

            return sortByValue(map);
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
            ResultSet set = statement.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Map<UUID, Long> getDungeonTop(String dungeonId) {
        try (Connection connection = SwoftyDungeons.getPlugin().sql.getConnection()) {
            HashMap<UUID, Long> map = new HashMap();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `dungeon_sessions` WHERE dungeon=?");
            statement.setString(1, dungeonId);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                map.put(UUID.fromString(set.getString("uuid")),
                        set.getLong("time"));
            }

            return sortByValue(map);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        List<K> alKeys = new ArrayList<>(result.keySet());
        Collections.reverse(alKeys);

        Map<K, V> toReturn = new LinkedHashMap<>();
        // iterate LHM using reverse order of keys
        for(K strKey : alKeys){
            toReturn.put(strKey, result.get(strKey));
        }

        return toReturn;
    }
}