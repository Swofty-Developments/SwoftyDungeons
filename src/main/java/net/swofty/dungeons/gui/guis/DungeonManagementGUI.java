package net.swofty.dungeons.gui.guis;

import net.swofty.dungeons.dungeon.Dungeon;
import net.swofty.dungeons.dungeon.DungeonHandler;
import net.swofty.dungeons.dungeon.DungeonRegistry;
import net.swofty.dungeons.dungeon.Spawner;
import net.swofty.dungeons.gui.GUI;
import net.swofty.dungeons.gui.guiitem.GUIClickableItem;
import net.swofty.dungeons.gui.guiitem.GUIItem;
import net.swofty.dungeons.utilities.ChatQuery;
import net.swofty.dungeons.utilities.PaginationList;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DungeonManagementGUI extends GUI {

    private int page;
    private Dungeon dungeon;

    private static final int[] INTERIOR = new int[]{
            19, 20, 21,
            28, 29, 30
    };

    private static final int[] INTERIOR_2 = new int[]{
            23, 24, 25,
            32, 33, 34
    };

    enum GUIType {
        NORMAL(),
        SPAWN_CONDITIONS()
    }

    public DungeonManagementGUI(Dungeon dungeon, int page, GUIType guiType, int spawnerNo) {
        super("", 54);
        PaginationList<Map.Entry<Integer, Spawner>> pagedSpawners = new PaginationList<>(6);
        try {
            pagedSpawners.addAll(dungeon.getSpawners().entrySet());
        } catch (Exception e) {}

        this.title = "Dungeon Management Menu";
        this.page = page;
        this.dungeon = dungeon;

        fill(SUtil.getStack("§e ", Material.BLACK_STAINED_GLASS_PANE, (short) 0, 1));
        fill(SUtil.getStack("§e ", Material.GREEN_STAINED_GLASS_PANE, (short) 0, 1), 0, 8);
        fill(SUtil.getStack("§e ", Material.GREEN_STAINED_GLASS_PANE, (short) 0, 1), 45, 53);

        if (dungeon.getFinished()) {
            dungeon.setFinished(false);
            DungeonRegistry.updateDungeon(dungeon);
            DungeonHandler.dungeonEdit(dungeon);
        }

        boolean atleastOneParkour = dungeon.getSpawners() != null && !dungeon.getSpawners().isEmpty() && dungeon.getSpawners().size() > 0;
        boolean validSpawnAmount = true;
        for (Map.Entry<Integer, Spawner> entry : dungeon.getSpawners().entrySet()) {
            try {
                entry.getValue().convertSpawnAmount();
            } catch (Exception e) {
                validSpawnAmount = false;
                break;
            }
        }
        boolean validSpawnRequirements = true;
        for (Map.Entry<Integer, Spawner> entry : dungeon.getSpawners().entrySet()) {
            try {
                entry.getValue().convertSpawnConditions();
            } catch (Exception e) {
                validSpawnRequirements = false;
                break;
            }
        }

        boolean finalValidSpawnRequirements = validSpawnRequirements;
        boolean finalValidSpawnAmount = validSpawnAmount;
        set(new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                if (!finalValidSpawnRequirements || !finalValidSpawnAmount || !atleastOneParkour) {
                    e.getWhoClicked().sendMessage("§cYou must meet all requirements before finalizing the dungeon");
                    return;
                }

                dungeon.setFinished(true);
                DungeonRegistry.updateDungeon(dungeon);

                e.getWhoClicked().closeInventory();

                e.getWhoClicked().sendMessage("§aYou have successfully published the dungeon §e" + dungeon.getName());
                e.getWhoClicked().sendMessage("§aNote that opening the edit menu again will unpublish the dungeon");
                e.getWhoClicked().sendMessage("§bPlayers can now run §7/dungeon start " + dungeon.getName());
            }

            @Override
            public int getSlot() {
                    return 53;
                }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§aPublish Dungeon", Material.LIME_STAINED_GLASS_PANE, (short) 0, 1, "§7Click this button to either publish or", "§7unpublish the dungeon for general players.", "§e", "§fRequirements for finalization",
                        "§8- §7All spawners must contain valid spawn requirements  " + (finalValidSpawnRequirements ? "§a§lCOMPLETE" : "§c§lINCOMPLETE"),
                        "§8- §7All spawners must contain valid spawn amount  " + (finalValidSpawnAmount ? "§a§lCOMPLETE" : "§c§lINCOMPLETE"),
                        "§8- §7There must be atleast one spawner  " + (atleastOneParkour ? "§a§lCOMPLETE" : "§c§lINCOMPLETE"),
                        "§b",
                        "§eClick to publish");
            }
        });

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

        if (dungeon.getTop() != null) {
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    Location loc = e.getWhoClicked().getLocation();
                    dungeon.setTop(new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5));
                    DungeonRegistry.updateDungeon(dungeon);
                    e.getWhoClicked().sendMessage("§aSuccessfully §eSET §adungeon leaderboard to your location");

                    e.getWhoClicked().closeInventory();
                    new DungeonManagementGUI(dungeon, page, guiType, spawnerNo);
                }

                @Override
                public int getSlot() {
                    return 23;
                }

                @Override
                public ItemStack getItem() {
                    return SUtil.getStack("§eSet Top", Material.SPRUCE_SIGN, (short) 0, 1, "§fX: " + dungeon.getTop().getBlockX() + " Y: " + dungeon.getTop().getBlockY() + " Z: " + dungeon.getTop().getBlockZ(),
                            "§b",
                            "§eClick to set");
                }
            });
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    dungeon.setTop(null);
                    DungeonRegistry.updateDungeon(dungeon);
                    e.getWhoClicked().sendMessage("§aSuccessfully §cREMOVED §adungeon leaderboard from its location");

                    e.getWhoClicked().closeInventory();
                    new DungeonManagementGUI(dungeon, page, guiType, spawnerNo);
                }

                @Override
                public int getSlot() {
                    return 24;
                }

                @Override
                public ItemStack getItem() {
                    return SUtil.getStack("§cRemove Top", Material.CRIMSON_SIGN, (short) 0, 1, "§fX: " + dungeon.getTop().getBlockX() + " Y: " + dungeon.getTop().getBlockY() + " Z: " + dungeon.getTop().getBlockZ(),
                            "§b",
                            "§cClick to remove");
                }
            });
        } else {
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    Location loc = e.getWhoClicked().getLocation();
                    dungeon.setTop(new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5));
                    DungeonRegistry.updateDungeon(dungeon);
                    e.getWhoClicked().sendMessage("§aSuccessfully §eSET §adungeon leaderboard to your location");

                    e.getWhoClicked().closeInventory();
                    new DungeonManagementGUI(dungeon, page, guiType, spawnerNo);
                }

                @Override
                public int getSlot() {
                    return 23;
                }

                @Override
                public ItemStack getItem() {
                    return SUtil.getStack("§eSet Top", Material.SPRUCE_SIGN, (short) 0, 1, "§fX: §cN/A §fY: §cN/A §fZ: §cN/A",
                            "§b",
                            "§eClick to set");
                }
            });
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    dungeon.setTop(null);
                    DungeonRegistry.updateDungeon(dungeon);
                    e.getWhoClicked().sendMessage("§aSuccessfully §cREMOVED §adungeon leaderboard from its location");

                    e.getWhoClicked().closeInventory();
                    new DungeonManagementGUI(dungeon, page, guiType, spawnerNo);
                }

                @Override
                public int getSlot() {
                    return 24;
                }

                @Override
                public ItemStack getItem() {
                    return SUtil.getStack("§cRemove Top", Material.CRIMSON_SIGN, (short) 0, 1, "§fX: §cN/A §fY: §cN/A §fZ: §cN/A",
                            "§b",
                            "§cClick to remove");
                }
            });
        }

        set(new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.getWhoClicked().closeInventory();
                new DungeonKitGUI(dungeon).open(e.getWhoClicked());
            }

            @Override
            public int getSlot() {
                return 25;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§aKit Editor", Material.IRON_SWORD, (short) 0, 1, "§7Set up the kit that is given", "§7when a player joins the dungeon.", "§b",
                        "§eClick to open editor");
            }
        });
        set(new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                DungeonHandler.dungeonDelete(dungeon);
                DungeonRegistry.removeDungeon(dungeon.getName());
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage("§aSuccessfully deleted the dungeon §e" + dungeon.getName());
            }

            @Override
            public int getSlot() {
                return 33;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§cDelete Dungeon", Material.RED_BANNER, (short) 0, 1, "§7Note this will clear all the dungeon", "§7sessions on this dungeon.",
                        "§b",
                        "§eClick to delete");
            }
        });
        set(new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.getWhoClicked().closeInventory();

                dungeon.setSpawnLocation(e.getWhoClicked().getLocation());
                DungeonRegistry.updateDungeon(dungeon);

                new DungeonManagementGUI(dungeon, page, guiType, spawnerNo);
            }

            @Override
            public int getSlot() {
                return 32;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§eUpdate Location", Material.AZALEA, (short) 0, 1,
                        "§fX: " + dungeon.getSpawnLocation().getBlockX() + " Y: " + dungeon.getSpawnLocation().getBlockY() + " Z: " + dungeon.getSpawnLocation().getBlockZ(),
                        "§b",
                        "§eClick to update");
            }
        });
        set(GUIClickableItem.getCloseItem(49));

        switch (guiType) {
            case NORMAL -> {
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
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        HashMap<Integer, Spawner> spawner = dungeon.getSpawners();
                        spawner.put(spawner.size(), new Spawner(e.getWhoClicked().getLocation()));
                        dungeon.setSpawners(spawner);
                        DungeonRegistry.updateDungeon(dungeon);

                        e.getWhoClicked().closeInventory();
                        new DungeonManagementGUI(dungeon, page, guiType).open(e.getWhoClicked());
                    }

                    @Override
                    public int getSlot() {
                        return 38;
                    }

                    @Override
                    public ItemStack getItem() {
                        return SUtil.getStack("§aCreate New Spawner", Material.GREEN_CANDLE, (short) 0, 1, "§7Note that this will set the location to where", "§7you are currently standing.", "§e", "§eClick to create a new spawner");
                    }
                });

                List<Map.Entry<Integer, Spawner>> spawners = pagedSpawners.getPage(page);
                if (spawners != null) {
                    for (int i = 0; i < spawners.size(); i++) {
                        int slot = INTERIOR[i];
                        Map.Entry<Integer, Spawner> spawner = spawners.get(i);
                        int finalI = i;
                        set(new GUIClickableItem() {
                            @Override
                            public void run(InventoryClickEvent e) {
                                Player player = (Player) e.getWhoClicked();
                                ClickType click = e.getClick();

                                if (click.isShiftClick()) {
                                    if (click.isRightClick()) {
                                        e.getWhoClicked().closeInventory();
                                        new DungeonManagementGUI(dungeon, page, GUIType.SPAWN_CONDITIONS, finalI).open(player);
                                    } else {
                                        HashMap<Integer, Spawner> spawner2 = dungeon.getSpawners();
                                        spawner2.remove(spawner.getKey());

                                        HashMap<Integer, Spawner> toSet = new HashMap<>();
                                        AtomicInteger i = new AtomicInteger();
                                        spawner2.forEach((key, value) -> {
                                            toSet.put(i.get(), value);
                                            i.getAndIncrement();
                                        });

                                        dungeon.setSpawners(toSet);
                                        DungeonRegistry.updateDungeon(dungeon);

                                        e.getWhoClicked().closeInventory();
                                        new DungeonManagementGUI(dungeon, page, guiType).open(e.getWhoClicked());
                                    }
                                } else {
                                    if (click.isRightClick()) {
                                        HashMap<Integer, Spawner> spawner2 = dungeon.getSpawners();
                                        spawner2.remove(spawner.getKey());
                                        spawner.getValue().setLocation(e.getWhoClicked().getLocation());
                                        spawner2.put(spawner.getKey(), spawner.getValue());
                                        dungeon.setSpawners(spawner2);
                                        DungeonRegistry.updateDungeon(dungeon);

                                        e.getWhoClicked().closeInventory();
                                        new DungeonManagementGUI(dungeon, page, guiType).open(e.getWhoClicked());
                                    } else {

                                        e.getWhoClicked().closeInventory();
                                        e.getWhoClicked().sendMessage("§8>-- §aGuide on setting spawn amount §8--<");
                                        e.getWhoClicked().sendMessage("§7Format: {Amount to spawn};{How many seconds in between each spawn};{Entity Type}");
                                        e.getWhoClicked().sendMessage("§fAn example of a valid spawn amount would be if you entered §710;20;ZOMBIE§f, which means that 10 zombies will spawn every 20 seconds. A full list of entity types can be found here; §bhttps://www.spigotmc.org/wiki/entitytypes/");

                                        new ChatQuery((Player) e.getWhoClicked(), input -> {
                                            Spawner fakeSpawner = new Spawner(e.getWhoClicked().getLocation());
                                            fakeSpawner.setSpawnAmount(input);

                                            try {
                                                fakeSpawner.convertSpawnAmount();

                                                HashMap<Integer, Spawner> spawners = dungeon.getSpawners();
                                                Spawner spawner = dungeon.getSpawners().get(finalI);
                                                spawner.setSpawnAmount(input);

                                                spawners.put(finalI, spawner);
                                                e.getWhoClicked().closeInventory();
                                                new DungeonManagementGUI(dungeon, page, guiType).open(e.getWhoClicked());
                                                dungeon.setSpawners(spawners);
                                                DungeonRegistry.updateDungeon(dungeon);
                                            } catch (Exception e2) {
                                                e.getWhoClicked().sendMessage("§cYour input, '§e" + input + "§c' is not valid. Please read the above guide on how to use the spawn amount feature.");
                                            }
                                        }).show();

                                        e.getWhoClicked().closeInventory();
                                    }
                                }
                            }

                            @Override
                            public int getSlot() {
                                return slot;
                            }

                            @Override
                            public ItemStack getItem() {
                                return SUtil.getStack("§aSpawner #" + spawner.getKey(), Material.SPAWNER, (short) 0, 1, "§fX: " + spawner.getValue().getLocation().getBlockX() + " Y: " + spawner.getValue().getLocation().getBlockY() + " Z: " + spawner.getValue().getLocation().getBlockZ(),
                                        "§e ",
                                        "§dSpawn Conditions: §7" + (spawner.getValue().getSpawnConditions().isEmpty() ? "§cNot Set" : spawner.getValue().getSpawnConditions()),
                                        "§dSpawn Amount: §7" + (spawner.getValue().getSpawnAmount().equals("") ? "§cNot Set" : spawner.getValue().getSpawnAmount()),
                                        "§b ",
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
                            new DungeonManagementGUI(dungeon, page + 1, guiType).open((Player) e.getWhoClicked());
                        }

                        @Override
                        public int getSlot() {
                            return 39;
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
                            new DungeonManagementGUI(dungeon, page - 1, guiType).open((Player) e.getWhoClicked());
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
            case SPAWN_CONDITIONS -> {
                for (int i = 0; i < INTERIOR.length; i++) {
                    int finalI = i;
                    set(new GUIItem() {
                        @Override
                        public int getSlot() {
                            return INTERIOR[finalI];
                        }

                        @Override
                        public ItemStack getItem() {
                            return SUtil.getStack("§cEmpty Slot", Material.PURPLE_STAINED_GLASS_PANE, (short) 0, 1, "§7Click on the blue candle below to add new spawn condition");
                        }
                    });
                }
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        new DungeonManagementGUI(dungeon, page, GUIType.NORMAL).open((Player) e.getWhoClicked());
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
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        HashMap<Integer, Spawner> spawners = dungeon.getSpawners();
                        if (spawners.size() == 6) {
                            e.getWhoClicked().sendMessage("§cYou can only have a maximum of 6 spawners");
                            return;
                        }

                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage("§8>-- §aGuide on setting spawn condition §8--<");
                        e.getWhoClicked().sendMessage("§fValid spawn conditions are made up of two sections, the spawn condition type and the actual value for the condition to be met. Here are a full list of conditions;");
                        e.getWhoClicked().sendMessage("§7- §eMINIMUM_KILLS");
                        e.getWhoClicked().sendMessage("§7- §eMINIMUM_TIME");
                        e.getWhoClicked().sendMessage("§7- §eHEALTH_ABOVE");
                        e.getWhoClicked().sendMessage("§7- §eHEALTH_BELOW");
                        e.getWhoClicked().sendMessage("§7Format: {Condition Type};{Number relating to condition}");
                        e.getWhoClicked().sendMessage("§fAn example of a valid spawn condition would be if you entered §7MINIMUM_KILLS;50§f, which means that the spawner will only activate after the player has a minimum of 50 kills. The process is the same for all other condition types.");

                        new ChatQuery((Player) e.getWhoClicked(), input -> {
                            Spawner fakeSpawner = new Spawner(e.getWhoClicked().getLocation());

                            List<String> fakeConditions = fakeSpawner.getSpawnConditions();
                            fakeConditions.add(input);
                            fakeSpawner.setSpawnConditions(fakeConditions);

                            try {
                                fakeSpawner.convertSpawnConditions();

                                Spawner spawner = dungeon.getSpawners().get(spawnerNo);
                                List<String> conditions = spawner.getSpawnConditions();
                                conditions.add(input);
                                spawner.setSpawnConditions(conditions);

                                spawners.put(spawnerNo, spawner);
                                e.getWhoClicked().closeInventory();
                                new DungeonManagementGUI(dungeon, page, guiType).open(e.getWhoClicked());
                                dungeon.setSpawners(spawners);
                                DungeonRegistry.updateDungeon(dungeon);
                            } catch (Exception e2) {
                                e.getWhoClicked().sendMessage("§cYour input, '§e" + input + "§c' is not valid. Please read the above guide on how to use the spawn condition feature.");
                            }
                        }).show();

                        e.getWhoClicked().closeInventory();
                    }

                    @Override
                    public int getSlot() {
                        return 38;
                    }

                    @Override
                    public ItemStack getItem() {
                        return SUtil.getStack("§aCreate New Spawn Condition", Material.BLUE_CANDLE, (short) 0, 1, "§e", "§eClick to create a new spawn condition");
                    }
                });

                Spawner spawner = dungeon.getSpawners().get(spawnerNo);
                List<String> conditions = spawner.getSpawnConditions();
                for (int i = 0; i < conditions.size(); i++) {
                    int slot = INTERIOR[i];
                    String condition = conditions.get(i);
                    int finalI = i;
                    set(new GUIClickableItem() {
                        @Override
                        public int getSlot() {
                            return slot;
                        }

                        @Override
                        public ItemStack getItem() {
                            return SUtil.getStack("§aSpawn Condition #" + finalI, Material.COBWEB, (short) 0, 1,
                                    "§7Spawn Condition: " + condition,
                                    "§e ",
                                    "§eClick to remove"
                                    );
                        }

                        @Override
                        public void run(InventoryClickEvent e) {
                            conditions.remove(condition);
                            spawner.setSpawnConditions(conditions);

                            HashMap<Integer, Spawner> spawner2 = dungeon.getSpawners();
                            spawner2.remove(spawnerNo);
                            spawner2.put(spawnerNo, spawner);

                            dungeon.setSpawners(spawner2);
                            DungeonRegistry.updateDungeon(dungeon);

                            e.getWhoClicked().closeInventory();
                            new DungeonManagementGUI(dungeon, page, guiType).open(e.getWhoClicked());
                        }
                    });
                }
            }
        }
    }

    public DungeonManagementGUI(Dungeon dungeon) {
        this(dungeon, 1, GUIType.NORMAL);
    }

    public DungeonManagementGUI(Dungeon dungeon, GUIType type) {
        this(dungeon, 1, type);
    }

    public DungeonManagementGUI(Dungeon dungeon, int page, GUIType guiType) {
        this(dungeon, page, guiType, 0);
    }

    @Override
    public void onBottomClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }
}
