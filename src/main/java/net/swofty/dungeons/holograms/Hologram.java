package net.swofty.dungeons.holograms;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.swofty.dungeons.SwoftyDungeons;
import net.swofty.dungeons.utilities.ReflectionHelper;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class Hologram {

    public static final double DELTA = SwoftyDungeons.getPlugin().getConfig().getDouble("hologram-split-size");

    public static final Map<UUID, HashMap<String, Hologram>> HOLO_MAP = new HashMap<>();
    public static final Map<String, Hologram> HOLOGRAM_CACHE = new HashMap<>();
    public static final Multimap<UUID, String> IN_SIGHT = ArrayListMultimap.create();
    public static final Multimap<UUID, String> HOLOGRAMS = ArrayListMultimap.create();

    private final List<Object> armorStands = new ArrayList<>();
    private final List<Packet<ClientGamePacketListener>> showPackets = new ArrayList<>();
    private final List<Packet<ClientGamePacketListener>> showPackets2 = new ArrayList<>();
    private final List<Packet<ClientGamePacketListener>> hidePackets = new ArrayList<>();
    private final List<Object> metaPackets = new ArrayList<>();

    private final Location location;
    private final Object worldServer;
    @Getter
    private final String name;

    @Getter
    private List<String> text;
    public List<Object> updatePackets = null;

    public Hologram(Location location, List<String> text) {
        this(UUID.randomUUID().toString(), location, text);
    }

    public Hologram(String name, Location location, List<String> text) {
        this.name = name;
        this.location = location;
        this.text = text;

        worldServer = ((CraftWorld) location.getWorld()).getHandle();

        HOLOGRAM_CACHE.put(name, this);

        createPackets(text.toArray(new String[text.size()]));
    }

    public Hologram setText(String... text) {
        this.text = Arrays.asList(text);
        return this;
    }

    private void createPackets(String[] text) {
        Location location = this.location.clone().add(0, (DELTA * text.length) + 1f, 0); // markers drop the armor stand's nametag by around 1 block

        for (String line : text) {
            net.minecraft.world.entity.decoration.ArmorStand eas = getEAS(line, location);

            location.subtract(0, DELTA, 0);
            if (line.equals(""))
                continue;

            showPackets.add(new ClientboundAddEntityPacket(eas));
            showPackets2.add(new ClientboundSetEntityDataPacket(eas.getId(), eas.getEntityData(), false));
            hidePackets.add(new ClientboundRemoveEntitiesPacket(eas.getId()));
        }
    }

    public void show(Player player) {
        show(player, true);
    }

    public void show(Player player, boolean add) {
        if (player.getWorld().getUID() != location.getWorld().getUID())
            return;
        for (int i = 0; i < text.size(); i++) {
            if (text.get(i).isEmpty()) continue; // No need to spawn the line.
            ReflectionHelper.sendPacket(showPackets.get(i), player);
            ReflectionHelper.sendPacket(showPackets2.get(i), player);
        }
        IN_SIGHT.put(player.getUniqueId(), getName());

        if (add)
            HOLOGRAMS.put(player.getUniqueId(), getName());
    }

    public void hide(Player player, boolean delete) {
        for (int i = 0; i < text.size(); i++) {
            if (text.get(i).isEmpty()) continue; // No need to hide the line (as it was never spawned).
            ReflectionHelper.sendPacket(hidePackets.get(i), player);
        }
        IN_SIGHT.remove(player.getUniqueId(), getName());

        if (delete)
            HOLOGRAMS.remove(player.getUniqueId(), getName());
    }

    public void hide(Player player) {
        hide(player, false);
    }

    public static void handleRefreshment(Player player) {
        ArrayList<String> supposedToView = new ArrayList<>(HOLOGRAMS.get(player.getUniqueId()));
        for (String name : supposedToView) {
            if (name.contains("lb")) continue;

            Hologram hologram = HOLOGRAM_CACHE.get(name);
            if (hologram == null) continue;

            if (IN_SIGHT.containsEntry(player.getUniqueId(), name)) {
                if (hologram.getLocation().distance(player.getLocation()) >= 35) {
                    hologram.hide(player, false);
                    IN_SIGHT.remove(player.getUniqueId(), name);
                }
                continue;
            }
            if (hologram.getLocation().distance(player.getLocation()) < 35) {
                hologram.hide(player, false);
                new BukkitRunnable() {
                    public void run() {
                        hologram.show(player, false);
                    }
                }.runTaskLater(SwoftyDungeons.getPlugin(), 3);
                IN_SIGHT.put(player.getUniqueId(), name);
            }
        }
    }

    public static net.minecraft.world.entity.decoration.ArmorStand getEAS(String text, Location location) {
        net.minecraft.world.entity.decoration.ArmorStand eas = new net.minecraft.world.entity.decoration.ArmorStand(((CraftWorld) location.getWorld()).getHandle(),
                location.getX(), location.getY(), location.getZ());

        eas.setCustomName(Component.Serializer.fromJsonLenient("{\"text\":\"" + text + "\"}"));
        eas.setSmall(true);
        eas.setNoBasePlate(true);
        eas.setShowArms(false);
        eas.setNoGravity(true);
        eas.setCustomNameVisible(true);
        eas.setInvisible(true);
        ((ArmorStand) eas.getBukkitEntity()).setMarker(true);

        eas.getEntityData().set(new EntityDataAccessor<>(15, EntityDataSerializers.BYTE), (byte) (0x01 | 0x04 | 0x08 | 0x10));
        //eas.getDataWatcher().set(new DataWatcherObject<Byte>(14, DataWatcherRegistry.a), (byte) (0x01 | 0x02 | 0x08 | 0x10));

        //eas.getAddEntityPacket().write(new FriendlyByteBuf( (byte) (0x01 | 0x02 | 0x08 | 0x10)).write);
        //eas.getDataWatcher().set(new DataWatcherObject<Boolean>(5, DataWatcherRegistry.i), true);
        //eas.getDataWatcher().set(new DataWatcherObject<Byte>(14, DataWatcherRegistry.a), (byte) (0x01 | 0x02 | 0x08 | 0x10));

        return eas;
    }
}
