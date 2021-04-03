package com.customwrld.customstaffmode.tasks;

import com.customwrld.customlib.util.CC;
import com.customwrld.customstaffmode.CustomStaffMode;
import com.customwrld.customstaffmode.Staff;
import com.customwrld.customstaffmode.util.XrayBlockEntry;
import com.customwrld.customstaffmode.util.XrayUtils;
import net.minecraft.server.v1_16_R3.Block;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public class StaffModeTasks {

    private ArrayList<Material> blockTypes;

    public static void startTasks() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomStaffMode.getCustomStaffMode(), () -> {
            for (Map.Entry<Player, Staff> entry : CustomStaffMode.getCustomStaffMode().getOnlinestaff().entrySet()) {
                if (entry.getKey().hasMetadata("modmode")) {
                    if (entry.getKey().hasMetadata("vanished")) {
                        entry.getKey().sendActionBar(CC.GRAY + "You are currently " + CC.GREEN + "invisible.");
                    } else {
                        entry.getKey().sendActionBar(CC.GRAY + "You are currently " + CC.RED + "visible.");
                    }
                }
            }
        }, 0L, 20L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomStaffMode.getCustomStaffMode(), () -> {
            for (Map.Entry<Player, Staff> entry : CustomStaffMode.getCustomStaffMode().getOnlinestaff().entrySet()) {
                if (entry.getKey().hasMetadata("modmode")) {
                    Player player = entry.getKey();
                    Staff staff = entry.getValue();

                    assert player != null;
                    assert staff != null;

                    if (staff.isXray()) {
                        for (int x=-16; x < 16; x++) {
                            for (int y=-16; y < 16; y++) {
                                for (int z=-16; z < 16; z++) {
                                    for (XrayBlockEntry xrayBlockEntry : XrayUtils.blockTypes) {
                                        if (xrayBlockEntry.getMaterial() == player.getWorld().getBlockAt(player.getLocation().add(x, y, z)).getType()) {
                                            Location location = player.getLocation().add(x, y, z);
                                            if (staff.getCachedBlocks().containsKey(location)) {
                                                if (player.getWorld().getBlockAt(location).getType() != staff.getCachedBlocks().get(location)) {
                                                    staff.getCachedBlocks().remove(location);
                                                }
                                            }
                                            staff.addXrayBlock(player, location, xrayBlockEntry.getMaterial(), xrayBlockEntry.getColor(), xrayBlockEntry.getDyeColor(), ((CraftBlock) player.getWorld().getBlockAt(player.getLocation().add(x, y, z))).getNMS().getBlock());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, 0L, 20L);
    }
}
