package net.swofty.dungeons.gui.guis;

import net.swofty.dungeons.dungeon.Dungeon;
import net.swofty.dungeons.dungeon.DungeonRegistry;
import net.swofty.dungeons.dungeon.Spawner;
import net.swofty.dungeons.gui.GUI;
import net.swofty.dungeons.gui.guiitem.GUIClickableItem;
import net.swofty.dungeons.gui.guiitem.GUIItem;
import net.swofty.dungeons.utilities.PaginationList;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class DungeonManagementGUI extends GUI {

    private int page;
    private Dungeon dungeon;

    private static final int[] INTERIOR = new int[]{
            19, 20, 21,
            28, 29, 30
    };

    public DungeonManagementGUI(Dungeon dungeon, int page) {
        super("", 54);
        PaginationList<Map.Entry<String, Spawner>> pagedSpawners = new PaginationList<>(6);
        try {
            pagedSpawners.addAll(dungeon.getSpawners().entrySet());
        } catch (Exception e) {}

        this.title = "Dungeon Management Menu";
        this.page = page;
        this.dungeon = dungeon;

        fill(SUtil.getStack("", Material.BLACK_STAINED_GLASS_PANE, (short) 0, 1));
        fill(SUtil.getStack("", Material.GREEN_STAINED_GLASS_PANE, (short) 0, 1), 0, 8);
        fill(SUtil.getStack("", Material.GREEN_STAINED_GLASS_PANE, (short) 0, 1), 45, 53);

        if (dungeon.getFinished()) {
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    dungeon.setFinished(false);
                    DungeonRegistry.updateDungeon(dungeon);

                    e.getWhoClicked().closeInventory();
                    new DungeonManagementGUI(dungeon).open(e.getWhoClicked());
                }

                @Override
                public int getSlot() {
                    return 54;
                }

                @Override
                public ItemStack getItem() {
                    return SUtil.getStack("§aPublish Dungeon", Material.LIME_STAINED_GLASS_PANE, (short) 0, 1, "§7Click this button to either publish or", "§7unpublish the dungeon for general players.", "§e", "§fRequirements for finalization",
                            "§8- §7All spawners must contain valid spawn requirements  §a§lCOMPLETE",
                            "§8- §7All spawners must contain valid spawn amount  §a§lCOMPLETE",
                            "§8- §7There must be atleast one spawner  §a§lCOMPLETE",
                            "§b",
                            "§eClick to unfinalize");
                }
            });
        } else {
            Boolean atleastOneParkour = !dungeon.getSpawners().isEmpty() && dungeon.getSpawners().size() > 0;
            Boolean validSpawnAmount = true;
            for (Map.Entry<String, Spawner> entry : dungeon.getSpawners().entrySet()) {
                Spawner value = entry.getValue();
                if (value.spawnAmount == null) {
                    validSpawnAmount = false;
                    break;
                }
            }

        }

        set(new GUIItem() {
            @Override
            public int getSlot() {
                return 4;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§aParkour Management for §e" + dungeon.getName(), Material.ENDER_PEARL, (short) 0, 1, "§e", "§7This menu displays all of the configurable", "§7options for the §f" + dungeon.getName() + " §7dungeon in pages.", "§7With pagination controls being found at the", "§7bottom of this GUI.", "§e ", "§7To the left of the GUI is the list of spawners", "§7whereas to the right is the dungeon management panel.");
            }
        });

        set(GUIClickableItem.getCloseItem(49));

        for (int i = 0; i < INTERIOR.length; i++) {
            int finalI = i;
            set(new GUIItem() {
                @Override
                public int getSlot() {
                    return INTERIOR[finalI];
                }

                @Override
                public ItemStack getItem() {
                    return SUtil.getStack("§cEmpty Slot", Material.RED_STAINED_GLASS_PANE, (short) 0, 1, "§7Click on the green candle below to add new spawner");
                }
            });
        }
        set(new GUIItem() {
            @Override
            public int getSlot() {
                return 38;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§aCreate New Spawner", Material.GREEN_CANDLE, (short) 0, 1, "§7Note that this will set the location to where", "§7you are currently standing.", "§e", "§eClick to create a new spawner");
            }
        });

        List<Map.Entry<String, Spawner>> spawners = pagedSpawners.getPage(page);
        if (spawners != null) {
            for (int i = 0; i < spawners.size(); i++) {
                int slot = INTERIOR[i];
                Map.Entry<String, Spawner> spawner = spawners.get(i);
                int finalI = i;
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        Player player = (Player) e.getWhoClicked();
                        ClickType click = e.getClick();

                        if (click.isShiftClick()) {
                            if (click.isRightClick()) {


                            } else {


                            }
                        } else {
                            if (click.isRightClick()) {


                            } else {


                            }
                        }
                    }

                    @Override
                    public int getSlot() {
                        return slot;
                    }

                    @Override
                    public ItemStack getItem() {
                        return SUtil.getStack("§aSpawner #" + (((page - 1) * pagedSpawners.getElementsPerPage()) + finalI + 1), Material.SPAWNER, (short) 0, 1, "§fX: " + spawner.getValue().getLocation().getBlockX() + " Y: " + spawner.getValue().getLocation().getBlockY() + " Z: " + spawner.getValue().getLocation().getBlockZ(), "§e ",
                                "§bRight-Click to set location",
                                "§bShift-Right-Click to set spawn conditions",
                                "§eLeft-Click to set spawn amount",
                                "§eShift-Left-Click to delete"
                        );
                    }
                });
            }
        }

        if (page != pagedSpawners.getPageCount() && pagedSpawners.getPage(page) != null) {
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    new DungeonManagementGUI(dungeon, page + 1).open((Player) e.getWhoClicked());
                }

                @Override
                public int getSlot() {
                        return 39;
                    }

                @Override public ItemStack getItem() {
                    return SUtil.createNamedItemStack(Material.ARROW, ChatColor.GRAY + "->");
                }
            });
        }
        if (page > 1) {
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    new DungeonManagementGUI(dungeon, page - 1).open((Player) e.getWhoClicked());
                }

                @Override
                public int getSlot() {
                        return 37;
                    }

                @Override
                public ItemStack getItem() {
                    return SUtil.createNamedItemStack(Material.ARROW, ChatColor.GRAY + "<-");
                }
            });
        }
    }

    public DungeonManagementGUI(Dungeon dungeon) {
        this(dungeon, 1);
    }

    @Override
    public void onBottomClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }
}
