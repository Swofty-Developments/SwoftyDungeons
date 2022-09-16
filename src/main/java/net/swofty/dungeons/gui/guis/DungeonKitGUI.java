package net.swofty.dungeons.gui.guis;

import lombok.SneakyThrows;
import net.swofty.dungeons.dungeon.Dungeon;
import net.swofty.dungeons.dungeon.DungeonRegistry;
import net.swofty.dungeons.gui.GUI;
import net.swofty.dungeons.gui.events.GUIOpenEvent;
import net.swofty.dungeons.gui.guiitem.GUIClickableItem;
import net.swofty.dungeons.gui.guiitem.GUIItem;
import net.swofty.dungeons.utilities.SUtil;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DungeonKitGUI extends GUI {

    private Dungeon dungeon;

    public DungeonKitGUI(Dungeon dungeon) {
        super("Dungeon Management", 54);
        this.dungeon = dungeon;
    }

    @Override
    public void onOpen(GUIOpenEvent e) {
        fill(SUtil.getStack("§e ", Material.BLACK_STAINED_GLASS_PANE, (short) 0, 1));
        set(GUIClickableItem.createGUIOpenerItem(new DungeonManagementGUI(dungeon), e.getPlayer(), "§aBack", 1, Material.ARROW, (short) 0));
        set(GUIClickableItem.getCloseItem(0));

        List<ItemStack> kit = dungeon.getKit();

        for (int i = 1; i < 37; i++) {
            int finalI = i;

            try {
                if (kit.get(i - 1) != null) {
                    List<ItemStack> finalStorage = kit;
                    set(new GUIItem() {
                        @Override
                        public int getSlot() {
                            return finalI + 8;
                        }

                        @Override
                        public ItemStack getItem() {
                            return finalStorage.get(finalI - 1);
                        }

                        @Override
                        public boolean canPickup() {
                            return true;
                        }
                    });
                } else {
                    set(new GUIItem() {
                        @Override
                        public int getSlot() {
                            return finalI + 8;
                        }

                        @Override
                        public ItemStack getItem() {
                            return new ItemStack(Material.AIR);
                        }

                        @Override
                        public boolean canPickup() {
                            return true;
                        }
                    });
                }
            } catch (Exception e2) {
                set(new GUIItem() {
                    @Override
                    public int getSlot() {
                        return finalI + 8;
                    }

                    @Override
                    public ItemStack getItem() {
                        return new ItemStack(Material.AIR);
                    }

                    @Override
                    public boolean canPickup() {
                        return true;
                    }
                });
            }
        }
    }

    @SneakyThrows
    @Override
    public void onClose(InventoryCloseEvent e) throws ExecutionException {
        List<ItemStack> storage = new ArrayList<>();
        if (storage != null)
            storage.clear();

        for (int i = 1; i < 37; i++) {
            storage.add(e.getInventory().getItem(i + 8));
        }

        dungeon.setKit(new ArrayList<>(storage));
        DungeonRegistry.updateDungeon(dungeon);
    }

}
