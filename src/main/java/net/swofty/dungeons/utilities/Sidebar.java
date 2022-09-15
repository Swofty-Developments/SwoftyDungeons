package net.swofty.dungeons.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class Sidebar {
    public static Map<UUID, Sidebar> sidebarMap = new HashMap<>();
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();

    private String name;
    private String identifier;

    private Scoreboard board;
    private final Objective obj;
    private Objective health;
    private List<Score> scores;

    public Sidebar(Player target, String name, String identifier) {
        if (sidebarMap.containsKey(target.getUniqueId())) {
            Sidebar sidebar = sidebarMap.get(target.getUniqueId());
            this.name = name;
            this.identifier = identifier;
            this.scores = new ArrayList<>();
            this.board = sidebar.board;
            this.obj = sidebar.obj;
            this.health = sidebar.health;
        } else {
            this.name = name;
            this.identifier = identifier;
            this.board = manager.getNewScoreboard();
            this.obj = board.registerNewObjective(identifier, "");
            this.scores = new ArrayList<>();
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName(name);

        }
        sidebarMap.put(target.getUniqueId(), this);

        for (int i = 0; i < scores.size(); i++)
            scores.get(i).setScore(i);
        target.setScoreboard(board);
    }

    public void add(String s) {
        Score score = obj.getScore(s);
        scores.add(0, score);
    }

    public void apply(Player player) {
        try {
            if (sidebarMap.get(player.getUniqueId()) != null) {
                Scoreboard board = player.getScoreboard();
                Objective obj2 = board.getObjective(identifier);
                obj2.setDisplayName(name);
                for (int i = 0; i < scores.size(); i++)
                    SidebarU.replaceScore(obj, i, scores.get(i).getEntry());
            } else {
                for (int i = 0; i < scores.size(); i++)
                    scores.get(i).setScore(i);
                player.setScoreboard(board);
            }
        } catch (Exception ignored) { }
    }
}