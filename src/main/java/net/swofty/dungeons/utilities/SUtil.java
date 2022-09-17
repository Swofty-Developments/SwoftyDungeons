package net.swofty.dungeons.utilities;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.data.Config;
import net.swofty.dungeons.dungeon.DungeonSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SUtil {

    public static HashMap<String, ChatColor> cachedHexColors = new HashMap<>();

    public static void setCachedHexColors() {
        Config config = SwoftyDungeons.getPlugin().getMessages();

        config.getConfigurationSection("hex-colors").getKeys(true).forEach(key -> {
            cachedHexColors.put("$" + key, net.md_5.bungee.api.ChatColor.of(config.getConfigurationSection("hex-colors").getString(key)));
        });
    }

    public static ItemStack getSkull(String playerName, ItemStack stack) {
        Location loc = new Location(Bukkit.getServer().getWorlds().get(0), 1000, 200, 1000);

        loc.getBlock().setType(Material.PLAYER_HEAD);
        Skull s = (Skull) loc.getBlock().getState();
        s.setOwner(playerName);
        s.update();

        ItemStack skull1 = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sk1 = (SkullMeta)skull1.getItemMeta();
        sk1.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
        skull1.setItemMeta(sk1);
        return stack;
    }

    public static ItemStack getStack(String name, Material material, short data, int amount, String... lore) {
        return getStack(name, material, data, amount, Arrays.asList(lore));
    }

    public static BaseComponent[] combine(BaseComponent[] one, BaseComponent[] two) {
        BaseComponent[] combined = new BaseComponent[one.length + two.length];
        System.arraycopy(one, 0, combined, 0, one.length);
        System.arraycopy(two, 0, combined, one.length, two.length);
        return combined;
    }

    public static ItemStack getStack(String name, Material material, short data, int amount, String l) {
        List<String> lore = new ArrayList<>();
        for (String string : SUtil.splitByWordAndLength(l, 30, "\\s"))
            lore.add(org.bukkit.ChatColor.GRAY + string);

        return getStack(name, material, data, amount, lore);
    }

    public static ItemStack getStack(String name, Material material, short data, int amount, List<String> lore) {
        ItemStack stack = new ItemStack(material, data);
        stack.setDurability(data);
        ItemMeta meta = stack.getItemMeta();
        if (name != null) {
            if (name.contains("glowing:"))
                meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            meta.setDisplayName(name.replaceAll("glowing:", ""));
        }
        stack.setAmount(amount);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }

    public static void delay(Runnable runnable, long delay) {
        new BukkitRunnable() {
            public void run() {
                runnable.run();
            }
        }.runTaskLater(SwoftyDungeons.getPlugin(), delay);
    }

    public static ItemStack getSkullStack(String name, String skullName, int amount, String... lore) {
        ItemStack stack = getStack(name, Material.PLAYER_HEAD, (short) 3, amount, lore);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(skullName);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack createNamedItemStack(Material material, String name) {
        ItemStack stack = new ItemStack(material);
        if (name != null) {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(name);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public static void runBukkitAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(SwoftyDungeons.getPlugin(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        new Thread(runnable).start();
//            Bukkit.getScheduler().runTaskAsynchronously(SkyBlock.getPlugin(), runnable);
    }

    public static void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(SwoftyDungeons.getPlugin(), runnable);
    }

    public static ItemStack getSingleLoreStack(String name, Material material, short data, int amount, String lore) {
        List<String> l = new ArrayList<>();
        for (String line : SUtil.splitByWordAndLength(lore, 30, "\\s"))
            l.add(org.bukkit.ChatColor.GRAY + line);
        return getStack(name, material, data, amount, l.toArray(new String[]{}));
    }

    public static List<String> splitByWordAndLength(String string, int splitLength, String separator) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\G" + separator + "*(.{1," + splitLength + "})(?=\\s|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(string);
        while (matcher.find())
            result.add(matcher.group(1));
        return result;
    }

    public static List<String> variableize(List<String> list, List<Map.Entry<String, String>> entries) {
        entries.forEach(entry -> {
            list.replaceAll(s -> s.replace(entry.getKey(), entry.getValue()));
        });
        return list;
    }

    public static String variableize(String str, List<Map.Entry<String, String>> entries) {
        for (Map.Entry<String, String> entry : entries) {
            str = str.replace(entry.getKey(), entry.getValue());
        }
        return str;
    }

    public static void sendMessageList(Player player, List<String> list) {
        SUtil.translateColorWords(list).forEach(player::sendMessage);
    }

    public static HashMap<UUID, Long> sortByValue(HashMap<UUID, Long> hm) {
        LinkedHashMap<UUID, Long> reverseSortedMap = new LinkedHashMap<>();

        hm.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        return reverseSortedMap;
    }

    public static List<String> translateColorWords(List<String> s) {
        s.replaceAll(string -> string
                .replace("%%black%%", "§0")
                .replace("%%dark-blue%%", "§1")
                .replace("%%dark-green%%", "§2")
                .replace("%%dark-aqua%%", "§3")
                .replace("%%dark-red%%", "§4")
                .replace("%%purple%%", "§5")
                .replace("%%gold%%", "§6")
                .replace("%%gray%%", "§7")
                .replace("%%dark-gray%%", "§8")
                .replace("%%blue%%", "§9")
                .replace("%%green%%", "§a")
                .replace("%%aqua%%", "§b")
                .replace("%%red%%", "§c")
                .replace("%%pink%%", "§d")
                .replace("%%yellow%%", "§e")
                .replace("%%white%%", "§f")
                .replace("%%clear%%", "§r")
                .replace("%%magic%%", "§k")
                .replace("%%bold%%", "§l")
                .replace("%%strike%%", "§m")
                .replace("%%underlined%%", "§n"));
        return color(s);
    }

    public static String translateColorWords(String s) {
        return color(s
                .replaceAll("%%black%%", "§0")
                .replaceAll("%%dark-blue%%", "§1")
                .replaceAll("%%dark-green%%", "§2")
                .replaceAll("%%dark-aqua%%", "§3")
                .replaceAll("%%dark-red%%", "§4")
                .replaceAll("%%purple%%", "§5")
                .replaceAll("%%gold%%", "§6")
                .replaceAll("%%gray%%", "§7")
                .replaceAll("%%dark-gray%%", "§8")
                .replaceAll("%%blue%%", "§9")
                .replaceAll("%%green%%", "§a")
                .replaceAll("%%aqua%%", "§b")
                .replaceAll("%%red%%", "§c")
                .replaceAll("%%pink%%", "§d")
                .replaceAll("%%yellow%%", "§e")
                .replaceAll("%%white%%", "§f")
                .replaceAll("%%clear%%", "§r")
                .replaceAll("%%magic%%", "§k")
                .replaceAll("%%bold%%", "§l")
                .replaceAll("%%strike%%", "§m")
                .replaceAll("%%underlined%%", "§n"));
    }

    private static String color(String string) {
        String toReturn = org.bukkit.ChatColor.translateAlternateColorCodes('&', string);
        for (Map.Entry<String, net.md_5.bungee.api.ChatColor> entry : cachedHexColors.entrySet()) {
            String key = entry.getKey();
            net.md_5.bungee.api.ChatColor value = entry.getValue();
            toReturn = toReturn.replace(key, value.toString());
        }
        return toReturn;
    }

    private static List<String> color(List<String> string) {
        string.replaceAll(str -> {
            str = str.replace("&", "§");
            for (Map.Entry<String, net.md_5.bungee.api.ChatColor> entry : cachedHexColors.entrySet()) {
                String key = entry.getKey();
                net.md_5.bungee.api.ChatColor value = entry.getValue();
                str = str.replaceAll(key, value.toString());
            }
            return str;
        });
        return string;
    }
}
