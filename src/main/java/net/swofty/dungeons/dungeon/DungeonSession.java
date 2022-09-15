package net.swofty.dungeons.dungeon;

import lombok.Getter;

public class DungeonSession {
    @Getter
    public Long timeSpent;
    @Getter
    public Long entitiesKilled;
    @Getter
    public Long damageDealt;
    @Getter
    public Long damageRecieved;
    @Getter
    public String dungeon;

    public DungeonSession(Long timeSpent, Long entitiesKilled, Long damageDealt, Long damageRecieved, String dungeon) {
        this.timeSpent = timeSpent;
        this.entitiesKilled = entitiesKilled;
        this.damageDealt = damageDealt;
        this.damageRecieved = damageRecieved;
        this.dungeon = dungeon;
    }
}
