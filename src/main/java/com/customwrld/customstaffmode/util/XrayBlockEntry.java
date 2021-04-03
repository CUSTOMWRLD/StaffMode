package com.customwrld.customstaffmode.util;

import lombok.Getter;
import net.minecraft.server.v1_16_R3.EnumChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.scoreboard.Team;

public class XrayBlockEntry {

    @Getter private final Material material;
    @Getter private final EnumChatFormat color;
    @Getter private final DyeColor dyeColor;

    public XrayBlockEntry(Material material, EnumChatFormat color, DyeColor dyeColor) {
        this.material = material;
        this.color = color;
        this.dyeColor = dyeColor;
    }
}
