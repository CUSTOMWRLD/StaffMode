package com.customwrld.customstaffmode.command;

import com.customwrld.customlib.util.CC;
import com.customwrld.customstaffmode.CustomStaffMode;
import com.customwrld.customstaffmode.Staff;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

import java.io.IOException;

public class StaffCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("staff")
                .withPermission("flame.staffmode")
                .withAliases("mod")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(CC.RED + "You must be a player to run this command!");
                        return;
                    }
                    Player player = (Player) sender;
                    Staff staffMode = CustomStaffMode.getCustomStaffMode().getOnlinestaff().get(player);
                    if (staffMode == null) {
                        new Staff(player);
                        sender.sendMessage(CC.translate("&7Mod mode&7: &aEnabled"));
                    } else {
                        staffMode.destroy(player);
                        sender.sendMessage(CC.translate("&7Mod mode&7: &cDisabled"));
                    }
                });
    }
}
