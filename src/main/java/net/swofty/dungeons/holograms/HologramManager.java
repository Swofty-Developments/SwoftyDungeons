package net.swofty.dungeons.holograms;

import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.dungeon.DungeonRegistry;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class HologramManager {

    public static ArrayList<Hologram> toHide = new ArrayList<>();
    public static ArrayList<Hologram> toHide2 = new ArrayList<>();

    public static void runHologramLoop() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            toHide.forEach(s -> {
                s.hide(player, true);
            });
            toHide2.forEach(s -> {
                s.hide(player, true);
            });
            toHide.clear();
            toHide2.clear();
        });

        DungeonRegistry.dungeonRegistry.forEach(dungeon -> {
            if (dungeon.getSpawners() != null && !dungeon.getSpawners().isEmpty()) {
                dungeon.getSpawners().forEach((key, value) -> {
                    toHide.add(new Hologram(
                                    value.getLocation().clone().add(0.5, -0.5, 0.5),
                            SUtil.variableize(
                                    SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getStringList("messages.dungeons.holograms.spawner")),
                                    Arrays.asList(Map.entry("$SPAWNER", String.valueOf(key)), Map.entry("$NAME", dungeon.getName()))))
                    );
                });
            }
            if (dungeon.getTop() != null) {
                Location location = dungeon.getTop();
                Map<UUID, Long> sortedLeaderboard = SUtil.sortByValue(new HashMap<>(SwoftyDungeons.getPlugin().getSql().getDungeonTop(dungeon.getName())));
                ArrayList<String> cached = new ArrayList<>();
                for (int x = 0; x < SwoftyDungeons.getPlugin().getConfig().getInt("leaderboard-display-size"); x++) {
                    if (sortedLeaderboard.size() <= x) {
                        cached.add(SUtil.variableize(
                                SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getString("messages.dungeons.holograms.leaderboard-entry")),
                                Arrays.asList(
                                        Map.entry("$NUMBER", String.valueOf(x + 1)),
                                        Map.entry("$USERNAME", "§cNone"),
                                        Map.entry("$TIME", "§cNone")
                                )));
                    } else {
                        cached.add(SUtil.variableize(
                                SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getString("messages.dungeons.holograms.leaderboard-entry")),
                                Arrays.asList(
                                        Map.entry("$NUMBER", String.valueOf(x + 1)),
                                        Map.entry("$USERNAME", Bukkit.getOfflinePlayer(sortedLeaderboard.entrySet().stream().collect(Collectors.toList()).get(x).getKey()).getName()),
                                        Map.entry("$TIME", new SimpleDateFormat("mm:ss.SSS").format(sortedLeaderboard.entrySet().stream().collect(Collectors.toList()).get(x).getValue()))
                                )));
                    }
                }

                Bukkit.getOnlinePlayers().forEach(player -> {
                    ArrayList<String> hologram = new ArrayList<>();
                    if (SwoftyDungeons.getPlugin().getSql().getPosition(player.getUniqueId(), dungeon) != 0) {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getStringList("messages.dungeons.holograms.leaderboard-top")),
                                        Arrays.asList(
                                                Map.entry("$NAME", dungeon.getName()),
                                                Map.entry("$PLAYERTIME", new SimpleDateFormat("mm:ss.SSS").format(SwoftyDungeons.getPlugin().getSql().getTimesForPlayer(player.getUniqueId()).get(dungeon)))
                                        )));
                    } else {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getStringList("messages.dungeons.holograms.leaderboard-top")),
                                        Arrays.asList(
                                                Map.entry("$NAME", dungeon.getName()),
                                                Map.entry("$PLAYERTIME", "§cNever played dungeon")
                                        )));
                    }

                    hologram.addAll(cached);

                    if (SwoftyDungeons.getPlugin().getSql().getPosition(player.getUniqueId(), dungeon) != 0) {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getStringList("messages.dungeons.holograms.leaderboard-bottom")),
                                        Arrays.asList(
                                                Map.entry("$NAME", dungeon.getName()),
                                                Map.entry("$PLAYERTIME", new SimpleDateFormat("mm:ss.SSS").format(SwoftyDungeons.getPlugin().getSql().getTimesForPlayer(player.getUniqueId()).get(dungeon)))
                                        )));
                    } else {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getStringList("messages.dungeons.holograms.leaderboard-bottom")),
                                        Arrays.asList(
                                                Map.entry("$NAME", dungeon.getName()),
                                                Map.entry("$PLAYERTIME", "§cNever played dungeon")
                                        )));
                    }

                    Hologram holo = new Hologram(location, hologram);
                    holo.show(player);
                    toHide2.add(holo);
                });
            }
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            toHide.forEach(s -> {
                s.show(player);
            });
        });
    }

}
