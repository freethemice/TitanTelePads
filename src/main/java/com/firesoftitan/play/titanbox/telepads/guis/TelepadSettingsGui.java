package com.firesoftitan.play.titanbox.telepads.guis;

import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import com.firesoftitan.play.titanbox.telepads.managers.TelePadsManager;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TelepadSettingsGui {
    private static HashMap<UUID, TelepadSettingsGui> activeGuis = new HashMap<UUID, TelepadSettingsGui>();
    private Inventory myGui;
    private int size;
    private Player viewer;
    private Location locations;
    private TelePadsManager padsManager;
    public static String guiName = "TelePad Settings Gui";
    public static TelepadSettingsGui getGui(Player player)
    {
        if (activeGuis.containsKey(player.getUniqueId())) {
            TelepadSettingsGui telepadGui = activeGuis.get(player.getUniqueId());
            return telepadGui;
        }
        return null;
    }
    public TelepadSettingsGui() {
        this.size = 9;
        myGui = Bukkit.createInventory(null, size, guiName);
        padsManager = TelePadsManager.instants;
    }

    private void mainDraw() {

        NBTTagCompound nbtTagCompound;
        ItemStack button = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        for (int i = 0; i < size; i++) {
            myGui.setItem(i, button.clone());
        }
        reDrawSettings();


    }

    public void reDrawSettings() {
        NBTTagCompound nbtTagCompound;
        ItemStack button;
        int slot = 0;

        button = new ItemStack(Material.NAME_TAG);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, "Name: " + ChatColor.WHITE + padsManager.getName(locations));
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "name");
        myGui.setItem(slot, button.clone());
        slot++;

        button = new ItemStack(Material.PLAYER_HEAD);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, "Owner: " + ChatColor.WHITE + TelePadsManager.instants.getOwnerName(locations));
        if (padsManager.isAdmin(locations)) {
            button = new ItemStack(Material.BOOKSHELF);
            button = TitanTelePads.tools.getItemStackTool().changeName(button, "Owner: " + ChatColor.DARK_RED + "ADMIN");
        }
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "owner");
        myGui.setItem(slot, button.clone());
        slot++;

        button = new ItemStack(Material.LIME_CONCRETE);
        if (padsManager.isPrivate(locations)) button = new ItemStack(Material.RED_CONCRETE);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, "Private: " + ChatColor.WHITE + padsManager.isPrivate(locations));
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "private");
        myGui.setItem(slot, button.clone());
        slot++;

        button = new ItemStack(Material.LECTERN);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, "Category: " + ChatColor.WHITE + padsManager.getCategory(locations));
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "category");
        myGui.setItem(slot, button.clone());
        slot++;

        button = padsManager.getIcon(locations);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, "Icon");
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "icon");
        myGui.setItem(slot, button.clone());
        slot++;

        button = new ItemStack(Material.ARROW);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, "Back");
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "Back");
        myGui.setItem(slot, button.clone());
        slot++;
    }

    public boolean isGuiOpen()
    {
        if (viewer != null) {
            if (viewer.getOpenInventory().getTitle().equals(guiName)) {
                return true;
            }
        }
        return false;
    }
    public Player getViewer()
    {
        if (viewer != null) {
            if (viewer.getOpenInventory().getTitle().equals(guiName)) {
                return viewer;
            }
        }
        viewer = null;
        return null;
    }
    public Inventory getMyGui() {
        return myGui;
    }

    public Location getLocations() {
        return locations;
    }

    public int getSize() {
        return size;
    }
    public void showGUI(Player player, Location location)
    {
        viewer = player;
        this.locations = location;
        activeGuis.put(viewer.getUniqueId(), this);
        mainDraw();
        player.openInventory(myGui);
    }
}
