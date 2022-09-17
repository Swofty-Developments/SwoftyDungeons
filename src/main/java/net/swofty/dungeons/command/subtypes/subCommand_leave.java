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
public class subCommand_leave extends DungeonCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {

        if (DungeonHandler.getFromPlayer(sender.getPlayer()) == null) {
            send(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.not-in-a-dungeon")));
            return;
        }

        DungeonHandler.endDungeon(sender.getPlayer(), DungeonHandler.EndReason.DEATH);
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
