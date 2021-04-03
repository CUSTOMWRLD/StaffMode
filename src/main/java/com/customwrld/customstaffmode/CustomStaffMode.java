package com.customwrld.customstaffmode;

import com.customwrld.customstaffmode.command.StaffCommand;
import com.customwrld.customstaffmode.event.StaffModeListeners;
import com.customwrld.customstaffmode.tasks.StaffModeTasks;
import com.customwrld.customstaffmode.util.XrayUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class CustomStaffMode extends JavaPlugin {

    @Getter private static CustomStaffMode customStaffMode;

    @Getter private Map<Player, Staff> onlinestaff = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        customStaffMode = this;

        StaffCommand.command().register();

        Bukkit.getPluginManager().registerEvents(new StaffModeListeners(), this);

        XrayUtils.setBlockTypes();

        StaffModeListeners.setShulkers();

        StaffModeTasks.startTasks();
    }

    @Override
    public void onDisable() {
        for (Map.Entry<Player, Staff> entry : onlinestaff.entrySet()) {
            entry.getValue().destroy(entry.getKey());
        }
    }
}
