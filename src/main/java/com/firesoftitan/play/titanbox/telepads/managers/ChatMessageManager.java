package com.firesoftitan.play.titanbox.telepads.managers;

import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import com.firesoftitan.play.titanbox.telepads.guis.TelepadSettingsGui;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

import static com.firesoftitan.play.titanbox.telepads.TitanTelePads.*;

public class ChatMessageManager {
    private  HashMap<UUID, Location> changeNames = new HashMap<UUID, Location>();
    private  HashMap<UUID, Location> changeIcons = new HashMap<UUID, Location>();
    public ChatMessageManager() {

    }
    public boolean hasPlayer(Player player)
    {
        return hasPlayer(player.getUniqueId());
    }
    public boolean hasPlayer(UUID uuid)
    {
        if (changeNames.containsKey(uuid) || changeIcons.containsKey(uuid)) return true;
        return false;
    }

    public void addToNames(Player player, Location location)
    {
        addToNames(player.getUniqueId(), location);
    }
    public void addToNames(UUID uuid, Location location)
    {
        changeNames.put(uuid, location);
    }
    public boolean hasPlayerInNames(Player player)
    {
        return hasPlayerInNames(player.getUniqueId());
    }
    public boolean hasPlayerInNames(UUID uuid)
    {
        return changeNames.containsKey(uuid);
    }


    public void addToIcon(Player player, Location location)
    {
        addToIcon(player.getUniqueId(), location);
    }
    public void addToIcon(UUID uuid, Location location)
    {
        changeIcons.put(uuid, location);
    }
    public boolean hasPlayerInIcons(Player player)
    {
        return hasPlayerInIcons(player.getUniqueId());
    }
    public boolean hasPlayerInIcons(UUID uuid)
    {
        return changeIcons.containsKey(uuid);
    }

    public void chatInput(Player player, String message)
    {
        if (message.toLowerCase().equalsIgnoreCase("exit") || message.toLowerCase().equalsIgnoreCase("cancel"))
        {
            clearPlayer(player);
            player.sendMessage(ChatColor.RED + "Setup canceled!");
            return;
        }

        if (changeIcons.containsKey(player.getUniqueId()))
        {
            Location location = changeIcons.get(player.getUniqueId());
            clearPlayer(player);
            UUID owner = TelePadsManager.instants.getOwner(location);
            if (owner.equals(player.getUniqueId()) || isAdmin(player)) {
                if (message.equalsIgnoreCase("hand"))
                {
                    TelePadsManager.instants.setIcon(location, player.getInventory().getItemInMainHand().clone());
                }
                else
                {
                    ItemStack skull = tools.getSkullTool().getSkull(message);
                    TelePadsManager.instants.setIcon(location, skull);
                }
                messageTool.sendMessagePlayer(player, LangManager.instants.getMessage("icon.changed"));
                TelepadSettingsGui settingsGui = TelepadSettingsGui.getGui(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        settingsGui.showGUI(player, settingsGui.getLocations());
                    }
                }.runTaskLater(instants, 1);
                return;
            }
            messageTool.sendMessagePlayer(player, LangManager.instants.getMessage("icon.canceled"));
        }
        if (changeNames.containsKey(player.getUniqueId()))
        {
            Location location = changeNames.get(player.getUniqueId());
            clearPlayer(player);
            UUID owner = TelePadsManager.instants.getOwner(location);
            if (owner.equals(player.getUniqueId()) || isAdmin(player)) {
                TelePadsManager.instants.setName(location, ChatColor.translateAlternateColorCodes('&', message));
                messageTool.sendMessagePlayer(player, LangManager.instants.getMessage("name.changed"));
                TelepadSettingsGui settingsGui = TelepadSettingsGui.getGui(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        settingsGui.showGUI(player, settingsGui.getLocations());
                    }
                }.runTaskLater(instants, 1);
                return;
            }
            messageTool.sendMessagePlayer(player, LangManager.instants.getMessage("name.canceled"));
        }
    }

    private void clearPlayer(Player player) {
        TitanTelePads.instants.togglePlayerChat(player, true);
        changeNames.remove(player.getUniqueId());
        changeIcons.remove(player.getUniqueId());
    }
}
