package net.swofty.dungeons;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.swofty.dungeons.command.CommandLoader;
import net.swofty.dungeons.command.DungeonCommand;
import net.swofty.dungeons.data.Config;
import net.swofty.dungeons.dungeon.DungeonRegistry;
import net.swofty.dungeons.dungeon.Spawner;
import net.swofty.dungeons.holograms.Hologram;
import net.swofty.dungeons.holograms.HologramManager;
import net.swofty.dungeons.listener.PListener;
import net.swofty.dungeons.placeholders.PlaceHolderHook;
import net.swofty.dungeons.sql.SQLDatabase;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
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
    @Getter
    public Config dungeons;
    @Getter
    public SQLDatabase sql;
    @Getter
    public Repeater repeater;

    static {
        ConfigurationSerialization.registerClass(Spawner.class, "spawner");
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        plugin = this;

        /**
         * Initialize configurations and database connections
         */
        config = new Config("config.yml");
        messages = new Config("messages.yml");
        dungeons = new Config("dungeons.yml");
        sql = new SQLDatabase();
        DungeonRegistry.loadFromConfig(dungeons);
        SUtil.setCachedHexColors();
        SwoftyDungeons.getPlugin().getSql().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `dungeon_sessions` (\n" +
                "\t`uuid` TEXT,\n" +
                "\t`dungeon` TEXT,\n" +
                "\t`timeSpent` INT(64),\n" +
                "\t`time` INT(64),\n" +
                "\t`entitiesKilled` INT(64),\n" +
                "\t`damageDealt` INT(64),\n" +
                "\t`damageRecieved` INT(64)\n" +
                ");").execute();

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
         * Handle holograms
         */
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(Hologram::handleRefreshment);
            }
        }.runTaskTimer(this, 0, 20);
        new BukkitRunnable() {
            @Override
            public void run() {
                HologramManager.runHologramLoop();
            }
        }.runTaskTimer(this, 10, 10);

        /**
         * Handle PlaceHolderAPI
         */
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderHook(this).register();
        }

        /**
         * Handle startup
         */
        repeater = new Repeater();
    }

    @Override
    public void onDisable() {
        repeater.stop();
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
