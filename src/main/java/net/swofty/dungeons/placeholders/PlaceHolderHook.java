package net.swofty.dungeons.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.swofty.dungeons.SwoftyDungeons;
import org.bukkit.entity.Player;

public class PlaceHolderHook extends PlaceholderExpansion {

    private final SwoftyDungeons plugin;

    public PlaceHolderHook(SwoftyDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "Swofty";
    }

    @Override
    public String getIdentifier() {
        return "dungeons";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        switch (params) {
            case "overall_kills" -> {
                return null;
            }
        }

        return null;
    }
}