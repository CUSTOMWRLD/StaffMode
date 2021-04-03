package com.customwrld.customstaffmode.util;

import com.customwrld.customlib.util.NMSUtil;
import com.customwrld.customstaffmode.CustomStaffMode;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftFallingBlock;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftShulker;
import org.bukkit.craftbukkit.v1_16_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class XrayUtils {

    public static ArrayList<XrayBlockEntry> blockTypes = new ArrayList<>();

    public static void setBlockTypes() {

        blockTypes.addAll(Arrays.asList(
                new XrayBlockEntry(Material.DIAMOND_ORE, EnumChatFormat.AQUA, DyeColor.LIGHT_BLUE),
                new XrayBlockEntry(Material.IRON_ORE, EnumChatFormat.WHITE, DyeColor.WHITE),
                new XrayBlockEntry(Material.COAL_ORE, EnumChatFormat.BLACK, DyeColor.BLACK),
                new XrayBlockEntry(Material.EMERALD_ORE, EnumChatFormat.GREEN, DyeColor.LIME),
                new XrayBlockEntry(Material.LAPIS_ORE, EnumChatFormat.DARK_BLUE, DyeColor.BLUE),
                new XrayBlockEntry(Material.NETHER_GOLD_ORE, EnumChatFormat.YELLOW, DyeColor.YELLOW),
                new XrayBlockEntry(Material.GOLD_ORE, EnumChatFormat.YELLOW, DyeColor.YELLOW),
                new XrayBlockEntry(Material.ANCIENT_DEBRIS, EnumChatFormat.DARK_PURPLE, DyeColor.PURPLE),
                new XrayBlockEntry(Material.REDSTONE_ORE, EnumChatFormat.RED, DyeColor.RED)));
    }

    public static void sendGlowingFallingBlock(Player p, Location loc, long lifetime, EnumChatFormat color, Material material, Block block) {
        Scoreboard nmsScoreBoard = ((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle();

        IBlockData blockData = block.getBlockData();

        EntityFallingBlock fallingBlock = new EntityFallingBlock(((CraftWorld) loc.getWorld()).getHandle(), loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5, blockData);
        fallingBlock.setLocation(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5, 0, 0);
        fallingBlock.glowing = true;
        fallingBlock.setNoGravity(true);
        fallingBlock.setInvisible(true);
        fallingBlock.setFlag(6, true); //Glow

        //shulker.setFlag(5, true); //Invisibility

        NMSUtil.sendPacket(p, new PacketPlayOutEntityMetadata(fallingBlock.getId(), fallingBlock.getDataWatcher(), true));

        String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();
        try {
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "a", name);
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "e", "never");
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "h", Collections.singletonList(fallingBlock.getName()));
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "g", color);
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "i", 0);
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "j", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NMSUtil.sendPacket(p, packetPlayOutScoreboardTeam);

        PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(fallingBlock);
        NMSUtil.sendPacket(p, spawnPacket);
        NMSUtil.sendPacket(p, new PacketPlayOutEntityMetadata(fallingBlock.getId(), fallingBlock.getDataWatcher(), true));

        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomStaffMode.getCustomStaffMode(), () -> {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(fallingBlock.getId());
            NMSUtil.sendPacket(p, destroyPacket);
            PacketPlayOutScoreboardTeam destroyScoreboardTeamPacket = new PacketPlayOutScoreboardTeam();
            try {
                NMSUtil.setValue(packetPlayOutScoreboardTeam, "a", name);
                NMSUtil.setValue(destroyScoreboardTeamPacket, "i", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            NMSUtil.sendPacket(p, destroyScoreboardTeamPacket);

        }, lifetime);
    }
    public static void sendGlowingBlockShulker(Player p, Location loc, long lifetime, EnumChatFormat color, DyeColor dyeColor) {
        Scoreboard nmsScoreBoard = ((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle();

        EntityShulker shulker = new EntityShulker(EntityTypes.SHULKER, ((CraftWorld) loc.getWorld()).getHandle());
        shulker.setLocation(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5, 0, 0);
        shulker.glowing = true;
        shulker.setInvisible(true);
        shulker.setFlag(6, true); //Glow
        shulker.getDataWatcher().set(EntityShulker.COLOR, color == null ? 16 : dyeColor.getWoolData());
        //shulker.setFlag(5, true); //Invisibility

        NMSUtil.sendPacket(p, new PacketPlayOutEntityMetadata(shulker.getId(), shulker.getDataWatcher(), true));

        String name = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();
        try {
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "a", name);
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "e", "never");
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "h", Collections.singletonList(shulker.getName()));
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "g", color);
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "i", 0);
            NMSUtil.setValue(packetPlayOutScoreboardTeam, "j", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NMSUtil.sendPacket(p, packetPlayOutScoreboardTeam);

        PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(shulker);
        NMSUtil.sendPacket(p, spawnPacket);
        NMSUtil.sendPacket(p, new PacketPlayOutEntityMetadata(shulker.getId(), shulker.getDataWatcher(), true));

        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomStaffMode.getCustomStaffMode(), () -> {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(shulker.getId());
            NMSUtil.sendPacket(p, destroyPacket);
            PacketPlayOutScoreboardTeam destroyScoreboardTeamPacket = new PacketPlayOutScoreboardTeam();
            try {
                NMSUtil.setValue(packetPlayOutScoreboardTeam, "a", name);
                NMSUtil.setValue(destroyScoreboardTeamPacket, "i", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            NMSUtil.sendPacket(p, destroyScoreboardTeamPacket);

        }, lifetime);
    }
}
