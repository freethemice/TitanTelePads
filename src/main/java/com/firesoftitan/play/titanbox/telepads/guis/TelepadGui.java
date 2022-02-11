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
    private boolean showingALl = false;
    private List<Location> locations;
    public static String guiName = "TelePad Gui";
    public static TelepadGui getGui(Player player)
    {
        if (activeGuis.containsKey(player.getUniqueId())) {
            TelepadGui telepadGui = activeGuis.get(player.getUniqueId());
            if (telepadGui.isGuiOpen()) return telepadGui;
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
        NBTTagCompound nbtTagCompound = TitanTelePads.tools.getNBTTool().getNBTTag(button);
        nbtTagCompound.a("buttonaction", "left");
        button = TitanTelePads.tools.getNBTTool().setNBTTag(button, nbtTagCompound);
        myGui.setItem(size - 9, button.clone());

        redrawBookButton();


        button = new ItemStack(Material.ARROW);
        button = TitanTelePads.tools.getItemStackTool().changeName(button, "Scroll Up");
        nbtTagCompound = TitanTelePads.tools.getNBTTool().getNBTTag(button);
        nbtTagCompound.a("buttonaction", "right");
        button = TitanTelePads.tools.getNBTTool().setNBTTag(button, nbtTagCompound);
        myGui.setItem(size - 1, button.clone());
    }

    public void redrawBookButton() {
        ItemStack button;
        NBTTagCompound nbtTagCompound;
        button = new ItemStack(Material.BOOK);
        if (showingALl) {
            button = new ItemStack(Material.BOOKSHELF);
            button = TitanTelePads.tools.getItemStackTool().changeName(button, "Showing All TelePads");
        } else {
            button = TitanTelePads.tools.getItemStackTool().changeName(button, "Showing Personal TelePads");
        }
        nbtTagCompound = TitanTelePads.tools.getNBTTool().getNBTTag(button);
        nbtTagCompound.a("buttonaction", "switch");
        button = TitanTelePads.tools.getNBTTool().setNBTTag(button, nbtTagCompound);
        myGui.setItem(size - 5, button.clone());
    }

    public void drawMain() {
        ItemStack telepads = new ItemStack(TitanTelePads.configManager.getMaterial());

        for (int i = 0; i < size - 9; i++) {
            ItemStack button = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            if (i + scrollStart < locations.size() ) {
                try {
                    Location l = locations.get(i + scrollStart);
                    Boolean admin = TelePadsManager.instants.isAdmin(l);
                    UUID owner = TelePadsManager.instants.getOwner(l);
                    if (!admin) {
                        String playersTexture = TitanTelePads.tools.getPlayerTool().getPlayersTexture(owner);
                        button = TitanTelePads.tools.getSkullTool().getSkull(playersTexture);
                    }
                    else
                    {
                        button = new ItemStack(TitanTelePads.configManager.getMaterial());
                    }
                    if (TitanTelePads.tools.getItemStackTool().isEmpty(button)) button = telepads.clone();

                    button = TitanTelePads.tools.getItemStackTool().changeName(button, ChatColor.AQUA + "Teleport Pad");
                    List<String> lore = new ArrayList<String>();
                    lore.add("World: " + ChatColor.WHITE + l.getWorld().getName());
                    lore.add("Name: " + ChatColor.WHITE + TelePadsManager.instants.getName(l));
                    lore.add("Private: " + ChatColor.WHITE + TelePadsManager.instants.isPrivate(l));
                    if (admin)
                        lore.add("Owner: " + ChatColor.RED + "ADMIN");
                    else
                        lore.add("Owner: " + ChatColor.WHITE + TelePadsManager.instants.getOwnerName(l));
                    if (owner.equals(viewer.getUniqueId()))
                    {
                        lore.add(ChatColor.GRAY + "Right to set name");
                        lore.add(ChatColor.GRAY + "Shift-Left to change privacy");
                    }
                    if (TitanTelePads.isAdmin(viewer))
                    {
                        lore.add(ChatColor.GRAY + "Shift-Right to change admin status of pad");
                    }
                    button = TitanTelePads.tools.getItemStackTool().addLore(button, lore);
                    NBTTagCompound nbtTagCompound = TitanTelePads.tools.getNBTTool().getNBTTag(button);
                    nbtTagCompound.a("padlocation", TitanTelePads.tools.getSerializeTool().serializeLocation(l));
                    button = TitanTelePads.tools.getNBTTool().setNBTTag(button, nbtTagCompound);
                } catch (IOException e) {
//                    e.printStackTrace();
                }
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
    public void setToggle()
    {
        if (showingALl) setOwnerList();
        else setAllList();
    }
    public void setOwnerList()
    {
        showingALl = false;
        scrollStart = 0;
        locations = TelePadsManager.instants.getAll(viewer.getUniqueId());
    }
    public void setAllList()
    {
        showingALl = true;
        scrollStart = 0;
        locations = TelePadsManager.instants.getAll();
        if (!TitanTelePads.isAdmin(viewer))
        {
            locations.clear();
            for(Location location: TelePadsManager.instants.getAll())
            {
                if (!TelePadsManager.instants.isPrivate(location) || TelePadsManager.instants.getOwner(location).equals(viewer.getUniqueId()))
                {
                    locations.add(location);
                }
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
        setOwnerList();
        mainDraw();
        player.openInventory(myGui);
    }
}
