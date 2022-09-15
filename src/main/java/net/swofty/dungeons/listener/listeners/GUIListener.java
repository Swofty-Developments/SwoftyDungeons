package net.swofty.dungeons.listener.listeners;

import lombok.SneakyThrows;
import net.swofty.dungeons.gui.GUI;
import net.swofty.dungeons.gui.events.GUIOpenEvent;
import net.swofty.dungeons.gui.guiitem.GUIChatQueryItem;
import net.swofty.dungeons.gui.guiitem.GUIClickableItem;
import net.swofty.dungeons.gui.guiitem.GUIItem;
import net.swofty.dungeons.listener.PListener;
import net.swofty.dungeons.utilities.ChatQuery;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener extends PListener {

    public static final Map<UUID, GUIChatQueryItem> QUERY_MAP = new HashMap<>();
    public static final Map<UUID, ChatQuery> ALT_QUERY_MAP = new HashMap<>();
    private static final Map<UUID, Long> GUI_COOLDOWN = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD) || e.getAction().equals(InventoryAction.HOTBAR_SWAP))
            e.setCancelled(true);
        GUI gui = GUI.GUI_MAP.get(e.getWhoClicked().getUniqueId());
        if (gui == null) return;
        if (e.getClick().equals(ClickType.DOUBLE_CLICK))
            e.setCancelled(true);

        if (GUI_COOLDOWN.containsKey(e.getWhoClicked().getUniqueId()) && (System.currentTimeMillis() - GUI_COOLDOWN.get(e.getWhoClicked().getUniqueId()) < 100)) {
            e.setCancelled(true);
            e.getWhoClicked().sendMessage("§cYou must wait a bit before doing this!");
            return;
        } else {
            try {
                GUI_COOLDOWN.remove(e.getWhoClicked().getUniqueId());
            } catch (Exception e2) {
            }
            GUI_COOLDOWN.put(e.getWhoClicked().getUniqueId(), System.currentTimeMillis());
        }

        if (e.getClickedInventory() == e.getView().getTopInventory()) {
            int slot = e.getSlot();
            GUIItem item = gui.get(slot);
            if (item != null) {
                if (!item.canPickup())
                    e.setCancelled(true);
                if (item instanceof GUIClickableItem) {
                    GUIClickableItem clickable = (GUIClickableItem) item;
                    ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    clickable.run(e);
                }
                Player player = (Player) e.getWhoClicked();
                if (item instanceof GUIChatQueryItem) {
                    GUIChatQueryItem query = (GUIChatQueryItem) item;
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "Enter your query:");
                    if (!query.acceptRightClick() && e.getClick().equals(ClickType.RIGHT)) return;

                    QUERY_MAP.put(player.getUniqueId(), query);
                }
            }
        } else
            gui.onBottomClick(e);
        gui.update(e.getView().getTopInventory());
    }

    @EventHandler
    public void onGUIOpen(GUIOpenEvent e) {
        e.getOpened().onOpen(e);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(PlayerChatEvent e) {
        Player player = e.getPlayer();
        if ((!QUERY_MAP.containsKey(player.getUniqueId())) && (!ALT_QUERY_MAP.containsKey(player.getUniqueId()))) return;

        if (ALT_QUERY_MAP.containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            ChatQuery query = ALT_QUERY_MAP.get(player.getUniqueId());
            player.sendMessage("§aQuerying for '" + e.getMessage() + "'");
            query.getOnFinish().accept(e.getMessage());
            ALT_QUERY_MAP.remove(player.getUniqueId());
            return;
        }


        e.setCancelled(true);
        GUIChatQueryItem item = QUERY_MAP.get(player.getUniqueId());
        player.sendMessage(ChatColor.GOLD + "Querying for: " + e.getMessage());
        GUI next = item.onQueryFinish(e.getMessage());
        if (next != null)
            next.open(player);
        QUERY_MAP.remove(player.getUniqueId());
    }

    @SneakyThrows
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player player = (Player) e.getPlayer();
        GUI gui = GUI.GUI_MAP.get(player.getUniqueId());
        if (gui == null) return;
        gui.onClose(e);
        GUI.GUI_MAP.remove(player.getUniqueId());
    }
}