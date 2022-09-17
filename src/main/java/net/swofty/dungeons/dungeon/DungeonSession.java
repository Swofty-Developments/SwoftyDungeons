package net.swofty.dungeons.dungeon;

import lombok.Getter;

public class DungeonSession {
    @Getter
    public Long timeSpent;
    @Getter
    public Long timeStarted;
    @Getter
    public Long entitiesKilled;
    @Getter
    public Long damageDealt;
    @Getter
    public Long damageRecieved;
    @Getter
    public String dungeon;

    public DungeonSession(String dungeon) {
        this.timeSpent = 0L;
        this.entitiesKilled = 0L;
        this.damageDealt = 0L;
        this.damageRecieved = 0L;
        this.dungeon = dungeon;
        this.timeStarted = System.currentTimeMillis();
    }

    public DungeonSession(Long timeSpent, Long entitiesKilled, Long damageDealt, Long damageRecieved, String dungeon) {
        this.timeSpent = timeSpent;
        this.entitiesKilled = entitiesKilled;
        this.damageDealt = damageDealt;
        this.damageRecieved = damageRecieved;
        this.dungeon = dungeon;
        this.timeStarted = System.currentTimeMillis();
    }
}
