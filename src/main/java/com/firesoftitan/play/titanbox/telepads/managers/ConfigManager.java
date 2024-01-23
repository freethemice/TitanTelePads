package com.firesoftitan.play.titanbox.telepads.managers;

import com.firesoftitan.play.titanbox.libs.managers.SaveManager;
import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {
    private SaveManager configFile;
    private int savetime;
    private int teleportdelay;
    private HashMap<String, String> categories;
    private HashMap<String, Integer> categoriesSlots;
    private String categoryDefault;
    private String categoryDefaultOpen;
    private String resourcePackURL;
    private boolean resourcePackEnabled;
    private boolean gui_enabled;
    private String language;
    public ConfigManager() {
        reload();
    }
    public void reload()
    {
        configFile = new SaveManager(TitanTelePads.instants.getName(), "config");
        if (configFile.contains("settings.material"))
        {
            configFile.delete("settings.material");
        }
        if (configFile.contains("settings.enable_old_vanilla_only_version"))
        {
            configFile.delete("settings.enable_old_vanilla_only_version");
        }
        if (!configFile.contains("settings.savetimer"))
        {
            configFile.set("settings.savetimer", 600);
        }
        if (!configFile.contains("settings.teleportdelay"))
        {
            configFile.set("settings.teleportdelay", 1);
        }

        if (!configFile.contains("settings.resourcepack.url") ||
                configFile.getString("settings.resourcepack.url").equals("http://play.firesoftitan.com/global/022022/TitanBox.zip") ||
                configFile.getString("settings.resourcepack.url").equals("http://play.firesoftitan.com/global/030122/TitanBox.zip") ||
                configFile.getString("settings.resourcepack.url").equals("http://play.firesoftitan.com/global/031522/TitanBox.zip") ||
                configFile.getString("settings.resourcepack.url").equals("http://play.firesoftitan.com/global/122122/TitanTelepads.zip"))
        {

            configFile.set("settings.resourcepack.url", "http://play.firesoftitan.com/global/012324/TitanPack.zip");
        }
        if (!configFile.contains("settings.resourcepack.gui_enabled"))
        {
            configFile.set("settings.resourcepack.gui_enabled", true);
        }
        if (!configFile.contains("settings.resourcepack.enabled"))
        {
            configFile.set("settings.resourcepack.enabled", true);
        }
        if (!configFile.contains("settings.language"))
        {
            configFile.set("settings.language", "en_us");
        }
        if (!configFile.contains("settings.resourcePackEnabled"))
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
        this.resourcePackURL = configFile.getString("settings.resourcepack.url");
        this.resourcePackEnabled = configFile.getBoolean("settings.resourcepack.enabled");

        this.teleportdelay = configFile.getInt("settings.teleportdelay");
        this.savetime = configFile.getInt("settings.savetimer");

        this.gui_enabled = configFile.getBoolean("settings.resourcepack.gui_enabled");
        this.language = configFile.getString("settings.language");
        if (this.language == null) this.language = "en_us";
        configFile.save();

    }


    public String getLanguage() {
        return language;
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

    public boolean isGui_enabled() {
        return gui_enabled;
    }

    public String getResourcePackURL() {
        return resourcePackURL;
    }

    public boolean isResourcePackEnabled() {
        return resourcePackEnabled;
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
        return Material.WHITE_CARPET;
    }

    public int getSaveTime() {
        return savetime;
    }

    public int getTeleportDelay() {
        return teleportdelay;
    }
}
