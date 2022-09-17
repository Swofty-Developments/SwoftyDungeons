package net.swofty.dungeons.gui.guis;

import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.dungeon.Dungeon;
import net.swofty.dungeons.gui.GUI;
import net.swofty.dungeons.gui.guiitem.GUIClickableItem;
import net.swofty.dungeons.gui.guiitem.GUIItem;
import net.swofty.dungeons.utilities.PaginationList;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DungeonStatisticsGUI extends GUI {

    private int page;
    private Dungeon dungeon;

    private static final int[] INTERIOR = new int[]{
            10, 11, 12, 13, 14, 15, 16
    };

    public DungeonStatisticsGUI(Dungeon dungeon, int page) {
        super("", 27);
        PaginationList<Map.Entry<UUID, Long>> pagedPositions = new PaginationList<>(7);
        try {
            SwoftyDungeons.getPlugin().getSql().getDungeonTop(dungeon.getName()).forEach(((uuid, aLong) -> pagedPositions.add(Map.entry(uuid, aLong))));
        } catch (Exception e) {}

        this.title = "Dungeon Leaderboards | Page " + page + "/" + pagedPositions.getPageCount();
        this.page = page;
        this.dungeon = dungeon;

        border(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        set(new GUIItem() {
            @Override
            public int getSlot() {
                return 4;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§aMenu Navigation", Material.WARPED_SIGN, (short) 0, 1, "§e", "§7This menu displays all of the leaderboard", "§7entries of the §f" + dungeon.getName() + " §7dungeon in pages.", "§7With pagination controls being found at the", "§7bottom of this GUI.");
            }
        });

        set(GUIClickableItem.getCloseItem(22));

        try {
            List<Map.Entry<UUID, Long>> loc = pagedPositions.getPage(page);
            for (int i = 0; i < loc.size(); i++) {
                int slot = INTERIOR[i];
                Map.Entry<UUID, Long> position = loc.get(i);
                int finalI = i;
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        Player player = (Player) e.getWhoClicked();
                        player.performCommand("dungeon stats " + position.getKey().toString());
                    }

                    @Override
                    public int getSlot() {
                        return slot;
                    }

                    @Override
                    public ItemStack getItem() {
                        return SUtil.getStack("§a#" + (((page - 1) * pagedPositions.getElementsPerPage()) + finalI + 1) + " §8- §d" + Bukkit.getOfflinePlayer(position.getKey()).getName(), Material.ENDER_PEARL, (short) 0, 1, "§bTime: " + new SimpleDateFormat("mm:ss.SSS").format(position.getValue()), "§e ", "§eClick to open stats");
                    }
                });
            }

            if (page != pagedPositions.getPageCount()) {
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        new DungeonStatisticsGUI(dungeon, page + 1).open((Player) e.getWhoClicked());
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
                        new DungeonStatisticsGUI(dungeon, page - 1).open((Player) e.getWhoClicked());
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
            fill(SUtil.createNamedItemStack(Material.RED_WOOL, "§cNo Entries"), 11, 15);
        }
    }

    public DungeonStatisticsGUI(Dungeon dungeon) {
        this(dungeon, 1);
    }

    @Override
    public void onBottomClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }
}
