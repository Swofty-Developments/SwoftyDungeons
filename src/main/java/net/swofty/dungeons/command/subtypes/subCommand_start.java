package net.swofty.dungeons.command.subtypes;

import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.command.CommandCooldown;
import net.swofty.dungeons.command.CommandParameters;
import net.swofty.dungeons.command.CommandSource;
import net.swofty.dungeons.command.DungeonCommand;
import net.swofty.dungeons.dungeon.Dungeon;
import net.swofty.dungeons.dungeon.DungeonHandler;
import net.swofty.dungeons.dungeon.DungeonRegistry;
import net.swofty.dungeons.gui.guis.DungeonManagementGUI;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandParameters(permission = "dungeon.play")
public class subCommand_start extends DungeonCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length < 2) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.usage-command")), Arrays.asList(Map.entry("$USAGE", "/dungeon play <dungeon>"))));
            return;
        }

        String name = args[1];
        Dungeon dungeon = DungeonRegistry.getFromName(name);

        if (dungeon == null) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.dungeon-not-found")), Arrays.asList(Map.entry("$NAME", name))));
            return;
        }

        if (!dungeon.getFinished()) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.dungeon-not-finished")), Arrays.asList(Map.entry("$NAME", name))));
            return;
        }

        DungeonHandler.startDungeon(sender.getPlayer(), DungeonRegistry.getFromName(name));
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
