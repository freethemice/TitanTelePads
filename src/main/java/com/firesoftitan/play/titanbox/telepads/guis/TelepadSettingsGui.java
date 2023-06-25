package com.firesoftitan.play.titanbox.telepads.guis;

import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import com.firesoftitan.play.titanbox.telepads.managers.LangManager;
import com.firesoftitan.play.titanbox.telepads.managers.TelePadsManager;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class TelepadSettingsGui {
    private static HashMap<UUID, TelepadSettingsGui> activeGuis = new HashMap<UUID, TelepadSettingsGui>();
    private Inventory myGui;
    private int size;
    private Player viewer;
    private Location locations;
    private TelePadsManager padsManager;
    public static String guiName = LangManager.instants.getMessage("telepadguisettings.name");
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
        ItemStack button = getCustomItem(71009);
        for (int i = 0; i < size; i++) {
            myGui.setItem(i, button.clone());
        }
        reDrawSettings();


    }
    private ItemStack getCustomItem(int id) {
        ItemStack button = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, " ");
        if (TitanTelePads.configManager.isGui_enabled()) {
            ItemMeta itemMeta;
            itemMeta = button.getItemMeta();
            itemMeta.setCustomModelData(id);
            button.setItemMeta(itemMeta);
        }
        return button.clone();
    }
    public void reDrawSettings() {
        NBTTagCompound nbtTagCompound;
        ItemStack button;
        int slot = 0;

        button = getCustomItem(71016);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, LangManager.instants.getMessage("telepadguisettings.name2") + ChatColor.WHITE + padsManager.getName(locations));
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "name");
        myGui.setItem(slot, button.clone());
        slot++;

        button = getCustomItem(71021);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, LangManager.instants.getMessage("telepadgui.owner") + ChatColor.WHITE + TelePadsManager.instants.getOwnerName(locations));
        if (padsManager.isAdmin(locations)) {
            button = getCustomItem(71017);
            button = TitanTelePads.tools.getItemStackTool().changeName(button, LangManager.instants.getMessage("telepadgui.owner")  + ChatColor.DARK_RED + "ADMIN");
        }
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "owner");
        myGui.setItem(slot, button.clone());
        slot++;

        button = getCustomItem(71011);
        if (padsManager.isPrivate(locations)) button = getCustomItem(71010);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, LangManager.instants.getMessage("telepadguisettings.private") + ChatColor.WHITE + padsManager.isPrivate(locations));
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "private");
        myGui.setItem(slot, button.clone());
        slot++;

        button = getCustomItem(71023);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, LangManager.instants.getMessage("telepadguisettings.category") + ChatColor.WHITE + padsManager.getCategory(locations));
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "category");
        myGui.setItem(slot, button.clone());
        slot++;

        button = padsManager.getIcon(locations);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, LangManager.instants.getMessage("telepadguisettings.icon"));
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "icon");
        myGui.setItem(slot, button.clone());
        slot++;

        button = getCustomItem(71015);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, LangManager.instants.getMessage("telepadguisettings.back"));
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
