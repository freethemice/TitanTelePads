package com.firesoftitan.play.titanbox.telepads.guis;

import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import com.firesoftitan.play.titanbox.telepads.managers.TelePadsManager;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TelepadGui {
    private static HashMap<UUID, TelepadGui> activeGuis = new HashMap<UUID, TelepadGui>();
    private Inventory myGui;
    private int size;
    private Player viewer;
    private int scrollStart = 0;
    private List<Location> locations;
    private String showingCat = "";
    public static String guiName = "TelePad Gui";
    public static TelepadGui getGui(Player player)
    {
        if (activeGuis.containsKey(player.getUniqueId())) {
            TelepadGui telepadGui = activeGuis.get(player.getUniqueId());
            return telepadGui;
        }
        return null;
    }
    public TelepadGui() {
        this.size = 54;
        myGui = Bukkit.createInventory(null, size, guiName);
    }

    private void mainDraw() {
        drawMain();
        ItemStack button = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        for (int i = size - 9; i < size; i++) {
            myGui.setItem(i, button.clone());
        }
        button = new ItemStack(Material.ARROW);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, "Scroll Down");
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "left");
        myGui.setItem(size - 9, button.clone());

        redrawBookButton();


        button = new ItemStack(Material.ARROW);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, "Scroll Up");
        button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "right");
        myGui.setItem(size - 1, button.clone());
    }

    public void redrawBookButton() {
        int start = size - 8;
        ItemStack button;
        NBTTagCompound nbtTagCompound;
        List<String> allCats = TitanTelePads.configManager.getCategoryNames();
        for (int i = 0; i < Math.min(allCats.size(), 7); i++) {
            button = new ItemStack(Material.BOOK);
            String catName = allCats.get(i);
            int slot = TitanTelePads.configManager.getCategorySlot(catName);
            if (catName.equalsIgnoreCase("admin") || catName.equalsIgnoreCase("mine") || catName.equalsIgnoreCase("all"))
            {
                button = new ItemStack(Material.WRITABLE_BOOK);
            }
            if (catName.equals(showingCat))
            {
                button = new ItemStack(Material.ENCHANTED_BOOK);
            }
            button = TitanTelePads.tools.getItemStackTool().changeName(button, catName);
            button = TitanTelePads.tools.getNBTTool().set(button, "buttonaction", "switch");
            button = TitanTelePads.tools.getNBTTool().set(button, "category", catName);
            myGui.setItem(start + slot, button.clone());
        }
    }

    public void drawMain() {
        ItemStack telepads = new ItemStack(TitanTelePads.configManager.getMaterial());

        for (int i = 0; i < size - 9; i++) {
            ItemStack button = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            if (i + scrollStart < locations.size() ) {
                Location l = locations.get(i + scrollStart);
                Boolean admin = TelePadsManager.instants.isAdmin(l);
                UUID owner = TelePadsManager.instants.getOwner(l);
                button = TelePadsManager.instants.getIcon(l);
                if (TitanTelePads.tools.getItemStackTool().isEmpty(button)) button = telepads.clone();
                button = TitanTelePads.tools.getItemStackTool().changeName(button, ChatColor.GREEN + "(Public) "  + ChatColor.RESET + TelePadsManager.instants.getName(l));
                if (TelePadsManager.instants.isPrivate(l)) button = TitanTelePads.tools.getItemStackTool().changeName(button, ChatColor.RED + "(Private) "  + ChatColor.RESET + TelePadsManager.instants.getName(l));

                List<String> lore = new ArrayList<String>();
                lore.add("World: " + ChatColor.WHITE + l.getWorld().getName());
                if (admin)
                    lore.add("Owner: " + ChatColor.WHITE + "ADMIN");
                else
                    lore.add("Owner: " + ChatColor.WHITE + TelePadsManager.instants.getOwnerName(l));
                if (owner.equals(viewer.getUniqueId()) || TitanTelePads.isAdmin(viewer))
                {
                    lore.add(ChatColor.GRAY + "Right for settings");
                }
                button = TitanTelePads.tools.getItemStackTool().addLore(button, lore);
                button = TitanTelePads.tools.getNBTTool().set(button, "padlocation", l);
            }
            myGui.setItem(i, button.clone());
        }
    }
    public void addScroll()
    {
        scrollStart += 9;
        if (scrollStart > locations.size()) scrollStart -= 9;
    }
    public void subtractScroll()
    {
        scrollStart -= 9;
        if (scrollStart < 0) scrollStart = 0;
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
    public void setToggle(String category)
    {
        TelePadsManager padsManager = TelePadsManager.instants;
        showingCat = category;
         List<Location> tempSort = padsManager.getAll(showingCat);
        if (category.equalsIgnoreCase("all")) tempSort = padsManager.getAll();
        if (category.equalsIgnoreCase("mine")) tempSort = padsManager.getAll(viewer.getUniqueId());
        if (category.equalsIgnoreCase("admin")) tempSort = padsManager.getAllAdmin();
        filterPrivacy(tempSort);
    }

    private void filterPrivacy(List<Location> tempSort) {
        TelePadsManager padsManager = TelePadsManager.instants;
        locations = new ArrayList<Location>();
        for(Location location: tempSort)
        {
            if (padsManager.isPrivate(location))
            {
                if (padsManager.isAdmin(location))
                {
                    if (TitanTelePads.isAdmin(viewer))
                    {
                        locations.add(location.clone());
                    }
                }
                else
                {
                    if (TitanTelePads.isAdmin(viewer) || padsManager.getOwner(location).equals(viewer.getUniqueId()))
                    {
                        locations.add(location.clone());
                    }
                }
            }
            else
            {
                locations.add(location.clone());
            }
        }
    }

    public Inventory getMyGui() {
        return myGui;
    }

    public int getSize() {
        return size;
    }
    public void showGUI(Player player)
    {
        viewer = player;
        activeGuis.put(viewer.getUniqueId(), this);
        setToggle(TitanTelePads.configManager.getCategoryDefaultOpen());
        mainDraw();
        player.openInventory(myGui);
    }
}
