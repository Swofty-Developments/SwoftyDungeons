package net.swofty.dungeons.command;

import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class DungeonCommand implements CommandExecutor, TabCompleter {
    private static final Map<UUID, HashMap<DungeonCommand, Long>> CMD_COOLDOWN = new HashMap<>();
    public static final String COMMAND_SUFFIX = "subCommand_";

    private final CommandParameters params;
    private final String name;
    private final String description;
    private final String usage;
    private final List<String> aliases;
    private final String permission;

    private CommandSource sender;

    protected DungeonCommand() {
        this.params = this.getClass().getAnnotation(CommandParameters.class);
        this.name = this.getClass().getSimpleName().replace(COMMAND_SUFFIX, "").toLowerCase();
        this.description = this.params.description();
        this.usage = this.params.usage();
        this.aliases = Arrays.asList(this.params.aliases().split(","));
        this.permission = this.params.permission();
    }

    public abstract void run(CommandSource sender, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    public static void register() {
        SwoftyDungeons.getPlugin().commandMap.register("", new DungeonCommandHandler());
    }

    public static class DungeonCommandHandler extends Command {

        public DungeonCommand command;

        public DungeonCommandHandler() {
            super("dungeon", "Join, manage and leave dungeons", "", new ArrayList<>());
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (!(sender instanceof Player)) {
                System.out.println("Console senders cannot use commands");
                return false;
            }

            if (args.length == 0) {
                SwoftyDungeons.getPlugin().messages.getStringList("messages.command.usage-overall").forEach(s -> {
                    sender.sendMessage(SUtil.translateColorWords(s));
                });
                return false;
            }

            for (DungeonCommand dungeonCommand : CommandLoader.commands) {
                if (dungeonCommand.name.equals(args[0]) || dungeonCommand.aliases.contains(args[0])) {
                    this.command = dungeonCommand;
                }
            }

            if (this.command == null) {
                SwoftyDungeons.getPlugin().messages.getStringList("messages.command.usage-overall").forEach(s -> {
                    sender.sendMessage(SUtil.translateColorWords(s));
                });
                return false;
            }

            command.sender = new CommandSource(sender);

            if (!command.permission.equals("") && !sender.hasPermission(command.permission)) {
                sender.sendMessage(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.no-permission")));
                return false;
            }

            if (command instanceof CommandCooldown) {
                HashMap<DungeonCommand, Long> cooldowns = new HashMap<>();
                if (CMD_COOLDOWN.containsKey(((Player) sender).getUniqueId())) {
                    cooldowns = CMD_COOLDOWN.get(((Player) sender).getUniqueId());
                    if (cooldowns.containsKey(command)) {
                        if (System.currentTimeMillis() - cooldowns.get(command) < ((CommandCooldown) command).getCooldown()) {
                            sender.sendMessage(SUtil.translateColorWords(SwoftyDungeons.getPlugin().messages.getString("messages.command.cooldown").replace("$SECONDS", String.valueOf((double) (System.currentTimeMillis() - cooldowns.get(command)) / 1000))));
                            return false;
                        }
                    }
                }
                cooldowns.put(command, System.currentTimeMillis() + ((CommandCooldown) command).getCooldown());
                CMD_COOLDOWN.put(((Player) sender).getUniqueId(), cooldowns);
            }

            command.run(command.sender, args);
            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
            if (args.length <= 1) {
                List<String> list = new ArrayList<>();
                CommandLoader.commands.stream().forEach(entry -> list.add(entry.name));
                return list;
            } else {
                for (DungeonCommand dungeonCommand : CommandLoader.commands) {
                    if (dungeonCommand.name.equals(args[0]) || dungeonCommand.aliases.contains(args[0])) {
                        this.command = dungeonCommand;
                        return dungeonCommand.tabCompleters(sender, alias, args);
                    }
                }

                this.command = null;
                return new ArrayList<>();
            }
        }
    }

    public abstract List<String> tabCompleters(CommandSender sender, String alias, String[] args);

    public void send(String message, CommandSource sender) {
        sender.send(ChatColor.GRAY + message.replace("&", "§"));
    }

    public void send(String message) {
        send(SUtil.translateColorWords(message), sender);
    }

    public void send(List<String> message) {
        SUtil.translateColorWords(message).forEach(message2 -> {
            sender.send(message2);
        });
    }
}