package net.swofty.dungeons.utilities;

import lombok.Getter;
import net.swofty.dungeons.listener.listeners.GUIListener;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Getter
public class ChatQuery
{
    private final Player player;
    private final Consumer<String> onFinish;

    public ChatQuery(Player player, Consumer<String> onFinish) {
        this.player = player;
        this.onFinish = onFinish;
    }

    public void show() {
        this.player.sendMessage("§e§lEnter your query in chat:");
        this.player.closeInventory();
        GUIListener.ALT_QUERY_MAP.put(player.getUniqueId(), this);
    }
}
