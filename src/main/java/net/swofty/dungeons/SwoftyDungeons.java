package net.swofty.dungeons;

import lombok.Getter;
import net.swofty.dungeons.command.CommandLoader;
import net.swofty.dungeons.command.DungeonCommand;
import net.swofty.dungeons.data.Config;
import net.swofty.dungeons.listener.PListener;
import net.swofty.dungeons.placeholders.PlaceHolderHook;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Field;

public final class SwoftyDungeons extends JavaPlugin {

    @Getter
    private static SwoftyDungeons plugin;
    public CommandLoader cl;
    public CommandMap commandMap;
    @Getter
    public Config config;
    @Getter
    public Config messages;

    @Override
    public void onEnable() {
        plugin = this;

        /**
         * Initialize configurations and database connections
         */
        config = new Config("config.yml");
        messages = new Config("messages.yml");
        SUtil.setCachedHexColors();

        /**
         * Initialize commands and register them
         */
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        cl = new CommandLoader();
        loadCommands();

        /**
         * Initialize plugin listeners
         */
        loadListeners();

        /**
         * Handle PlaceHolderAPI
         */
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderHook(this).register();
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    private void loadCommands() {
        DungeonCommand.register();

        Reflections reflection = new Reflections("net.swofty.dungeons.command.subtypes");
        for(Class<? extends DungeonCommand> l:reflection.getSubTypesOf(DungeonCommand.class)) {
            try {
                DungeonCommand command = l.newInstance();
                cl.register(command);
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadListeners() {
        Reflections reflection = new Reflections("net.swofty.dungeons.listener.listeners");
        for(Class<? extends PListener> l:reflection.getSubTypesOf(PListener.class)) {
            try {
                PListener clazz = l.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }
}
