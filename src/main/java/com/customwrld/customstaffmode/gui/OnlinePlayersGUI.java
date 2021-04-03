package com.customwrld.customstaffmode.gui;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;

import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.smartinvs.content.SlotIterator;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemBuilder;
import com.customwrld.customstaffmode.CustomStaffMode;
import com.customwrld.customstaffmode.Staff;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OnlinePlayersGUI implements InventoryProvider {

    private Staff staffmode;

    private SmartInventory inventory;

    public OnlinePlayersGUI(Player player, Staff staffmode) {

        this.staffmode = staffmode;

        this.inventory = SmartInventory.builder()
                .id("onlinePlayersGUI")
                .provider(this)
                .size(4, 9)
                .title(CC.YELLOW + "Online Staff").build();

        this.inventory.open(player, 0);

    }

    @Override
    public void init(Player player, InventoryContents contents) {

        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[CustomStaffMode.getCustomStaffMode().getOnlinestaff().size()];

        contents.fill(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).title(" ").build()));

        AtomicInteger i= new AtomicInteger();

        CustomStaffMode.getCustomStaffMode().getOnlinestaff().forEach((staff, staffmode) -> {

            List<String> lore = new ArrayList<>();

            lore.add(CC.YELLOW + "Mod Mode: " + (staff.hasMetadata("modmode") ? CC.GREEN + "True" : CC.RED + "False"));

            ItemStack head = new ItemBuilder(Material.PLAYER_HEAD).title(staff.getDisplayName()).lores(lore).build();

            SkullMeta meta = (SkullMeta) head.getItemMeta();

            meta.setOwningPlayer(staff);

            head.setItemMeta(meta);

            items[i.intValue()] = ClickableItem.of(head,
                    e -> teleportToPlayer(player, staff));

            i.getAndIncrement();

        });

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        contents.set(0, 0, ClickableItem.of(new ItemBuilder(Material.ARROW).title("&6Previous Page").build(),
                e -> inventory.open(player, pagination.previous().getPage())));

        contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.ARROW).title("&6Next Page").build(),
                e -> inventory.open(player, pagination.next().getPage())));


    }

    private void teleportToPlayer(Player clicked, Player to) {

        clicked.teleport(to);
        clicked.sendMessage(CC.YELLOW + "Teleported you to " + to.getDisplayName());

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
