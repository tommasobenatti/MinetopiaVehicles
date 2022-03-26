package nl.mtvehicles.core.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import nl.mtvehicles.core.events.VehiclePlaceEvent;
import nl.mtvehicles.core.infrastructure.dataconfig.DefaultConfig;
import nl.mtvehicles.core.infrastructure.enums.Message;
import nl.mtvehicles.core.infrastructure.enums.RegionAction;
import nl.mtvehicles.core.infrastructure.helpers.ItemFactory;
import nl.mtvehicles.core.infrastructure.helpers.TextUtils;
import nl.mtvehicles.core.infrastructure.models.Vehicle;
import nl.mtvehicles.core.infrastructure.models.VehicleUtils;
import nl.mtvehicles.core.infrastructure.modules.ConfigModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VehiclePlaceListener implements Listener {
    @EventHandler
    public void onVehiclePlace(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final Action action = e.getAction();
        final ItemStack item = e.getItem();

        if (e.isCancelled()) return;

        if (e.getItem() == null) return;

        if (!e.getItem().hasItemMeta()
                || !(new NBTItem(e.getItem())).hasKey("mtvehicles.kenteken")
                || e.getClickedBlock() == null
        ) return;

        if (e.getHand() != EquipmentSlot.HAND) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(TextUtils.colorize(ConfigModule.messagesConfig.getMessage(Message.WRONG_HAND)));
            return;
        }

        String license = VehicleUtils.getLicensePlate(item);
        if (license == null) {
            return;
        }
        if (!VehicleUtils.existsByLicensePlate(license)) {
            ConfigModule.messagesConfig.sendMessage(p, Message.VEHICLE_NOT_FOUND);
            e.setCancelled(true);
            return;
        }
        if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }


        VehiclePlaceEvent vehiclePlaceEvent = new VehiclePlaceEvent();
        vehiclePlaceEvent.setLocation(e.getClickedBlock().getLocation());
        vehiclePlaceEvent.setPlayer(e.getPlayer());
        // You can set more things take a look at VehiclePlaceEvent
        Bukkit.getPluginManager().callEvent(vehiclePlaceEvent);

        if (vehiclePlaceEvent.isCancelled()) {
            return;
        }

        Location loc = e.getClickedBlock().getLocation();
        e.setCancelled(true);

        if (ConfigModule.defaultConfig.isBlockWhitelistEnabled()
                && !ConfigModule.defaultConfig.blockWhiteList().contains(e.getClickedBlock().getType())) {
            ConfigModule.messagesConfig.sendMessage(p, Message.BLOCK_NOT_IN_WHITELIST);
            return;
        }
        if (!ConfigModule.defaultConfig.canProceedWithAction(RegionAction.PLACE, loc)) {
            ConfigModule.messagesConfig.sendMessage(p, Message.CANNOT_DO_THAT_HERE);
            return;
        }

        if (VehicleUtils.getByLicensePlate(license) == null) {
            ConfigModule.messagesConfig.sendMessage(p, Message.VEHICLE_NOT_FOUND);
            return;
        }

        Location location = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
        ArmorStand as = location.getWorld().spawn(location, ArmorStand.class);
        as.setVisible(false);
        as.setCustomName("MTVEHICLES_SKIN_" + license);
        as.getEquipment().setHelmet(item);
        ArmorStand as2 = location.getWorld().spawn(location, ArmorStand.class);
        as2.setVisible(false);
        as2.setCustomName("MTVEHICLES_MAIN_" + license);
        Vehicle vehicle = VehicleUtils.getByLicensePlate(license);
        List<Map<String, Double>> seats = (List<Map<String, Double>>) vehicle.getVehicleData().get("seats");
        p.getInventory().remove(p.getEquipment().getItemInHand());
        p.sendMessage(TextUtils.colorize(ConfigModule.messagesConfig.getMessage(Message.VEHICLE_PLACE).replace("%p%", VehicleUtils.getByLicensePlate(license).getOwnerName())));
        for (int i = 1; i <= seats.size(); i++) {
            Map<String, Double> seat = seats.get(i - 1);
            if (i == 1) {
                Location location2 = new Location(location.getWorld(), location.getX() + seat.get("x"), location.getY() + seat.get("y"), location.getZ() + seat.get("z"));
                ArmorStand as3 = location2.getWorld().spawn(location2, ArmorStand.class);
                as3.setCustomName("MTVEHICLES_MAINSEAT_" + license);
                as3.setGravity(false);
                as3.setVisible(false);
            }
        }
        List<Map<String, Double>> wiekens = (List<Map<String, Double>>) vehicle.getVehicleData().get("wiekens");
        if (ConfigModule.vehicleDataConfig.getType(license).isHelicopter()) {
            for (int i = 1; i <= wiekens.size(); i++) {
                Map<?, ?> seat = wiekens.get(i - 1);
                if (i == 1) {
                    Location location2 = new Location(location.getWorld(), location.getX() + (Double) seat.get("z"), (Double) location.getY() + (Double) seat.get("y"), location.getZ() + (Double) seat.get("x"));
                    ArmorStand as3 = location2.getWorld().spawn(location2, ArmorStand.class);
                    as3.setCustomName("MTVEHICLES_WIEKENS_" + license);
                    as3.setGravity(false);
                    as3.setVisible(false);
                    if ((boolean) ConfigModule.defaultConfig.get(DefaultConfig.Option.HELICOPTER_BLADES_ALWAYS_ON)) {
                        ItemStack car = (new ItemFactory(Material.getMaterial("DIAMOND_HOE"))).setDurability((short) 1058).setName(TextUtils.colorize("&6Wieken")).setNBT("mtvehicles.kenteken", license).toItemStack();
                        ItemMeta im = car.getItemMeta();
                        List<String> itemlore = new ArrayList<>();
                        itemlore.add(TextUtils.colorize("&a"));
                        itemlore.add(TextUtils.colorize("&a" + license));
                        itemlore.add(TextUtils.colorize("&a"));
                        im.setLore(itemlore);
                        im.setUnbreakable(true);
                        car.setItemMeta(im);
                        as3.setHelmet((ItemStack) seat.get("item"));
                    }
                }
            }
        }
    }
}
