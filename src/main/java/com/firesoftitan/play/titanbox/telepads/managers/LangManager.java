package com.firesoftitan.play.titanbox.telepads.managers;

import com.firesoftitan.play.titanbox.libs.managers.SaveManager;
import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import org.bukkit.ChatColor;

public class LangManager {
    private SaveManager configFile;
    private String lang_file;
    public static LangManager instants;

    public LangManager(String lang_file) {
        this.lang_file = lang_file;
        configFile = new SaveManager(TitanTelePads.instants.getName(), "lang" , this.lang_file);
        instants = this;
    }
    public boolean contains(String key)
    {
        return this.configFile.contains(key);
    }

    public String getMessage(String key) {
        if (!contains(key)) return ChatColor.RED + "KEY NOT FOUND" + key;
        String string = this.configFile.getString(key);
        string = ChatColor.translateAlternateColorCodes('&',string);
        return string;
    }
}
