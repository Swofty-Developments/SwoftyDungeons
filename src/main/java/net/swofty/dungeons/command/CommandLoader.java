package net.swofty.dungeons.command;

import java.util.ArrayList;
import java.util.List;

public class CommandLoader {
    public static List<DungeonCommand> commands;

    public CommandLoader() {
        this.commands = new ArrayList<>();
    }

    public void register(DungeonCommand command) {
        commands.add(command);
    }

    public int getCommandAmount() {
        return commands.size();
    }
}