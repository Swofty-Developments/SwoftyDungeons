package net.swofty.dungeons.command.subtypes;

import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.command.CommandCooldown;
import net.swofty.dungeons.command.CommandParameters;
import net.swofty.dungeons.command.CommandSource;
import net.swofty.dungeons.command.DungeonCommand;
import net.swofty.dungeons.dungeon.Dungeon;
import net.swofty.dungeons.dungeon.DungeonRegistry;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandParameters(permission = "dungeon.admin.create")
public class subCommand_create extends DungeonCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length == 1) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.usage-command")), Arrays.asList(Map.entry("$USAGE", "/dungeon create <name>"))));
            return;
        }

        String name = args[1];

        if (DungeonRegistry.getFromName(name) != null) {
            send(SUtil.variableize(SwoftyDungeons.getPlugin().messages.getString("messages.command.name-already-taken"), Arrays.asList(Map.entry("$NAME", name))));
            return;
        }

        Location originalLoc = sender.getPlayer().getLocation();
        Location loc = new Location(originalLoc.getWorld(), originalLoc.getBlockX(), originalLoc.getBlockY(), originalLoc.getBlockZ());

        Dungeon dungeon = new Dungeon();
        dungeon.setSpawnLocation(loc);
        dungeon.setName(name);

        DungeonRegistry.updateDungeon(dungeon);

        send(SUtil.variableize(SwoftyDungeons.getPlugin().messages.getStringList("messages.command.creation-message"), Arrays.asList(Map.entry("$NAME", name))));
    }

    @Override
    public List<String> tabCompleters(CommandSender sender, String alias, String[] args) {
        return null;
    }

    @Override
    public long cooldownSeconds() {
        return 1;
    }
}
