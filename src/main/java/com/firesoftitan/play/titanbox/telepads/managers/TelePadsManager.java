package com.firesoftitan.play.titanbox.telepads.managers;

import com.firesoftitan.play.titanbox.libs.managers.SaveManager;
import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.firesoftitan.play.titanbox.telepads.TitanTelePads.tools;


public class TelePadsManager {
    private SaveManager configFile;
    public static TelePadsManager instants;
    private List<Location> output;
    public TelePadsManager() {
        configFile  = new SaveManager(TitanTelePads.instants.getName(), "telepads");
        instants = this;
        output = new ArrayList<Location>();
        for(String key: configFile.getKeys("telepads"))
        {
            Location location = TitanTelePads.tools.getSerializeTool().deserializeLocation(key);
            output.add(location.clone());
        }
    }
    public void save()
    {
        configFile.save();
    }
    public boolean isTelePad(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (configFile.contains("telepads." + key))  return true;
        return false;
    }
    public String getOwnerName(Location location)
    {
        if (isAdmin(location)) return "Admin";
        UUID owner = getOwner(location);
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        String name = player.getName();
        if (name == null || name.length() < 1) return "Admin";
        return name;
    }
    public UUID getOwner(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".owner")) return null;
        return configFile.getUUID("telepads." + key + ".owner");
    }
    public void setName(Location location, String name)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".name", name);
        TitanTelePads.tools.getFloatingTextTool().changeFloatingText(location.clone().add(0.5f, 2, 0.5f),  name);
    }
    public ItemStack getIcon(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".icon"))
        {
            try {
                if (!isAdmin(location)) {
                    String playersTexture = TitanTelePads.tools.getPlayerTool().getPlayersTexture(getOwner(location));
                    ItemStack skull = TitanTelePads.tools.getSkullTool().getSkull(playersTexture);
                    configFile.set("telepads." + key + ".icon", skull);
                }
                else
                {
                    configFile.set("telepads." + key + ".icon", new ItemStack(Material.BOOKSHELF));
                }
            } catch (IOException e) {

            }
        }
        ItemStack item = configFile.getItem("telepads." + key + ".icon");
        if (item.getType() == Material.WHITE_CARPET)
        {
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(70001);
            item.setItemMeta(itemMeta);
        }
        return item.clone();
    }
    public void setIcon(Location location, ItemStack icon) {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        icon = TitanTelePads.tools.getItemStackTool().changeName(icon, "icon");
        icon = TitanTelePads.tools.getItemStackTool().clearLore(icon);
        configFile.set("telepads." + key + ".icon", icon.clone());
    }
    public String getCategory(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".category"))
        {
            configFile.set("telepads." + key + ".category", TitanTelePads.configManager.getCategoryDefault());
        }
        return configFile.getString("telepads." + key + ".category");
    }
    public void setCategory(Location location, String cat)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".category", cat);
    }
    public String getName(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".name")) return null;
        return configFile.getString("telepads." + key + ".name");
    }
    public void setPrivate(Location location, Boolean priv)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".private", priv);
    }
    public Boolean isPrivate(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".private")) return false;
        return configFile.getBoolean("telepads." + key + ".private");
    }
    public void setAdmin(Location location, Boolean admin)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".admin", admin);
    }
    public Boolean isAdmin(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".admin")) return false;
        return configFile.getBoolean("telepads." + key + ".admin");
    }
    public void removeTelePad(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        TitanTelePads.tools.getFloatingTextTool().deleteFloatingText(location.clone().add(0.5f, 2, 0.5f));
        TitanTelePads.tools.getFloatingTextTool().deleteFloatingText(location.clone().add(0.5f, 0, 0.5f));
        configFile.delete("telepads." + key);
        for(int i = 0; i < output.size(); i++)
        {
            Location locationA = output.get(i);
            if (TitanTelePads.tools.getLocationTool().isLocationsEqual(location, locationA))
            {
                output.remove(i);
                return;
            }
        }

    }
    public void placeTelePad(Location location, Player owner, String name)
    {
        placeTelePad(location, owner.getUniqueId(), name);
    }
    public void placeTelePad(Location location, UUID owner, String name)
    {
        placeTelePad(location, owner, name, false, false);
    }
    public void placeTelePad(Location location, Player owner, String name, Boolean privacy, Boolean admin)
    {
        placeTelePad(location, owner.getUniqueId(), name, privacy, admin);
    }
    public void placeTelePad(Location location, UUID owner, String name, Boolean privacy, Boolean admin)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".owner", owner);
        configFile.set("telepads." + key + ".name", name);
        configFile.set("telepads." + key + ".private", privacy);
        configFile.set("telepads." + key + ".admin", admin);
        configFile.set("telepads." + key + ".category", TitanTelePads.configManager.getCategoryDefault());

        try {
            if (!admin) {
                String playersTexture = TitanTelePads.tools.getPlayerTool().getPlayersTexture(owner);
                ItemStack skull = TitanTelePads.tools.getSkullTool().getSkull(playersTexture);
                configFile.set("telepads." + key + ".icon", skull);
            }
            else
            {
                configFile.set("telepads." + key + ".icon", new ItemStack(Material.BOOKSHELF));
            }
        } catch (IOException e) {

        }
        output.add(location.clone());
        TitanTelePads.tools.getFloatingTextTool().setFloatingText(location.add(0.5f, 2, 0.5f), name);
    }
    public List<Location> getAll(String category)
    {
        List<Location> owners = new ArrayList<Location>();
        for(Location location: output)
        {
            if (getOwner(location) != null) {
                if (getCategory(location).equals(category)) {
                    owners.add(location.clone());
                }
            }
        }
        return owners;
    }
    public List<Location> getAllAdmin()
    {
        List<Location> owners = new ArrayList<Location>();
        for(Location location: output)
        {
            if (isAdmin(location)) {
                owners.add(location.clone());
            }
        }
        return owners;
    }
    public List<Location> getAll(UUID owner)
    {
        List<Location> owners = new ArrayList<Location>();
        for(Location location: output)
        {
            if (getOwner(location) != null) {
                if (getOwner(location).equals(owner) && !isAdmin(location)) {
                    owners.add(location.clone());
                }
            }
        }
        return owners;
    }
    public List<Location> getAll()
    {
        List<Location> all = new ArrayList<Location>(output);
        return all;
    }
}
