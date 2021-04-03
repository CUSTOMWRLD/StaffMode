package com.customwrld.customstaffmode.util;

import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Items {

    public static Set<UUID> vanishToggle = new HashSet<>();

    public static void setItems(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory inventory = player.getInventory();
        inventory.setItem(0, TELEPORTER);
        inventory.setItem(1, INSPECT_INVENTORY);
        inventory.setItem(2, RANDOM_TELEPORT);
        inventory.setItem(3, FREEZE);
        inventory.setItem(5, XRAY);
        inventory.setItem(6, WORLDEDIT_WAND);
        inventory.setItem(7, ONLINE_STAFF);
        inventory.setItem(8, VANISH_TOGGLE(uuid));
        player.updateInventory();
    }
    public static ItemStack TELEPORTER = new ItemBuilder(Material.COMPASS).title(CC.RED + "Teleporter").lores(CC.GRAY + "Right-Click to teleport through blocks!").build();
    public static ItemStack INSPECT_INVENTORY = new ItemBuilder(Material.CHEST).title(CC.YELLOW + "Inspect Inventory").lores(CC.GRAY + "Right-Click a player to inspect their inventory!").build();
    public static ItemStack RANDOM_TELEPORT = new ItemBuilder(Material.CLOCK).title(CC.AQUA + "Random Teleport").lores(CC.GRAY + "Right-Click to teleport to a random player!").build();
    public static ItemStack FREEZE = new ItemBuilder(Material.PACKED_ICE).title(CC.RED + "Freeze").lores(CC.GRAY + "Right-Click a player to freeze them!").build();
    public static ItemStack XRAY = new ItemBuilder(Material.DIAMOND_ORE).title(CC.RED + "Xray").lores(CC.GRAY + "Right-Click to highlight ores and other valuables around you!").build();
    public static ItemStack WORLDEDIT_WAND = new ItemBuilder(Material.WOODEN_AXE).title(CC.PINK + "WorldEdit Wand").lores(CC.GRAY + "You can do WorldEdit things with this!").build();
    public static ItemStack ONLINE_STAFF = new ItemBuilder(Material.PLAYER_HEAD).title(CC.WHITE + "Online Staff").lores(CC.GRAY + "Right-Click to view and teleport to online staff!").build();
    public static ItemStack VANISH_TOGGLE(UUID uuid) {
        if (vanishToggle.contains(uuid)) {
            return new ItemBuilder(Material.RED_CONCRETE).title(CC.GREEN + "Become Invisible").lores(CC.GRAY + "Right-Click to toggle vanish!").build();
        }
        return new ItemBuilder(Material.LIME_CONCRETE).title(CC.GREEN + "Become Visible").lores(CC.GRAY + "Right-Click to toggle vanish!").build();
    }
}
