package com.firesoftitan.play.titanbox.telepads.listeners;

import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import com.firesoftitan.play.titanbox.telepads.guis.TelepadGui;
import com.firesoftitan.play.titanbox.telepads.managers.PressureManager;
import com.firesoftitan.play.titanbox.telepads.managers.TelePadsManager;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class MainListener  implements Listener {

    private static HashMap<UUID, Location> changeNames = new HashMap<UUID, Location>();
    public MainListener(){

    }
    public void registerEvents(){
        PluginManager pm = TitanTelePads.instants.getServer().getPluginManager();
        pm.registerEvents(this, TitanTelePads.instants);
    }

    @EventHandler
    public static void onPlayerLoginEvent(PlayerLoginEvent event)
    {
        Player player = event.getPlayer();


        if (TitanTelePads.isAdmin(player)) {
            if (TitanTelePads.update) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        TitanTelePads.messageTool.sendMessagePlayer(player,"There is a new update available.");
                        TitanTelePads.messageTool.sendMessagePlayer(player, "https://www.spigotmc.org/resources/titan-teleport-pads.99835/");

                    }
                }.runTaskLater(TitanTelePads.instants, 20);
            }
        }


    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public static void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        if (changeNames.containsKey(player.getUniqueId()))
        {
            event.setCancelled(true);
            Location location = changeNames.get(player.getUniqueId());
            changeNames.remove(player.getUniqueId());
            if (!event.getMessage().equalsIgnoreCase("cancel"))
            {
                UUID owner = TelePadsManager.instants.getOwner(location);
                if (owner.equals(player.getUniqueId())) {
                    TelePadsManager.instants.setName(location, ChatColor.translateAlternateColorCodes('&', event.getMessage()));
                    TitanTelePads.messageTool.sendMessagePlayer(player, "Name changed!");
                    return;
                }
            }
            TitanTelePads.messageTool.sendMessagePlayer(player, "Name changed CANCELED!");
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {

    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public static void onInventoryClickEvent(InventoryClickEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        InventoryView openInventory = whoClicked.getOpenInventory();
        Inventory clickedInventory = event.getClickedInventory();
        if (openInventory.getTitle().equals(TelepadGui.guiName))
        {
            event.setCancelled(true);
            TelepadGui telepadGui = TelepadGui.getGui((Player) whoClicked);
            if (telepadGui != null)
            {
                if (event.getSlot() > -1 && event.getSlot() < telepadGui.getSize()) {
                    ItemStack clicked = clickedInventory.getItem(event.getSlot());
                    if (!TitanTelePads.tools.getItemStackTool().isEmpty(clicked)) {
                        NBTTagCompound nbtTag = TitanTelePads.tools.getNBTTool().getNBTTag(clicked);
                        if (nbtTag != null) {
                            if (nbtTag.e("padlocation")) {
                                Location location = TitanTelePads.tools.getSerializeTool().deserializeLocation(nbtTag.l("padlocation"));
                                if ((event.getClick() == ClickType.SHIFT_RIGHT && TitanTelePads.isAdmin(whoClicked)))
                                {
                                    TelePadsManager.instants.setAdmin(location, !TelePadsManager.instants.isAdmin(location));
                                    TitanTelePads.messageTool.sendMessagePlayer((Player) whoClicked, ChatColor.GREEN + "Status changed to : " + TelePadsManager.instants.isAdmin(location));
                                    telepadGui.drawMain();
                                }else if (event.getClick() == ClickType.SHIFT_LEFT && TelePadsManager.instants.getOwner(location).equals(whoClicked.getUniqueId()))
                                {
                                    TelePadsManager.instants.setPrivate(location, !TelePadsManager.instants.isPrivate(location));
                                    TitanTelePads.messageTool.sendMessagePlayer((Player) whoClicked, ChatColor.GREEN + "Status changed to : " + TelePadsManager.instants.isAdmin(location));
                                    telepadGui.drawMain();
                                }
                                else if ((event.getClick() == ClickType.RIGHT && TitanTelePads.isAdmin(whoClicked))||
                                        (event.getClick() == ClickType.RIGHT && TelePadsManager.instants.getOwner(location).equals(whoClicked.getUniqueId())))
                                {
                                    changeNames.put(whoClicked.getUniqueId(), location);
                                    TitanTelePads.messageTool.sendMessagePlayer((Player) whoClicked, ChatColor.GREEN + "Type Name in chat, or cancel to cancel");
                                    whoClicked.closeInventory();
                                } else {

                                    int teleportDelay = TitanTelePads.configManager.getTeleportDelay();
                                    PressureManager pressureManager = new PressureManager(location, System.currentTimeMillis(), (Player) whoClicked);
                                    pressureManager.setTeleporting(true);
                                    TitanTelePads.tools.getPlayerTool().startTeleport((Player) whoClicked, location.clone().add(0.5f, 0, 0.5f), teleportDelay);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            location.getBlock().setType(TitanTelePads.configManager.getMaterial());
                                            TitanTelePads.tools.getFloatingTextTool().changeFloatingText(location.clone().add(0.5f, 2, 0.5f), TelePadsManager.instants.getName(location.clone()));
                                            pressureManager.setTeleporting(false);
                                        }
                                    }.runTaskLater(TitanTelePads.instants, (teleportDelay + 1)* 20L);
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
                                            telepadGui.setToggle();
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
        if (TelePadsManager.instants.isTelePad(location))
        {
            event.setDropItems(false);
            String name = TelePadsManager.instants.getName(location);
            Boolean admin = TelePadsManager.instants.isAdmin(location);
            Boolean privacy = TelePadsManager.instants.isPrivate(location);
            TelePadsManager.instants.removeTelePad(location);
            location.getWorld().dropItemNaturally(location, TitanTelePads.getTelePadItem(name, admin, privacy));
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
            }
            if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                if ((TelePadsManager.instants.getOwner(location).equals(player.getUniqueId())) || (TitanTelePads.isAdmin(player))) {
                    String name = TelePadsManager.instants.getName(location);
                    Boolean admin = TelePadsManager.instants.isAdmin(location);
                    Boolean privacy = TelePadsManager.instants.isPrivate(location);
                    TelePadsManager.instants.removeTelePad(location);
                    location.getWorld().dropItemNaturally(location, TitanTelePads.getTelePadItem(name, admin, privacy));
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
        if (TitanTelePads.tools.getNBTTool().hasNBTTag(itemInHand, "telepad"))
        {
            String name = TitanTelePads.tools.getNBTTool().getNBTTag(itemInHand).l("telepad_name");
            boolean admin = TitanTelePads.tools.getNBTTool().getNBTTag(itemInHand).q("telepad_admin");
            boolean privacy = TitanTelePads.tools.getNBTTool().getNBTTag(itemInHand).q("telepad_privacy");
            TelePadsManager.instants.placeTelePad(block.getLocation(), player, name, privacy, admin);
        }



    }
}
