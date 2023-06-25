package com.firesoftitan.play.titanbox.telepads;

import com.firesoftitan.play.titanbox.libs.managers.HologramManager;
import com.firesoftitan.play.titanbox.libs.tools.LibsMessageTool;
import com.firesoftitan.play.titanbox.libs.tools.Tools;
import com.firesoftitan.play.titanbox.telepads.enums.TitanItemTypesEnum;
import com.firesoftitan.play.titanbox.telepads.listeners.MainListener;
import com.firesoftitan.play.titanbox.telepads.listeners.PluginListener;
import com.firesoftitan.play.titanbox.telepads.listeners.TabCompleteListener;
import com.firesoftitan.play.titanbox.telepads.managers.ChatMessageManager;
import com.firesoftitan.play.titanbox.telepads.managers.ConfigManager;
import com.firesoftitan.play.titanbox.telepads.managers.RecipeManager;
import com.firesoftitan.play.titanbox.telepads.managers.TelePadsManager;
import com.firesoftitan.play.titanbox.telepads.runnables.SaveRunnable;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
    public static LibsMessageTool messageTool;
    public static ChatMessageManager chatMessageManager;
    public static PluginListener pluginListener;
    public void onEnable() {
        instants = this;
        tools = new Tools(this, new SaveRunnable(), 99835);
        messageTool = tools.getMessageTool();
        chatMessageManager = new ChatMessageManager();
        mainListener = new MainListener();
        mainListener.registerEvents();
        if (isBungee())
        {
            pluginListener = new PluginListener();
            pluginListener.registerEvents("titanbox:1");
            messageTool.sendMessageSystem("Bungee cord server enabled.");
        }
        configManager = new ConfigManager();
        recipeManager = new RecipeManager();
        new BukkitRunnable() {
            @Override
            public void run() {
                new TelePadsManager();
            }
        }.runTaskLater(this, 2);
        this.getCommand("telepads").setTabCompleter(new TabCompleteListener());
        this.getCommand("telepad").setTabCompleter(new TabCompleteListener());
        this.getCommand("tep").setTabCompleter(new TabCompleteListener());
    }
    public static String getNext(List<String> myList, String uid) {
        int idx = myList.indexOf(uid);
        if (idx < 0 || idx+1 == myList.size()) return myList.get(0);
        return myList.get(idx + 1);
    }
    public boolean isBungee()
    {
        ConfigurationSection settings = getServer().spigot().getConfig();
        boolean bungeecord = settings.getBoolean("settings.bungeecord");
        return bungeecord;
    }
    public void togglePlayerChat(Player player, boolean canChat) {
        if (isBungee()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("chat:toggle"); // the channel could be whatever you want
            out.writeUTF(player.getUniqueId().toString()); // this data could be whatever you want
            out.writeUTF(canChat + "");
            getServer().sendPluginMessage(this, "titanbox:1", out.toByteArray());
        }
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
                    if (name.equals("remove"))
                    {
                        if (!(sender instanceof Player))
                        {
                            messageTool.sendMessageSystem("Only players can use this command.");
                            return true;
                        }
                        boolean safety = true;
                        if (args.length > 2)
                        {
                            safety = Boolean.parseBoolean(args[2]);
                        }
                        try {
                            if (safety) {
                                List<HologramManager> holograms = new ArrayList<HologramManager>();
                                if (args[1].equalsIgnoreCase("all")) {
                                    holograms = tools.getHologramTool().getHolograms();
                                } else {
                                    int d = Integer.parseInt(args[1]);
                                    holograms = tools.getHologramTool().getHolograms(((Player) sender).getLocation(), d, d, d);
                                }
                                for (HologramManager hologramManager : holograms) {
                                    hologramManager.delete();
                                }
                                messageTool.sendMessagePlayer((Player) sender, holograms.size() + " holograms removed.");
                            }
                            else
                            {
                                if (args[1].equalsIgnoreCase("all")) {
                                    messageTool.sendMessagePlayer((Player) sender, "You can't use all on unsafe removal.");
                                    return true;
                                }

                                int d = Integer.parseInt(args[1]);
                                int i = 0;
                                for (Entity entity: ((Player) sender).getNearbyEntities(d, d, d))
                                {
                                    if (entity.getType() == EntityType.ARMOR_STAND)
                                    {
                                        entity.remove();
                                        i++;
                                    }
                                }
                                messageTool.sendMessagePlayer((Player) sender, i + " armor stands removed.");
                            }
                        } catch (NumberFormatException e) {
                            messageTool.sendMessagePlayer((Player) sender, args[1] + " isn't a number");
                            return true;
                        }
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
                messageTool.sendMessagePlayer(player, ChatColor.GOLD + "----- Admin Commands -----");
                messageTool.sendMessagePlayer(player, ChatColor.GOLD + "/tep " + ChatColor.WHITE + "reload " + ChatColor.AQUA + "- Reloads config files");
                messageTool.sendMessagePlayer(player, ChatColor.GOLD + "/tep " + ChatColor.WHITE + "give " + ChatColor.GRAY + "<name> " + ChatColor.AQUA + "- Give player (player) a telepad");
                messageTool.sendMessagePlayer(player, ChatColor.GOLD + "/tep " + ChatColor.WHITE + "give " + ChatColor.GRAY + "<telepad, wires, wiring_box, teleporter_box> <player> " + ChatColor.AQUA + "- Give player a telepad ,wires, wiring_box, or teleporter_box");
                messageTool.sendMessagePlayer(player, ChatColor.GOLD + "/tep " + ChatColor.WHITE + "remove " + ChatColor.GRAY + "<all/distance from player> " + ChatColor.RED + "- removes all holograms.");
                messageTool.sendMessagePlayer(player, ChatColor.GOLD + "/tep " + ChatColor.WHITE + "remove " + ChatColor.GRAY + "<distance from player> false " + ChatColor.RED + "- removes all armor stands.");
                messageTool.sendMessagePlayer(player, ChatColor.GOLD + "----- Admin Commands -----");
            }
            else
            {
                messageTool.sendMessageSystem("/tep reload - Reloads config files");
                messageTool.sendMessageSystem("/tep give <player> - Give player (name) a telepad");
                messageTool.sendMessageSystem("/tep give <telepads, wires, wiring_box, teleporter_box> <player> - Give player (name) a telepads ,wires, wiring_box, or teleporter_box");
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

        ItemMeta itemMeta = telepads.getItemMeta();
        itemMeta.setCustomModelData(ItemType.getDataID());
        telepads.setItemMeta(itemMeta);

        return telepads.clone();
    }
    @NotNull
    public static ItemStack getTelePadItem(String name, boolean admin, boolean privacy, ItemStack icon, String category) {
        ItemStack telepads = new ItemStack(configManager.getMaterial());
        telepads = tools.getNBTTool().set(telepads, "telepad_name" , name);
        telepads = tools.getNBTTool().set(telepads, "telepad_admin" , admin);
        telepads = tools.getNBTTool().set(telepads, "telepad_privacy" , privacy);
        if (category != null && category.length() > 1) {
            telepads = tools.getNBTTool().set(telepads, "telepad_category" , category);
        }
        if (!tools.getItemStackTool().isEmpty(icon)) {
            String itemStack = tools.getSerializeTool().serializeItemStack(icon.clone());
            telepads = tools.getNBTTool().set(telepads, "telepad_icon" , itemStack);
        }
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

        ItemMeta itemMeta = telepads.getItemMeta();
        itemMeta.setCustomModelData(TitanItemTypesEnum.TELEPAD.getDataID());
        telepads.setItemMeta(itemMeta);

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
