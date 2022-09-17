package net.swofty.dungeons.gui.guis;

import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.dungeon.Dungeon;
import net.swofty.dungeons.dungeon.DungeonSession;
import net.swofty.dungeons.gui.GUI;
import net.swofty.dungeons.gui.guiitem.GUIClickableItem;
import net.swofty.dungeons.gui.guiitem.GUIItem;
import net.swofty.dungeons.utilities.PaginationList;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlayerStatisticsGUI extends GUI {

    private static final int[] INTERIOR = new int[]{
            10, 11, 12, 13, 14, 15, 16
    };

    public Integer page;

    public PlayerStatisticsGUI(UUID uuid, Integer page) {
        super("", 27);
        PaginationList<DungeonSession> pagedSessions = new PaginationList<>(7);
        try {
            SwoftyDungeons.getPlugin().getSql().getSessions(uuid).forEach((dungeonSession, aLong) -> {
                pagedSessions.add(dungeonSession);
            });
        } catch (Exception e) {}

        this.title = "Player Information | Page " + page + "/" + pagedSessions.getPageCount();
        this.page = page;

        border(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        set(new GUIItem() {
            @Override
            public int getSlot() {
                return 4;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§aMenu Navigation", Material.WARPED_SIGN, (short) 0, 1, "§e", "§7This menu displays all of the", "§7active dungeons and the best time", "§7that the player you've selected has had", "§7on it.");
            }
        });

        set(GUIClickableItem.getCloseItem(22));

        try {
            List<DungeonSession> loc = pagedSessions.getPage(page);
            for (int i = 0; i < loc.size(); i++) {
                int slot = INTERIOR[i];
                DungeonSession mainSession = loc.get(i);
                int finalI = i;
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        Player player = (Player) e.getWhoClicked();
                        player.performCommand("dungeon info " + mainSession.dungeon);
                    }

                    @Override
                    public int getSlot() {
                        return slot;
                    }

                    @Override
                    public ItemStack getItem() {
                        return SUtil.getStack("§aDungeon Session #" + (((page - 1) * pagedSessions.getElementsPerPage()) + finalI + 1), Material.ENDER_PEARL, (short) 0, 1,
                                "§8" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(new Date(mainSession.getTimeStarted())),
                                " §e ",
                                "§7- §eDungeon§f: §d" + mainSession.getDungeon(),
                                "§7- §eTime Taken§f: §b" + new SimpleDateFormat("mm:ss.SSS").format(mainSession.getTimeSpent()),
                                "§7- §eEntities Killed§f: §b" + mainSession.getEntitiesKilled(),
                                "§7- §eDamage Dealt§f: §b" + mainSession.getDamageDealt(),
                                "§7- §eDamage Recieved§f: §b" + mainSession.getDamageRecieved(),
                                " §b ",
                                "§eClick to open dungeon top");
                    }
                });
            }

            if (page != pagedSessions.getPageCount()) {
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        new PlayerStatisticsGUI(uuid, page + 1).open((Player) e.getWhoClicked());
                    }

                    @Override
                    public int getSlot() {
                        return 23;
                    }

                    @Override
                    public ItemStack getItem() {
                        return SUtil.createNamedItemStack(Material.ARROW, ChatColor.GRAY + "->");
                    }
                });
            }
            if (page > 1) {
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        new PlayerStatisticsGUI(uuid, page - 1).open((Player) e.getWhoClicked());
                    }

                    @Override
                    public int getSlot() {
                        return 21;
                    }

                    @Override
                    public ItemStack getItem() {
                        return SUtil.createNamedItemStack(Material.ARROW, ChatColor.GRAY + "<-");
                    }
                });
            }
        } catch (Exception e) {
            fill(SUtil.createNamedItemStack(Material.RED_WOOL, "§cHas no previous sessions"), 11, 15);
        }
    }

    public PlayerStatisticsGUI(UUID uuid) {
        this(uuid,  1);
    }

    @Override
    public void onBottomClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }
}
