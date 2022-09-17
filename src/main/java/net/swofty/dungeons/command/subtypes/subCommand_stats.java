package net.swofty.dungeons.command.subtypes;

import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.command.CommandCooldown;
import net.swofty.dungeons.command.CommandParameters;
import net.swofty.dungeons.command.CommandSource;
import net.swofty.dungeons.command.DungeonCommand;
import net.swofty.dungeons.gui.guis.PlayerStatisticsGUI;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@CommandParameters(permission = "dungeon.stats")
public class subCommand_stats extends DungeonCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length < 2) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.usage-command")), Arrays.asList(Map.entry("$USAGE", "/dungeon stats <ign>"))));
            return;
        }

        UUID player;
        if (Pattern.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", args[1])) {
            player = UUID.fromString(args[1]);
        } else {
            player = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
        }

        if (SwoftyDungeons.getPlugin().getSql().getTimesForPlayer(player) == null && Bukkit.getPlayer(args[1]) == null) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.player-not-found")), Arrays.asList(Map.entry("$NAME", args[1]))));
            return;
        }

        new PlayerStatisticsGUI(player).open(sender.getPlayer());
    }

    @Override
    public List<String> tabCompleters(CommandSender sender, String alias, String[] args) {
        if (args.length < 3)
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        return null;
    }

    @Override
    public long cooldownSeconds() {
        return 1;
    }
}
