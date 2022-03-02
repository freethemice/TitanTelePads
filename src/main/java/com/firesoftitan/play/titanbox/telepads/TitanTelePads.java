package com.firesoftitan.play.titanbox.telepads;

import com.firesoftitan.play.titanbox.libs.tools.LibsMessageTool;
import com.firesoftitan.play.titanbox.libs.tools.Tools;
import com.firesoftitan.play.titanbox.telepads.enums.TitanItemTypesEnum;
import com.firesoftitan.play.titanbox.telepads.listeners.MainListener;
import com.firesoftitan.play.titanbox.telepads.managers.AutoUpdateManager;
import com.firesoftitan.play.titanbox.telepads.managers.ConfigManager;
import com.firesoftitan.play.titanbox.telepads.managers.RecipeManager;
import com.firesoftitan.play.titanbox.telepads.managers.TelePadsManager;
import com.firesoftitan.play.titanbox.telepads.runnables.SaveRunnable;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    public static RecipeManager recipeManager;
    public static ConfigManager configManager;
    public static TitanTelePads instants;
    public static MainListener mainListener;
    public static boolean update = false;
    public static LibsMessageTool messageTool;
    public void onEnable() {
        instants = this;
        tools = new Tools(this, new SaveRunnable());
        messageTool = tools.getMessageTool();
        mainListener = new MainListener();
        mainListener.registerEvents();
        configManager = new ConfigManager();
        recipeManager = new RecipeManager();
        new BukkitRunnable() {
            @Override
            public void run() {
                new TelePadsManager();
            }
        }.runTaskLater(this, 2);
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
                        return true;
                    }
                    if (name.equals("give"))
                    {
                        if (args.length > 1)
                        {
                            if (args.length > 2) {
                                for (TitanItemTypesEnum typesEnum : TitanItemTypesEnum.values()) {
                                    if (args[1].toUpperCase().equalsIgnoreCase(typesEnum.getID())) {
                                        Player player = Bukkit.getPlayer(args[2]);
                                        ItemStack telepads = getPartItem(typesEnum);
                                        player.getInventory().addItem(telepads.clone());
                                        return true;
                                    }
                                }
                            }
                            Player player = Bukkit.getPlayer(args[1]);
                            ItemStack telepads = getTelePadItem(System.currentTimeMillis() + "", false, false, null, configManager.getCategoryDefault());
                            player.getInventory().addItem(telepads.clone());
                            return true;
                        }
                    }
                }
            }
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                messageTool.sendMessagePlayer(player, "/tep reload - Reloads config files");
                messageTool.sendMessagePlayer(player, "/tep give <name> - Give player (name) a telepad");
                messageTool.sendMessagePlayer(player, "/tep give <telepad,wires,wiring_box,teleporter_box> <name> - Give player (name) a telepad ,wires, wiring_box, or teleporter_box");
            }
            else
            {
                messageTool.sendMessageSystem("/tep reload - Reloads config files");
                messageTool.sendMessageSystem("/tep give <name> - Give player (name) a telepad");
                messageTool.sendMessageSystem("/tep give <telepads,wires,wiring_box,teleporter_box> <name> - Give player (name) a telepads ,wires, wiring_box, or teleporter_box");
            }
        }

        return true;
    }
    public static boolean isItemTitanItem(TitanItemTypesEnum ItemType, ItemStack check)
    {
        if (tools.getItemStackTool().isEmpty(check)) return false;
        String titanitem_id = tools.getItemStackTool().getTitanItemID(check);
        if (titanitem_id.equals(ItemType.getID())) return true;
        return false;
    }
    @NotNull
    public static ItemStack getPartItem(TitanItemTypesEnum ItemType) {
        if (ItemType == TitanItemTypesEnum.TELEPAD)
        {
            return getTelePadItem(System.currentTimeMillis() + "", false, false, null, configManager.getCategoryDefault());
        }
        ItemStack telepads = new ItemStack(ItemType.getMaterial());
        telepads = tools.getItemStackTool().setTitanItemID(telepads, ItemType.getID());
        telepads = tools.getItemStackTool().setPlaceable(telepads, ItemType.isPlaceable());
        telepads = tools.getItemStackTool().changeName(telepads, ChatColor.AQUA + ItemType.getName());
        telepads = tools.getItemStackTool().addLore(true, telepads, ItemType.getLore());

        if (!configManager.isEnableVanillaOnly()) {
            ItemMeta itemMeta = telepads.getItemMeta();
            itemMeta.setCustomModelData(ItemType.getDataID());
            telepads.setItemMeta(itemMeta);
        }
        return telepads.clone();
    }
    @NotNull
    public static ItemStack getTelePadItem(String name, boolean admin, boolean privacy, ItemStack icon, String category) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
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
        telepads = tools.getItemStackTool().setTitanItemID(telepads, TitanItemTypesEnum.TELEPAD.getID());
        telepads = tools.getItemStackTool().setPlaceable(telepads, TitanItemTypesEnum.TELEPAD.isPlaceable());
        telepads = tools.getItemStackTool().changeName(telepads, ChatColor.AQUA + TitanItemTypesEnum.TELEPAD.getName());
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
            itemMeta.setCustomModelData(TitanItemTypesEnum.TELEPAD.getDataID());
            telepads.setItemMeta(itemMeta);
        }
        return telepads.clone();
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
