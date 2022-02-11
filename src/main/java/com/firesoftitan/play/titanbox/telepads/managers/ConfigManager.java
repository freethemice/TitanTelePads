package com.firesoftitan.play.titanbox.telepads.managers;

import com.firesoftitan.play.titanbox.libs.managers.SaveManager;
import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import org.bukkit.Material;

public class ConfigManager {
    private SaveManager configFile;

    private Material material;
    private int savetime;
    private int teleportdelay;

    public ConfigManager() {
        reload();
    }
    public void reload()
    {        configFile = new SaveManager(TitanTelePads.instants.getName(), "config");
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
