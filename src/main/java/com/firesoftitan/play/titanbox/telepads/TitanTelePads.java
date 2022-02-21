package com.firesoftitan.play.titanbox.telepads;

import com.firesoftitan.play.titanbox.libs.tools.LibsMessageTool;
import com.firesoftitan.play.titanbox.libs.tools.Tools;
import com.firesoftitan.play.titanbox.telepads.listeners.MainListener;
import com.firesoftitan.play.titanbox.telepads.managers.AutoUpdateManager;
import com.firesoftitan.play.titanbox.telepads.managers.ConfigManager;
import com.firesoftitan.play.titanbox.telepads.managers.TelePadsManager;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class TitanTelePads extends JavaPlugin {
    public static Tools tools;
    public static ConfigManager configManager;
    public static TitanTelePads instants;
    public static MainListener mainListener;
    public static boolean update = false;
    public static LibsMessageTool messageTool;
    public void onEnable() {
        instants = this;
        tools = new Tools(this);
        messageTool = tools.getMessageTool();
        mainListener = new MainListener();
        mainListener.registerEvents();
        configManager = new ConfigManager();
        new BukkitRunnable() {
            @Override
            public void run() {
                new TelePadsManager();
            }
        }.runTaskLater(this, 2);
        new BukkitRunnable() {
            @Override
            public void run() {
                TelePadsManager.instants.save();
            }
        }.runTaskTimer(this, configManager.getSaveTime() *20, configManager.getSaveTime() *20);

        new BukkitRunnable() {
            @Override
            public void run() {
                new AutoUpdateManager(TitanTelePads.this, 99835).getVersion(version -> {
                    if (TitanTelePads.this.getDescription().getVersion().equalsIgnoreCase(version)) {
                        messageTool.sendMessageSystem("Plugin is up to date.");
                    } else {
                        TitanTelePads.update = true;
                        messageTool.sendMessageSystem("There is a new update available.");
                        messageTool.sendMessageSystem( "https://www.spigotmc.org/resources/titan-teleport-pads.99835");
                    }
                });
            }
        }.runTaskLater(this,20);
    }
    public static String getNext(List<String> myList, String uid) {
        int idx = myList.indexOf(uid);
        if (idx < 0 || idx+1 == myList.size()) return myList.get(0);
        return myList.get(idx + 1);
    }
    public void onDisable()
    {
        this.saveALL();
    }
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        if (isAdmin(sender)) {
            if (label.equalsIgnoreCase("telepad") || label.equalsIgnoreCase("telepads") || label.equalsIgnoreCase("tep")) {
                if (args.length > 0) {
                    String name = args[0];
                    if (name.equals("reload"))
                    {
                        configManager.reload();
                        if (sender instanceof Player) messageTool.sendMessagePlayer((Player) sender, "config reloaded!");
                        else messageTool.sendMessageSystem("config reloaded!");
                    }
                    if (name.equals("give"))
                    {
                        if (args.length > 1)
                        {
                            Player player = Bukkit.getPlayer(args[1]);
                            ItemStack telepads = getTelePadItem(System.currentTimeMillis() + "", false, false, null, configManager.getCategoryDefault());
                            player.getInventory().addItem(telepads.clone());
                            return true;
                        }
                    }
                }
            }
        }
        return true;
    }
    @NotNull
    public static ItemStack getTelePadItem(String name, boolean admin, boolean privacy, ItemStack icon, String category) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.a("telepad" , true);
        nbtTagCompound.a("telepad_name" , name);
        nbtTagCompound.a("telepad_admin" , admin);
        nbtTagCompound.a("telepad_privacy" , privacy);
        if (category != null && category.length() > 1)
        {
            nbtTagCompound.a("telepad_category" , category);
        }
        if (!tools.getItemStackTool().isEmpty(icon))
        {
            String itemStack = tools.getSerializeTool().serializeItemStack(icon.clone());
            nbtTagCompound.a("telepad_icon" , itemStack);
        }
        ItemStack telepads = new ItemStack(configManager.getMaterial());
        telepads = tools.getNBTTool().setNBTTag(telepads, nbtTagCompound);
        telepads = tools.getItemStackTool().changeName(telepads, ChatColor.AQUA + "Teleport Pad");
        if (admin) telepads = tools.getItemStackTool().changeName(telepads, ChatColor.RED + "ADMIN " + ChatColor.AQUA + "Teleport Pad");

        List<String> lores = new ArrayList<String>();
        String privacylore = "Public";
        if (privacy) privacylore = "Private";

        lores.add("Name: " + ChatColor.WHITE + name);
        if (!tools.getItemStackTool().isEmpty(icon)) lores.add(ChatColor.GREEN + "Icon saved!");
        lores.add(privacylore);
        if (category != null && category.length() > 1) lores.add("Category: " +  ChatColor.WHITE + category);

        telepads = tools.getItemStackTool().addLore(true, telepads, lores);
        if (!configManager.isEnableVanillaOnly()) {
            ItemMeta itemMeta = telepads.getItemMeta();
            itemMeta.setCustomModelData(70001);
            telepads.setItemMeta(itemMeta);
        }
        return telepads;
    }

    public void saveALL()
    {
        TelePadsManager.instants.save();
    }
    public static boolean isAdmin(CommandSender sender)
    {
        if (sender.isOp() || sender.hasPermission("titanbox.admin")) return true;
        return false;
    }
}
