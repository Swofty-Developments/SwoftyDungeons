package net.swofty.dungeons.dungeon;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DungeonHandler {

    @Getter
    public static HashMap<UUID, Map.Entry<Dungeon, DungeonSession>> playerDungeonCache = new HashMap<>();

    public static void startDungeon(Player player, Dungeon dungeon) {

    }

    public static void endDungeon(Player player, Dungeon dungeon) {

    }

    public enum EndReason {
        DEATH(),
        DUNGEON_UPDATE()
    }
}
