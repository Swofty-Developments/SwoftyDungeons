package net.swofty.dungeons;

import net.swofty.dungeons.dungeon.Dungeon;
import net.swofty.dungeons.dungeon.DungeonHandler;
import net.swofty.dungeons.dungeon.DungeonSession;
import net.swofty.dungeons.utilities.SUtil;
import net.swofty.dungeons.utilities.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Repeater {
    private final List<BukkitTask> tasks;

    public Repeater() {
        this.tasks = new ArrayList<>();

        if (SwoftyDungeons.getPlugin().getConfig().getBoolean("scoreboard-enabled")) {
            this.tasks.add(new BukkitRunnable() {
                @Override
                public void run() {
                    for (Map.Entry<UUID, Map.Entry<Dungeon, DungeonSession>> entry : DungeonHandler.getPlayerDungeonCache().entrySet()) {
                        try {
                            Player player = Bukkit.getPlayer(entry.getKey());

                            List<String> scoreboardLinesTemp = SwoftyDungeons.getPlugin().getMessages().getStringList("messages.scoreboard");
                            List<String> scoreboardLines = new ArrayList<>();
                            scoreboardLinesTemp.forEach(entry2 -> {
                                scoreboardLines.add(SUtil.variableize(SUtil.translateColorWords(entry2),
                                        Arrays.asList(
                                                Map.entry("$NAME", entry.getValue().getKey().getName()),
                                                Map.entry("$TIME", String.valueOf(new DecimalFormat("#.00").format(Double.parseDouble(String.valueOf(System.currentTimeMillis() - entry.getValue().getValue().timeStarted)) / 1000))),
                                                Map.entry("$DAMAGE_DEALT", String.valueOf(entry.getValue().getValue().getDamageDealt())),
                                                Map.entry("$DAMAGE_RECIEVED", String.valueOf(entry.getValue().getValue().getDamageRecieved())),
                                                Map.entry("$ENTITIES_KILLED", String.valueOf(entry.getValue().getValue().getDamageRecieved())),
                                                Map.entry("$BEST_TIME",
                                                        String.valueOf(SwoftyDungeons.getPlugin().getSql().getTimesForPlayer(entry.getKey()).get(entry.getValue().getKey())).equals("null") ?
                                                                "§cNever completed" : new SimpleDateFormat("mm:ss.SSS").format(SwoftyDungeons.getPlugin().getSql().getTimesForPlayer(entry.getKey()).get(entry.getValue().getKey())))
                                        )));
                            });
                            //Collections.reverse(scoreboardLines); - did not end up needing this

                            Sidebar sidebar = new Sidebar(player, scoreboardLines.get(0), "dungeon");

                            for (int x = 1; x < scoreboardLines.size(); x++) {
                                sidebar.add(scoreboardLines.get(x));
                            }
                            sidebar.apply(player);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.runTaskTimer(SwoftyDungeons.getPlugin(), 3, 2));
        }
    }

    public void stop() {
        for (BukkitTask task : this.tasks)
            task.cancel();
    }
}
