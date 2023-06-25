package com.firesoftitan.play.titanbox.telepads;

import com.firesoftitan.play.titanbox.libs.managers.HologramManager;
import com.firesoftitan.play.titanbox.libs.tools.LibsMessageTool;
import com.firesoftitan.play.titanbox.libs.tools.Tools;
import com.firesoftitan.play.titanbox.telepads.enums.TitanItemTypesEnum;
import com.firesoftitan.play.titanbox.telepads.listeners.MainListener;
import com.firesoftitan.play.titanbox.telepads.listeners.PluginListener;
import com.firesoftitan.play.titanbox.telepads.listeners.TabCompleteListener;
import com.firesoftitan.play.titanbox.telepads.managers.*;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.*;
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
        saveDefaultFiles();

        configManager = new ConfigManager();
        new LangManager(configManager.getLanguage());
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

    private void saveDefaultFiles() {
        List<String> jarFileList = getJarFiles();
        for(String s: jarFileList)
        {
            System.out.println(s);
            saveDefaultFile(s);
        }
    }

    private void saveDefaultFile(String fileName) {
        String jarFileName = fileName;
        fileName = fileName.substring(9);
        File file = new File(getDataFolder(), fileName);

        if (!file.exists()) {
            // Create the parent directories if they don't exist
            file.getParentFile().mkdirs();

            try (InputStream inputStream = getClass().getResourceAsStream( jarFileName)) {
                if (inputStream != null) {
                    // Copy the file from the JAR to the plugin folder
                    if (!file.exists()) Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    getLogger().warning(LangManager.instants.getMessage("error.default_file") + fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private List<String> getJarFiles() {
        String directoryName = "defaults";
        List<String> jarFiles = new ArrayList<>();

        try {
            // Get the JAR file path
            Path jarPath = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

            if (jarPath != null && Files.isRegularFile(jarPath)) {
                // Open the JAR file system
                try (FileSystem jarFileSystem = FileSystems.newFileSystem(jarPath)) {
                    // Specify the path of the directory within the JAR
                    Path directoryPath = jarFileSystem.getPath("/" + directoryName);

                    // Iterate over all files in the specified directory in the JAR
                    Files.walk(directoryPath)
                            .filter(Files::isRegularFile)
                            .forEach(filePath -> jarFiles.add(filePath.toString()));
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return jarFiles;
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
        try {
            if (isAdmin(sender)) {
                if (label.equalsIgnoreCase("telepad") || label.equalsIgnoreCase("telepads") || label.equalsIgnoreCase("tep")) {
                    if (args.length > 0) {
                        String name = args[0];
                        if (name.equals("reload"))
                        {
                            configManager.reload();
                            if (sender instanceof Player) messageTool.sendMessagePlayer((Player) sender, LangManager.instants.getMessage("reloaded"));
                            else messageTool.sendMessageSystem(LangManager.instants.getMessage("reloaded"));
                            return true;
                        }
                        if (name.equals("remove"))
                        {
                            if (!(sender instanceof Player))
                            {
                                messageTool.sendMessageSystem(LangManager.instants.getMessage("error.console"));
                                return true;
                            }
                            boolean safety = true;
                            if (args[1].equalsIgnoreCase("holograms")) {
                                safety = true;
                            }
                            else if (args[1].equalsIgnoreCase("armorstand")) {
                                safety = false;
                            }
                            else
                            {
                                return true;
                            }
                                try {
                                    if (safety) {
                                        List<HologramManager> holograms = new ArrayList<HologramManager>();
                                        if (args[2].equalsIgnoreCase("all")) {
                                            holograms = tools.getHologramTool().getHolograms();
                                        } else {
                                            int d = Integer.parseInt(args[2]);
                                            holograms = tools.getHologramTool().getHolograms(((Player) sender).getLocation(), d, d, d);
                                        }
                                        for (HologramManager hologramManager : holograms) {
                                            hologramManager.delete();
                                        }
                                        messageTool.sendMessagePlayer((Player) sender, holograms.size() + LangManager.instants.getMessage("holograms_removed"));
                                    } else {
                                        if (args[2].equalsIgnoreCase("all")) {
                                            messageTool.sendMessagePlayer((Player) sender, LangManager.instants.getMessage("error.unsafe"));
                                            return true;
                                        }

                                        int d = Integer.parseInt(args[2]);
                                        int i = 0;
                                        for (Entity entity : ((Player) sender).getNearbyEntities(d, d, d)) {
                                            if (entity.getType() == EntityType.ARMOR_STAND) {
                                                entity.remove();
                                                i++;
                                            }
                                        }
                                        messageTool.sendMessagePlayer((Player) sender, i + LangManager.instants.getMessage("armor_stands_removed"));
                                    }
                                } catch (NumberFormatException e) {
                                    messageTool.sendMessagePlayer((Player) sender, args[2] + LangManager.instants.getMessage("error.number"));
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
                    for(int i = 0; i < 100; i++)
                    {
                        if (LangManager.instants.contains("help." + i))
                            player.sendMessage(LangManager.instants.getMessage("help." + i));
                    }
                }
                else
                {
                    for(int i = 0; i < 100; i++)
                    {
                        if (LangManager.instants.contains("help.c" + i))
                            messageTool.sendMessageSystem(LangManager.instants.getMessage("help.c" + i));
                    }
                }
            }
        } catch (Exception e) {
            if (sender instanceof Player)
            {
                messageTool.sendMessagePlayer((Player) sender,LangManager.instants.getMessage("error.understand"));
            }
            else
            {
                messageTool.sendMessageSystem(LangManager.instants.getMessage("error.understand"));
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
        telepads = tools.getItemStackTool().changeName(telepads, LangManager.instants.getMessage("items.telepad.name"));
        if (admin) telepads = tools.getItemStackTool().changeName(telepads, LangManager.instants.getMessage("items.telepad.admin") +  LangManager.instants.getMessage("items.telepad.name"));

        List<String> lores = new ArrayList<String>();
        String privacylore = LangManager.instants.getMessage("items.telepad.public");
        if (privacy) privacylore = LangManager.instants.getMessage("items.telepad.private");

        lores.add(LangManager.instants.getMessage("items.telepad.name2") + ChatColor.WHITE + name);
        if (!tools.getItemStackTool().isEmpty(icon)) lores.add(LangManager.instants.getMessage("items.telepad.icon"));
        lores.add(privacylore);
        if (category != null && category.length() > 1) lores.add(LangManager.instants.getMessage("items.telepad.category") +  ChatColor.WHITE + category);

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
