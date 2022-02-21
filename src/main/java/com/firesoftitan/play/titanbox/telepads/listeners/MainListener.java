package com.firesoftitan.play.titanbox.telepads.listeners;

import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import com.firesoftitan.play.titanbox.telepads.guis.TelepadGui;
import com.firesoftitan.play.titanbox.telepads.guis.TelepadSettingsGui;
import com.firesoftitan.play.titanbox.telepads.managers.PressureManager;
import com.firesoftitan.play.titanbox.telepads.managers.TelePadsManager;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.firesoftitan.play.titanbox.telepads.TitanTelePads.*;

public class MainListener  implements Listener {

    private static HashMap<UUID, Location> changeNames = new HashMap<UUID, Location>();
    private static HashMap<UUID, Location> changeIcons = new HashMap<UUID, Location>();
    public MainListener(){

    }
    public void registerEvents(){
        PluginManager pm = instants.getServer().getPluginManager();
        pm.registerEvents(this, instants);
    }

    @EventHandler
    public static void onPlayerLoginEvent(PlayerLoginEvent event)
    {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (configManager.isResourcePackEnabled()) player.setResourcePack(configManager.getResourcePackURL());
            }
        }.runTaskLater(instants, 20);

        if (isAdmin(player)) {
            if (update) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        messageTool.sendMessagePlayer(player,"There is a new update available.");
                        messageTool.sendMessagePlayer(player, "https://www.spigotmc.org/resources/titan-teleport-pads.99835/");

                    }
                }.runTaskLater(instants, 20);
            }
        }


    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public static void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        if (changeIcons.containsKey(player.getUniqueId()))
        {
            event.setCancelled(true);
            Location location = changeIcons.get(player.getUniqueId());
            changeIcons.remove(player.getUniqueId());
            if (!event.getMessage().equalsIgnoreCase("cancel"))
            {
                UUID owner = TelePadsManager.instants.getOwner(location);
                if (owner.equals(player.getUniqueId()) || isAdmin(player)) {
                    if (event.getMessage().equalsIgnoreCase("hand"))
                    {
                        TelePadsManager.instants.setIcon(location, player.getInventory().getItemInMainHand().clone());
                    }
                    else
                    {
                        ItemStack skull = tools.getSkullTool().getSkull(event.getMessage());
                        TelePadsManager.instants.setIcon(location, skull);
                    }
                    messageTool.sendMessagePlayer(player, "Icon changed!");
                    TelepadSettingsGui settingsGui = TelepadSettingsGui.getGui(player);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            settingsGui.showGUI(player, settingsGui.getLocations());
                        }
                    }.runTaskLater(instants, 1);
                    return;
                }
            }
            messageTool.sendMessagePlayer(player, "Icon changed CANCELED!");
        }
        if (changeNames.containsKey(player.getUniqueId()))
        {
            event.setCancelled(true);
            Location location = changeNames.get(player.getUniqueId());
            changeNames.remove(player.getUniqueId());
            if (!event.getMessage().equalsIgnoreCase("cancel"))
            {
                UUID owner = TelePadsManager.instants.getOwner(location);
                if (owner.equals(player.getUniqueId()) || isAdmin(player)) {
                    TelePadsManager.instants.setName(location, ChatColor.translateAlternateColorCodes('&', event.getMessage()));
                    messageTool.sendMessagePlayer(player, "Name changed!");
                    TelepadSettingsGui settingsGui = TelepadSettingsGui.getGui(player);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            settingsGui.showGUI(player, settingsGui.getLocations());
                        }
                    }.runTaskLater(instants, 1);

                    return;
                }
            }
            messageTool.sendMessagePlayer(player, "Name changed CANCELED!");
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {

    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        InventoryView openInventory = whoClicked.getOpenInventory();
        Inventory clickedInventory = event.getClickedInventory();
        TelepadGuiClicked(event, whoClicked, openInventory, clickedInventory);
        TelepadSettingsGuiClicked(event, whoClicked, openInventory, clickedInventory);

    }
    private void TelepadSettingsGuiClicked(InventoryClickEvent event, HumanEntity whoClicked, InventoryView openInventory, Inventory clickedInventory) {
        if (openInventory.getTitle().equals(TelepadSettingsGui.guiName)) {
            event.setCancelled(true);
            TelepadSettingsGui telepadGui = TelepadSettingsGui.getGui((Player) whoClicked);
            if (telepadGui != null)
            {
                Location locations = telepadGui.getLocations();
                boolean isOwner = TelePadsManager.instants.getOwner(locations).equals(whoClicked.getUniqueId());
                if (event.getSlot() > -1 && event.getSlot() < telepadGui.getSize()) {
                    ItemStack clicked = clickedInventory.getItem(event.getSlot());
                    if (!tools.getItemStackTool().isEmpty(clicked)) {
                        NBTTagCompound nbtTag = tools.getNBTTool().getNBTTag(clicked);
                        if (nbtTag != null) {
                            String action = nbtTag.l("buttonaction");
                            if (action != null && action.length() > 1) {
                                switch (action.toLowerCase()) {
                                    case "private":
                                        if (isAdmin(whoClicked) || isOwner) {
                                            TelePadsManager.instants.setPrivate(locations, !TelePadsManager.instants.isPrivate(locations));
                                            messageTool.sendMessagePlayer((Player) whoClicked, ChatColor.GREEN + "Status changed to : " + TelePadsManager.instants.isPrivate(locations));
                                            telepadGui.reDrawSettings();
                                        }
                                        break;
                                    case "category":
                                        if (isAdmin(whoClicked) || isOwner) {
                                            List<String> cats = configManager.getCategoryNames((Player) whoClicked);
                                            String category = TelePadsManager.instants.getCategory(locations);
                                            category = getNext(cats, category);
                                            TelePadsManager.instants.setCategory(locations, category);
                                            telepadGui.reDrawSettings();
                                        }
                                        break;
                                    case "icon":
                                        if (isAdmin(whoClicked) || isOwner) {
                                            changeIcons.put(whoClicked.getUniqueId(), locations);
                                            messageTool.sendMessagePlayer((Player) whoClicked, ChatColor.GREEN + "Type Hand in chat for item in your main hand, or paste texture code for head, or cancel to cancel");
                                            whoClicked.closeInventory();
                                        }
                                        break;
                                    case "owner":
                                        if (isAdmin(whoClicked)) {
                                            TelePadsManager.instants.setAdmin(locations, !TelePadsManager.instants.isAdmin(locations));
                                            messageTool.sendMessagePlayer((Player) whoClicked, ChatColor.GREEN + "Status changed to : " + TelePadsManager.instants.isAdmin(locations));
                                            telepadGui.reDrawSettings();
                                        }
                                        break;
                                    case "name":
                                        if (isAdmin(whoClicked) || isOwner) {
                                            changeNames.put(whoClicked.getUniqueId(), locations);
                                            messageTool.sendMessagePlayer((Player) whoClicked, ChatColor.GREEN + "Type Name in chat, or cancel to cancel");
                                            whoClicked.closeInventory();
                                        }
                                        break;
                                    case "back":
                                        TelepadGui gui = TelepadGui.getGui((Player) whoClicked);
                                        gui.showGUI((Player) whoClicked);
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + action.toLowerCase());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void TelepadGuiClicked(InventoryClickEvent event, HumanEntity whoClicked, InventoryView openInventory, Inventory clickedInventory) {
        if (openInventory.getTitle().equals(TelepadGui.guiName))
        {
            event.setCancelled(true);
            TelepadGui telepadGui = TelepadGui.getGui((Player) whoClicked);
            if (telepadGui != null)
            {
                if (event.getSlot() > -1 && event.getSlot() < telepadGui.getSize()) {
                    ItemStack clicked = clickedInventory.getItem(event.getSlot());
                    if (!tools.getItemStackTool().isEmpty(clicked)) {
                        NBTTagCompound nbtTag = tools.getNBTTool().getNBTTag(clicked);
                        if (nbtTag != null) {
                            if (nbtTag.e("padlocation")) {
                                Location location = tools.getSerializeTool().deserializeLocation(nbtTag.l("padlocation"));
                                if ((event.getClick() == ClickType.RIGHT && isAdmin(whoClicked))||
                                        (event.getClick() == ClickType.RIGHT && TelePadsManager.instants.getOwner(location).equals(whoClicked.getUniqueId()))){
                                    TelepadSettingsGui telepadSettingsGui = new TelepadSettingsGui();
                                    telepadSettingsGui.showGUI((Player) whoClicked, location);
                                } else {
                                    int teleportDelay = configManager.getTeleportDelay();
                                    PressureManager pressureManager = new PressureManager(location, System.currentTimeMillis(), (Player) whoClicked);
                                    pressureManager.setTeleporting(true);
                                    tools.getPlayerTool().startTeleport((Player) whoClicked, location.clone().add(0.5f, 0, 0.5f), teleportDelay);

                                    checkTelePadandFix(location, teleportDelay, pressureManager);
                                    whoClicked.closeInventory();

                                }
                            } else {
                                String action = nbtTag.l("buttonaction");
                                if (action != null && action.length() > 1) {
                                    switch (action.toLowerCase()) {
                                        case "left":
                                            telepadGui.subtractScroll();
                                            telepadGui.drawMain();
                                            break;
                                        case "right":
                                            telepadGui.addScroll();
                                            telepadGui.drawMain();
                                            break;
                                        case "switch":
                                            String cat = nbtTag.l("category");
                                            telepadGui.setToggle(cat);
                                            telepadGui.drawMain();
                                            telepadGui.redrawBookButton();
                                            break;
                                    }
                                }


                            }
                        }
                    }
                }
            }
        }
    }

    private void checkTelePadandFix(Location location, int teleportDelay, PressureManager pressureManager) {
        new BukkitRunnable() {
            @Override
            public void run() {
                location.getBlock().setType(configManager.getMaterial());
                tools.getFloatingTextTool().deleteFloatingText(location.clone().add(0.5f, 0, 0.5f));
                if (!configManager.isEnableVanillaOnly()) {
                    reDrawTelePad();
                }
                tools.getFloatingTextTool().changeFloatingText(location.clone().add(0.5f, 2, 0.5f), TelePadsManager.instants.getName(location.clone()));
                pressureManager.setTeleporting(false);
            }

            private void reDrawTelePad() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ItemStack telepads = new ItemStack(configManager.getMaterial());
                        ItemMeta itemMeta = telepads.getItemMeta();
                        itemMeta.setCustomModelData(70001);
                        telepads.setItemMeta(itemMeta);
                        mainListener.spawnTelePadStand(location.getBlock(), telepads, TelePadsManager.instants.getName(location));
                    }
                }.runTaskLater(instants, 1);
            }
        }.runTaskLater(instants, (teleportDelay + 1)* 20L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void  onPlayerMoveEvent(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Location location = event.getTo().clone();
        if (PressureManager.contains(player)) {
            PressureManager pad = PressureManager.get(player);
            if (!pad.isOnPad(player.getLocation()) && !pad.isTeleporting()) PressureManager.remove(player);
        }

        if (TelePadsManager.instants.isTelePad(location) || TelePadsManager.instants.isTelePad(location.clone().add(0, -1, 0))) {
            if (!PressureManager.contains(player)) {
                new PressureManager(location,System.currentTimeMillis(), player);
                TelepadGui telepadGui = new TelepadGui();
                telepadGui.showGUI(player);
            }
        }

    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void  onBlockBreakEvent(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();
        checkBreak(event, location);
        location = block.getLocation().add(0,1,0);
        checkBreak(event, location);

    }

    private void checkBreak(BlockBreakEvent event, Location location) {
        if (TelePadsManager.instants.isTelePad(location))
        {
            event.setDropItems(false);
            String name = TelePadsManager.instants.getName(location);
            Boolean admin = TelePadsManager.instants.isAdmin(location);
            Boolean privacy = TelePadsManager.instants.isPrivate(location);
            ItemStack icon = TelePadsManager.instants.getIcon(location);
            TelePadsManager.instants.removeTelePad(location);
            location.getWorld().dropItemNaturally(location, getTelePadItem(name, admin, privacy, icon));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void  onPlayerInteractEvent(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        Location location = event.getClickedBlock().getLocation();
        if (TelePadsManager.instants.isTelePad(location))
        {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                new PressureManager(location, System.currentTimeMillis(), player);
                TelepadGui telepadGui = new TelepadGui();
                telepadGui.showGUI(player);
                if (TelePadsManager.instants.getOwner(location).equals(player.getUniqueId()) || TitanTelePads.isAdmin(player)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            TelepadSettingsGui gui = new TelepadSettingsGui();
                            gui.showGUI(player, location);
                        }
                    }.runTaskLater(instants, 1);
                }
            }
            if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                if ((TelePadsManager.instants.getOwner(location).equals(player.getUniqueId())) || (isAdmin(player))) {
                    String name = TelePadsManager.instants.getName(location);
                    Boolean admin = TelePadsManager.instants.isAdmin(location);
                    Boolean privacy = TelePadsManager.instants.isPrivate(location);
                    ItemStack icon = TelePadsManager.instants.getIcon(location);
                    TelePadsManager.instants.removeTelePad(location);
                    location.getWorld().dropItemNaturally(location, getTelePadItem(name, admin, privacy, icon));
                    location.getBlock().setType(Material.AIR);
                }
            }
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void  onBlockPlaceEvent(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack itemInHand = event.getItemInHand();
        if (tools.getNBTTool().hasNBTTag(itemInHand, "telepad"))
        {
            String name = tools.getNBTTool().getNBTTag(itemInHand).l("telepad_name");
            boolean admin = tools.getNBTTool().getNBTTag(itemInHand).q("telepad_admin");
            boolean privacy = tools.getNBTTool().getNBTTag(itemInHand).q("telepad_privacy");
            String icon = tools.getNBTTool().getNBTTag(itemInHand).l("telepad_icon");
            TelePadsManager.instants.placeTelePad(block.getLocation(), player, name, privacy, admin);
            if (icon != null && icon.length() > 1 ) {
                ItemStack itemStack = tools.getSerializeTool().deserializeItemStackSimple(icon);
                TelePadsManager.instants.setIcon(block.getLocation(), itemStack.clone());
            }
            if (!configManager.isEnableVanillaOnly()) {
                spawnTelePadStand(block, itemInHand, name);
            }
        }



    }

    public void spawnTelePadStand(Block block, ItemStack itemInHand, String name) {
        Location location = block.getLocation().add(0.5f, 0, 0.5f);
        ArmorStand stand = block.getWorld().spawn(location, ArmorStand.class);
        stand.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
        stand.setCustomNameVisible(false);
        stand.setVisible(false);
        stand.setCollidable(false);
        stand.setMarker(true);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.getEquipment().setHelmet(itemInHand.clone());
    }
}
