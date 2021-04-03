package com.customwrld.customstaffmode.event;

import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.InventoryUtil;
import com.customwrld.customlib.util.ServerUtil;
import com.customwrld.customstaffmode.CustomStaffMode;
import com.customwrld.customstaffmode.Staff;
import com.customwrld.customstaffmode.gui.OnlinePlayersGUI;
import com.customwrld.customstaffmode.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
public class StaffModeListeners implements Listener {

    private static final ArrayList<Material> shulkers = new ArrayList<>();

    public static void setShulkers() {
        shulkers.addAll(Arrays.asList(Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX,
                Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX,
                Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.PINK_SHULKER_BOX,
                Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.WHITE_SHULKER_BOX,
                Material.YELLOW_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.SHULKER_BOX));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("flame.staffmode")) {

            new Staff(player);
            player.sendMessage(CC.translate("&7Mod mode&7: &aEnabled"));

        }

        for (Player otherPlayer : Bukkit.getServer().getOnlinePlayers().stream().filter(otherPlayer -> player != otherPlayer).collect(Collectors.toList())) {
            if (otherPlayer.hasMetadata("vanished")) {
                ServerUtil.hidePlayer(otherPlayer, player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) throws IOException {

        Player player = event.getPlayer();

        if (player.hasPermission("flame.staffmode")) {

            if (CustomStaffMode.getCustomStaffMode().getOnlinestaff().containsKey(player)) {

                if (CustomStaffMode.getCustomStaffMode().getOnlinestaff().get(player) != null) {

                    Staff staffmode = CustomStaffMode.getCustomStaffMode().getOnlinestaff().get(player);

                    staffmode.destroy(player);
                }
            }
            CustomStaffMode.getCustomStaffMode().getOnlinestaff().remove(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onContainerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (!player.hasMetadata("modmode") || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        if (block.getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
            player.openInventory(player.getEnderChest());
            player.sendMessage(CC.GREEN + "Opening ender chest silently.");
        }

        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
            event.setCancelled(true);
            Chest chest = (Chest) block.getState();
            player.openInventory(chest.getInventory());
            player.sendMessage(CC.GREEN + "Opening chest silently.");
        }

        if (shulkers.contains(block.getType())) {
            event.setCancelled(true);
            ShulkerBox shulkerBox = (ShulkerBox) block.getState();
            player.openInventory(shulkerBox.getInventory());
            player.sendMessage(CC.GREEN + "Opening shulker box silently.");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

            if (event.getItem() == null || event.getHand() != EquipmentSlot.HAND || event.getHand() == EquipmentSlot.OFF_HAND || event.getItem().getItemMeta() == null || !player.hasMetadata("modmode")) {
                return;
            }

            Staff staffmode = CustomStaffMode.getCustomStaffMode().getOnlinestaff().get(player);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);

            if (InventoryUtil.isSimilar(event.getItem(), Items.RANDOM_TELEPORT) && event.getHand() == EquipmentSlot.HAND) {
                event.setCancelled(true);
                if (Bukkit.getServer().getOnlinePlayers().size() == 1) {
                    player.sendMessage(CC.YELLOW + "There is nobody to teleport to!");
                    return;
                }
                Player randomPlayer = ((List<Player>) Bukkit.getServer().getOnlinePlayers()).get((int) (Math.random() * Bukkit.getServer().getOnlinePlayers().size()));
                if (randomPlayer != null && randomPlayer != player) {
                    player.teleport(randomPlayer);
                    player.sendMessage(CC.YELLOW + "Teleported you to " + randomPlayer.getName());
                }
            }
            if (InventoryUtil.isSimilar(event.getItem(), Items.ONLINE_STAFF) && event.getHand() == EquipmentSlot.HAND) {
                event.setCancelled(true);
                if (player.hasPermission("flame.staffmode.online")) {
                    new OnlinePlayersGUI(player, staffmode);
                } else {
                    player.sendMessage(CC.RED + "You do not have permission to see online staff.");
                }
            }

            if (InventoryUtil.isSimilar(event.getItem(), Items.XRAY) && event.getHand() == EquipmentSlot.HAND) {
                event.setCancelled(true);
                if (staffmode.isXray()) {
                    staffmode.deactivateXray(player);
                } else {
                    staffmode.activateXray(player);
                }
            }

            if (InventoryUtil.isSimilar(event.getItem(), Items.VANISH_TOGGLE(player.getUniqueId())) && event.getHand() == EquipmentSlot.HAND) {
                event.setCancelled(true);

                if (Items.vanishToggle.contains(player.getUniqueId())) {
                    Items.vanishToggle.remove(player.getUniqueId());
                    player.sendMessage(CC.GRAY + "You are now" + CC.GREEN + " invisible.");
                    vanish(player);
                } else {
                    Items.vanishToggle.add(player.getUniqueId());
                    player.sendMessage(CC.GRAY + "You are now" + CC.RED + " visible.");
                    unvanish(player);
                }
                player.getInventory().setItem(8, Items.VANISH_TOGGLE(player.getUniqueId()));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player player = event.getPlayer();
            Player rightClicked = (Player) event.getRightClicked();
            ItemStack itemStack = player.getItemInHand();

            if (itemStack.getItemMeta() == null || !player.hasMetadata("modmode")) {
                return;
            }

            if (InventoryUtil.isSimilar(itemStack, Items.INSPECT_INVENTORY) && event.getHand() == EquipmentSlot.HAND) {
                player.performCommand("invsee " + rightClicked.getName());
            }

            if (InventoryUtil.isSimilar(itemStack, Items.FREEZE) && event.getHand() == EquipmentSlot.HAND) {
                player.performCommand("freeze " + rightClicked.getName());
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {

        if (event.getPlayer().hasMetadata("modmode")) {

            if (Objects.requireNonNull(event.getMainHandItem()).isSimilar(Items.WORLDEDIT_WAND) || event.getMainHandItem().isSimilar(Items.RANDOM_TELEPORT) || event.getMainHandItem().isSimilar(Items.ONLINE_STAFF) || event.getMainHandItem().isSimilar(Items.TELEPORTER) || event.getMainHandItem().isSimilar(Items.INSPECT_INVENTORY) || event.getMainHandItem().isSimilar(Items.VANISH_TOGGLE(event.getPlayer().getUniqueId())))
                event.setCancelled(true);

            if (Objects.requireNonNull(event.getOffHandItem()).isSimilar(Items.WORLDEDIT_WAND) || event.getMainHandItem().isSimilar(Items.RANDOM_TELEPORT) || event.getMainHandItem().isSimilar(Items.ONLINE_STAFF) || event.getMainHandItem().isSimilar(Items.TELEPORTER) || event.getMainHandItem().isSimilar(Items.INSPECT_INVENTORY) || event.getMainHandItem().isSimilar(Items.VANISH_TOGGLE(event.getPlayer().getUniqueId())))
                event.setCancelled(true);


            if (event.getPlayer().hasPermission("flame.staffmode")) {
                return;
            }

            event.setCancelled(true);

        }

    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event.getWhoClicked().hasMetadata("modmode")) {

            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().isSimilar(Items.WORLDEDIT_WAND) || event.getCurrentItem().isSimilar(Items.RANDOM_TELEPORT) || event.getCurrentItem().isSimilar(Items.ONLINE_STAFF) || event.getCurrentItem().isSimilar(Items.TELEPORTER) || event.getCurrentItem().isSimilar(Items.INSPECT_INVENTORY) || event.getCurrentItem().isSimilar(Items.VANISH_TOGGLE(event.getWhoClicked().getUniqueId())))
                event.setCancelled(true);

            if (event.getWhoClicked().hasPermission("flame.staffmode.bypass")) {
                return;
            }

            event.setCancelled(true);

        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (event.getPlayer().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onInvDrag(InventoryDragEvent event) {

        if (event.getCursor() == null) {
            return;
        }

        if (event.getCursor().isSimilar(Items.WORLDEDIT_WAND) || event.getCursor().isSimilar(Items.RANDOM_TELEPORT) || event.getCursor().isSimilar(Items.ONLINE_STAFF) || event.getCursor().isSimilar(Items.TELEPORTER) || event.getCursor().isSimilar(Items.INSPECT_INVENTORY) || event.getCursor().isSimilar(Items.VANISH_TOGGLE(event.getWhoClicked().getUniqueId())))
            event.setCancelled(true);


        if (event.getWhoClicked().hasMetadata("modmode")) {

            if (event.getWhoClicked().hasPermission("flame.staffmode.bypass")) {
                return;
            }

            event.setCancelled(true);

        }
    }

    public void vanish(Player player) {
        player.setMetadata("vanished", new FixedMetadataValue(CustomStaffMode.getCustomStaffMode(), "vanished"));
        player.sendActionBar(CC.GRAY + "You are currently " + CC.GREEN + "invisible.");
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (!online.hasPermission("flame.staffmode") || online.hasMetadata("hidestaff")) {
                ServerUtil.hidePlayer(player, online);
            }
        }
    }

    public void unvanish(Player player) {
        player.removeMetadata("vanished", CustomStaffMode.getCustomStaffMode());

        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            ServerUtil.showPlayer(player, online);
        }
    }
}
