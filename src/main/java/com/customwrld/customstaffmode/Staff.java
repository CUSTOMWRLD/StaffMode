package com.customwrld.customstaffmode;

import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ServerUtil;
import com.customwrld.customstaffmode.util.Base64Helper;
import com.customwrld.customstaffmode.util.Items;
import com.customwrld.customstaffmode.util.XrayUtils;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.Block;
import net.minecraft.server.v1_16_R3.BlockBase;
import net.minecraft.server.v1_16_R3.EnumChatFormat;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Staff {

    private String previousInv;

    @Getter private boolean xray;

    @Getter private Map<Location, Material> cachedBlocks;

    public Staff(Player player) {
        init(player);

        CustomStaffMode.getCustomStaffMode().getOnlinestaff().put(player, this);
    }

    public void init(Player player) {

        this.previousInv = Base64Helper.itemStackArrayToBase64(player.getInventory().getContents());
        player.getInventory().clear();
        Items.setItems(player);
        player.setMetadata("modmode", new FixedMetadataValue(CustomStaffMode.getCustomStaffMode(), "modmode"));
        player.setGameMode(GameMode.CREATIVE);
        this.cachedBlocks = new HashMap<>();
        vanish(player);

    }

    public void destroy(Player player) {

        player.getInventory().clear();
        try {
            player.getInventory().setContents(Base64Helper.itemStackArrayFromBase64(previousInv));
        } catch (IOException e) {
            player.sendMessage(CC.RED + "There was an error giving you back your inventory.");
            e.printStackTrace();
        }
        player.updateInventory();
        player.removeMetadata("modmode", CustomStaffMode.getCustomStaffMode());
        player.setGameMode(GameMode.SURVIVAL);
        deactivateXray(player);
        CustomStaffMode.getCustomStaffMode().getOnlinestaff().remove(player);
        unvanish(player);

    }

    public void vanish(Player player) {
        player.setMetadata("vanished", new FixedMetadataValue(CustomStaffMode.getCustomStaffMode(), "vanished"));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CC.GRAY + "You are currently" + CC.GREEN + " invisible."));
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (!online.hasPermission("flame.staffmode") || online.hasMetadata("hidestaff")) {
                ServerUtil.hidePlayer(player, online);
            }
        }
    }

    public void unvanish(Player player) {
        player.removeMetadata("vanished", CustomStaffMode.getCustomStaffMode());
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CC.GRAY + "You are currently" + CC.RED + " visible."));
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            ServerUtil.showPlayer(player, online);
        }
    }

    public void activateXray(Player player) {
        xray = true;
        player.sendMessage(CC.GRAY + "Xray" + CC.GREEN + " enabled.");
    }

    public void deactivateXray(Player player) {
        cachedBlocks.clear();
        xray = false;
        player.sendMessage(CC.GRAY + "Xray" + CC.RED + " disabled.");
    }

    public void addXrayBlock(Player player, Location location, Material material, EnumChatFormat color, DyeColor dyeColor, Block block) {
        if (cachedBlocks.containsKey(location)) {
            return;
        }
        cachedBlocks.put(location, material);
        XrayUtils.sendGlowingBlockShulker(player, location, 20, color, dyeColor);
    }
}
