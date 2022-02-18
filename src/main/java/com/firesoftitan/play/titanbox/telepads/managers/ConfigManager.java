package com.firesoftitan.play.titanbox.telepads.managers;

import com.firesoftitan.play.titanbox.libs.managers.SaveManager;
import com.firesoftitan.play.titanbox.libs.tools.Tools;
import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {
    private SaveManager configFile;

    private Material material;
    private int savetime;
    private int teleportdelay;
    private HashMap<String, String> categories;
    private HashMap<String, Integer> categoriesSlots;
    private String categoryDefault;
    private String categoryDefaultOpen;
    public ConfigManager() {
        reload();
    }
    public void reload()
    {
        configFile = new SaveManager(TitanTelePads.instants.getName(), "config");
        if (!configFile.contains("settings.material"))
        {
            configFile.set("settings.material", "SMOKER");
        }
        if (!configFile.contains("settings.savetimer"))
        {
            configFile.set("settings.savetimer", 600);
        }
        if (!configFile.contains("settings.teleportdelay"))
        {
            configFile.set("settings.teleportdelay", 1);
        }

        if (!configFile.contains("settings.category"))
        {
            configFile.set("settings.category.All.slot", 0);
            configFile.set("settings.category.Admin.slot", 1);
            configFile.set("settings.category.Mine.slot", 2);
            configFile.set("settings.category.Mine.default_open", true);
            configFile.set("settings.category.Events.permission", "titanbox.admin");
            configFile.set("settings.category.Events.slot", 3);
            configFile.set("settings.category.Spawns.permission", "titanbox.admin");
            configFile.set("settings.category.Spawns.slot", 4);
            configFile.set("settings.category.Public.permission", "none");
            configFile.set("settings.category.Public.slot", 5);
            configFile.set("settings.category.Public.default", true);


        }
        categories = new HashMap<String, String>();
        categoriesSlots = new HashMap<String, Integer>();
        for(String key: configFile.getKeys("settings.category"))
        {
            String permission = configFile.getString("settings.category." + key + ".permission");
            int slot = configFile.getInt("settings.category." + key + ".slot");
            categories.put(key, permission);
            categoriesSlots.put(key, slot);
            if (categoryDefaultOpen == null || categoryDefaultOpen.length() < 1) categoryDefaultOpen = key;
            if (configFile.getBoolean("settings.category." + key + ".default")) categoryDefault = key;
            if (configFile.getBoolean("settings.category." + key + ".default_open")) categoryDefaultOpen = key;

        }

        if (categoryDefault == null || categoryDefault.length() < 1 || categoryDefault.equalsIgnoreCase("mine")
                || categoryDefault.equalsIgnoreCase("admin") || categoryDefault.equalsIgnoreCase("all"))
        {
            categoryDefault = "Click to Changed";
        }
        this.teleportdelay = configFile.getInt("settings.teleportdelay");
        this.savetime = configFile.getInt("settings.savetimer");
        try {
            this.material = Material.getMaterial(configFile.getString("settings.material").toUpperCase());
        } catch (Exception e) {
            TitanTelePads.messageTool.sendMessageSystem(configFile.getString("settings.material") + " can't find that material.");
            this.material = Material.END_PORTAL_FRAME;
        }


        configFile.save();

    }
    public List<String> getCategoryNames(Player player)
    {
        List<String> categoryNames = getCategoryNames();
        List<String> permissionCats = new ArrayList<String>();
        for(String key: categoryNames)
        {
            if (!key.equalsIgnoreCase("admin") && !key.equalsIgnoreCase("all") && !key.equalsIgnoreCase("mine")) {
                if (hasCategoryPermission(player, key)) {
                    permissionCats.add(key);
                }
            }
        }
        return permissionCats;
    }
    public List<String> getCategoryNames()
    {
        return new ArrayList<String>(categories.keySet());
    }
    public int getCategorySlot(String category)
    {
        int slot = categoriesSlots.get(category);
        return slot;
    }
    public boolean hasCategoryPermission(Player player, String category)
    {
        String per = categories.get(category);
        if (per == null || per.length() < 1 || per.equalsIgnoreCase("none")) return true;
        if (player.isOp()) return true;
        if (player.hasPermission(per)) return true;
        return false;
    }

    public String getCategoryDefaultOpen() {
        return categoryDefaultOpen;
    }

    public String getCategoryDefault()
    {
        return categoryDefault;
    }
    public Material getMaterial() {
        return this.material;
    }

    public int getSaveTime() {
        return savetime;
    }

    public int getTeleportDelay() {
        return teleportdelay;
    }
}
