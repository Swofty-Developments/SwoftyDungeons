package net.swofty.dungeons.utilities;

import org.bukkit.scoreboard.Objective;

public class SidebarU {
    public static String getEntryFromScore(Objective o, int score) {
        if(o == null) return null;
        if(!hasScoreTaken(o, score)) return null;
        for (String s : o.getScoreboard().getEntries()) {
            if(o.getScore(s).getScore() == score) return o.getScore(s).getEntry();
        }
        return null;
    }

    public static boolean hasScoreTaken(Objective o, int score) {
        for (String s : o.getScoreboard().getEntries()) {
            if(o.getScore(s).getScore() == score) return true;
        }
        return false;
    }

    public static void replaceScore(Objective o, int score, String name) {
        if(hasScoreTaken(o, score)) {
            if(getEntryFromScore(o, score).equalsIgnoreCase(name)) return;
            if(!(getEntryFromScore(o, score).equalsIgnoreCase(name))) o.getScoreboard().resetScores(getEntryFromScore(o, score));
        }
        o.getScore(name).setScore(score);
    }
}
