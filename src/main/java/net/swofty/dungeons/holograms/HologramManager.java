package net.swofty.dungeons.holograms;

import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.dungeon.DungeonRegistry;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class HologramManager {

    public static ArrayList<Hologram> toHide = new ArrayList<>();

    public static void runHologramLoop() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            toHide.forEach(s -> {
                s.hide(player, true);
            });
            toHide.clear();
        });

        DungeonRegistry.dungeonRegistry.forEach(dungeon -> {
            if (dungeon.getSpawners() != null && !dungeon.getSpawners().isEmpty()) {
                dungeon.getSpawners().forEach((key, value) -> {
                    toHide.add(new Hologram(
                                    value.getLocation().clone().add(0.5, -0.5, 0.5),
                            SUtil.variableize(
                                    SUtil.translateColorWords(SwoftyDungeons.getPlugin().getMessages().getStringList("messages.dungeons.holograms.spawner")),
                                    Arrays.asList(Map.entry("$SPAWNER", key))))
                    );
                });
            }
            /*if (parkour.getTop() != null) {
                Location location = parkour.getTop();
                Map<UUID, Long> sortedLeaderboard = SUtil.sortByValue(new HashMap<>(SwoftyParkour.getPlugin().getSql().getParkourTop(parkour)));
                ArrayList<String> cached = new ArrayList<>();
                for (int x = 0; x < SwoftyParkour.getPlugin().getConfig().getInt("leaderboard-display-size"); x++) {
                    if (sortedLeaderboard.size() <= x) {
                        cached.add(SUtil.variableize(
                                SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.holograms.leaderboard-entry")),
                                Arrays.asList(
                                        Map.entry("$NUMBER", String.valueOf(x + 1)),
                                        Map.entry("$USERNAME", "§cNone"),
                                        Map.entry("$TIME", "§cNone")
                                )));
                    } else {
                        cached.add(SUtil.variableize(
                                SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.holograms.leaderboard-entry")),
                                Arrays.asList(
                                        Map.entry("$NUMBER", String.valueOf(x + 1)),
                                        Map.entry("$USERNAME", Bukkit.getOfflinePlayer(sortedLeaderboard.entrySet().stream().collect(Collectors.toList()).get(x).getKey()).getName()),
                                        Map.entry("$TIME", new SimpleDateFormat("mm:ss.SSS").format(sortedLeaderboard.entrySet().stream().collect(Collectors.toList()).get(x).getValue()))
                                )));
                    }
                }

                Bukkit.getOnlinePlayers().forEach(player -> {
                    ArrayList<String> hologram = new ArrayList<>();
                    if (SwoftyParkour.getPlugin().getSql().getPosition(player.getUniqueId(), parkour) != 0) {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.leaderboard-top")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", new SimpleDateFormat("mm:ss.SSS").format(SwoftyParkour.getPlugin().getSql().getTimesForPlayer(player.getUniqueId()).get(parkour)))
                                        )));
                    } else {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.leaderboard-top")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", "§cNever completed course")
                                        )));
                    }

                    hologram.addAll(cached);

                    if (SwoftyParkour.getPlugin().getSql().getPosition(player.getUniqueId(), parkour) != 0) {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.leaderboard-bottom")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", new SimpleDateFormat("mm:ss.SSS").format(SwoftyParkour.getPlugin().getSql().getTimesForPlayer(player.getUniqueId()).get(parkour)))
                                        )));
                    } else {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.leaderboard-bottom")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", "§cNever completed course")
                                        )));
                    }

                    Hologram holo = new Hologram(location, hologram);
                    holo.show(player);
                    toHide2.add(holo);
                });
            }*/
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            toHide.forEach(s -> {
                s.show(player);
            });
        });
    }

}
