package net.swofty.dungeons.command.subtypes;

import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.command.CommandCooldown;
import net.swofty.dungeons.command.CommandParameters;
import net.swofty.dungeons.command.CommandSource;
import net.swofty.dungeons.command.DungeonCommand;
import net.swofty.dungeons.dungeon.Dungeon;
import net.swofty.dungeons.dungeon.DungeonRegistry;
import net.swofty.dungeons.gui.guis.DungeonManagementGUI;
import net.swofty.dungeons.gui.guis.DungeonStatisticsGUI;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandParameters(permission = "dungeon.info")
public class subCommand_info extends DungeonCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length < 2) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.usage-command")), Arrays.asList(Map.entry("$USAGE", "/dungeon info <dungeon>"))));
            return;
        }

        String name = args[1];

        if (DungeonRegistry.getFromName(name) == null) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.dungeon-not-found")), Arrays.asList(Map.entry("$NAME", name))));
            return;
        }

        new DungeonStatisticsGUI(DungeonRegistry.getFromName(name)).open(sender.getPlayer());
    }

    @Override
    public List<String> tabCompleters(CommandSender sender, String alias, String[] args) {
        if (args.length < 3)
            return DungeonRegistry.getDungeonRegistry().stream().map(Dungeon::getName).collect(Collectors.toList());
        return null;
    }

    @Override
    public long cooldownSeconds() {
        return 1;
    }
}
