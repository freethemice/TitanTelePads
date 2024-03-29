package com.firesoftitan.play.titanbox.telepads.managers;

import com.firesoftitan.play.titanbox.libs.managers.HologramManager;
import com.firesoftitan.play.titanbox.libs.managers.SaveManager;
import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import com.firesoftitan.play.titanbox.telepads.enums.TitanItemTypesEnum;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.firesoftitan.play.titanbox.telepads.TitanTelePads.configManager;
import static com.firesoftitan.play.titanbox.telepads.TitanTelePads.tools;


public class TelePadsManager {
    private final SaveManager configFile;
    public static TelePadsManager instants;
    private final List<Location> output;
    public TelePadsManager() {
        configFile  = new SaveManager(TitanTelePads.instants.getName(), "telepads");
        instants = this;
        output = new ArrayList<Location>();
        for(String key: configFile.getKeys("telepads"))
        {
            Location location = TitanTelePads.tools.getSerializeTool().deserializeLocation(key);
            output.add(location.clone());
            new BukkitRunnable() {
                @Override
                public void run() {
                    TelePadsManager.instants.FixTelepad(location);
                }
            }.runTaskLater(TitanTelePads.instants, 20);
        }

    }
    public void save()
    {
        configFile.save();
    }
    public boolean isTelePad(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        return configFile.contains("telepads." + key);
    }
    public String getOwnerName(Location location)
    {
        if (isAdmin(location)) return "Admin";
        UUID owner = getOwner(location);
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        String name = player.getName();
        if (name == null || name.isEmpty()) return "Admin";
        return name;
    }
    public HologramManager getHologramBlock(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".hologram.block")) return setUpHologramBlock(location);
        HologramManager hologramManager = TitanTelePads.tools.getHologramTool().getHologram(configFile.getUUID("telepads." + key + ".hologram.block"));
        if (hologramManager == null)
        {
            List<HologramManager> holograms = tools.getHologramTool().getHolograms(location);
            if (!holograms.isEmpty()) hologramManager = holograms.get(0);
            if (hologramManager == null || hologramManager.getText() != null) hologramManager = setUpHologramBlock(location);
        }
        return hologramManager;
    }
    private HologramManager setUpHologramBlock(Location location)
    {
        ItemStack telepads = new ItemStack(Material.WHITE_CARPET);
        ItemMeta itemMeta = telepads.getItemMeta();
        itemMeta.setCustomModelData(TitanItemTypesEnum.TELEPAD.getDataID());
        telepads.setItemMeta(itemMeta);

        HologramManager hologramManager = TitanTelePads.tools.getHologramTool().addHologram(location.clone().add(0.5f ,0,0.5f));
        hologramManager.setEquipment(EquipmentSlot.HEAD, telepads.clone());
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".hologram.block", hologramManager.getUUID());
        return hologramManager;
    }
    public void FixTelepad(Location location) {
        location.getBlock().setType(configManager.getMaterial());
        this.getHologramName(location);
        this.getHologramBlock(location);
    }
    public HologramManager getHologramName(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".hologram.name")) return setUpHologramName(location);
        HologramManager hologramManager = TitanTelePads.tools.getHologramTool().getHologram(configFile.getUUID("telepads." + key + ".hologram.name"));
        if (hologramManager == null)
        {
            List<HologramManager> holograms = tools.getHologramTool().getHolograms(location.clone().add(0 ,2,0));
            if (!holograms.isEmpty()) hologramManager = holograms.get(0);
            if (hologramManager == null || hologramManager.getText() == null) hologramManager = setUpHologramName(location);
        }
        return hologramManager;
    }
    private HologramManager setUpHologramName(Location location)
    {
        HologramManager hologramManager = TitanTelePads.tools.getHologramTool().addHologram(location.clone().add(0.5f ,2,0.5f));
        hologramManager.setText(this.getName(location));
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".hologram.name", hologramManager.getUUID());
        return hologramManager;
    }
    public boolean isUpdated(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".updated")) return false;
        return true;
    }
    public void setUpdated(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".updated", TitanTelePads.instants.getDescription().getVersion());
        TitanTelePads.tools.getHologramTool().deleteOldFloatingText(location.clone().add(0.5f, 2, 0.5f));
        TitanTelePads.tools.getHologramTool().deleteOldFloatingText(location.clone().add(0.5f, 0, 0.5f));
    }
    public UUID getOwner(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".owner")) return null;
        return configFile.getUUID("telepads." + key + ".owner");
    }
    public void setName(Location location, String name)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".name", name);
        new BukkitRunnable() {
            @Override
            public void run() {
                HologramManager hologramManager = TelePadsManager.instants.getHologramName(location);
                hologramManager.setText(name);
            }
        }.runTaskLater(TitanTelePads.instants, 1);

    }
    public ItemStack getIcon(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".icon"))
        {
            try {
                if (!isAdmin(location)) {
                    String playersTexture = null;
                    if (TitanTelePads.tools.getPlayerTool().doesPlayersHaveTexture(getOwner(location)))
                        playersTexture = TitanTelePads.tools.getPlayerTool().getPlayersTexture(getOwner(location));
                    else
                        playersTexture = TitanTelePads.tools.getPlayerTool().getPlayersTexture(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"));

                    ItemStack skull = new ItemStack(Material.LILAC);
                    if (playersTexture != null) skull = TitanTelePads.tools.getSkullTool().getSkull(playersTexture);
                    configFile.set("telepads." + key + ".icon", skull);

                }
                else
                {
                    configFile.set("telepads." + key + ".icon", new ItemStack(Material.BOOKSHELF));
                }
            } catch (Exception ignore) {

            }
        }
        ItemStack item = configFile.getItem("telepads." + key + ".icon");
        if (item.getType() == Material.WHITE_CARPET)
        {
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(70001);
            item.setItemMeta(itemMeta);
        }
        return item.clone();
    }
    public void setIcon(Location location, ItemStack icon) {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        icon = TitanTelePads.tools.getItemStackTool().changeName(icon, "icon");
        icon = TitanTelePads.tools.getItemStackTool().clearLore(icon);
        configFile.set("telepads." + key + ".icon", icon.clone());
    }
    public String getCategory(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".category"))
        {
            configFile.set("telepads." + key + ".category", TitanTelePads.configManager.getCategoryDefault());
        }
        return configFile.getString("telepads." + key + ".category");
    }
    public void setCategory(Location location, String cat)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".category", cat);
    }
    public String getName(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".name")) return null;
        return configFile.getString("telepads." + key + ".name");
    }
    public void setPrivate(Location location, Boolean priv)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".private", priv);
    }
    public Boolean isPrivate(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".private")) return false;
        return configFile.getBoolean("telepads." + key + ".private");
    }
    public void setAdmin(Location location, Boolean admin)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".admin", admin);
    }
    public Boolean isAdmin(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        if (!configFile.contains("telepads." + key + ".admin")) return false;
        return configFile.getBoolean("telepads." + key + ".admin");
    }
    public void removeTelePad(Location location)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        getHologramName(location).delete();
        getHologramBlock(location).delete();
        configFile.delete("telepads." + key);
        for(int i = 0; i < output.size(); i++)
        {
            Location locationA = output.get(i);
            if (TitanTelePads.tools.getLocationTool().isLocationsEqual(location, locationA))
            {
                output.remove(i);
                return;
            }
        }

    }
    public void placeTelePad(Location location, Player owner, String name)
    {
        placeTelePad(location, owner.getUniqueId(), name);
    }
    public void placeTelePad(Location location, UUID owner, String name)
    {
        placeTelePad(location, owner, name, false, false);
    }
    public void placeTelePad(Location location, Player owner, String name, Boolean privacy, Boolean admin)
    {
        placeTelePad(location, owner.getUniqueId(), name, privacy, admin);
    }
    public void placeTelePad(Location location, UUID owner, String name, Boolean privacy, Boolean admin)
    {
        String key = TitanTelePads.tools.getSerializeTool().serializeLocation(location);
        configFile.set("telepads." + key + ".owner", owner);
        configFile.set("telepads." + key + ".name", name);
        configFile.set("telepads." + key + ".private", privacy);
        configFile.set("telepads." + key + ".admin", admin);
        configFile.set("telepads." + key + ".updated", TitanTelePads.instants.getDescription().getVersion());
        configFile.set("telepads." + key + ".category", TitanTelePads.configManager.getCategoryDefault());
        setUpHologramName(location);
        setUpHologramBlock(location);
        try {
            if (!admin) {
                String playerTexture;
                if (TitanTelePads.tools.getPlayerTool().doesPlayersHaveTexture(getOwner(location)))
                    playerTexture = TitanTelePads.tools.getPlayerTool().getPlayersTexture(getOwner(location));
                else
                    playerTexture = TitanTelePads.tools.getPlayerTool().getPlayersTexture(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"));

                ItemStack skull = new ItemStack(Material.LILAC);
                if (playerTexture != null) skull = TitanTelePads.tools.getSkullTool().getSkull(playerTexture);
                configFile.set("telepads." + key + ".icon", skull);
            }
            else
            {
                configFile.set("telepads." + key + ".icon", new ItemStack(Material.BOOKSHELF));
            }
        } catch (Exception ignored) {

        }
        output.add(location.clone());
    }
    public List<Location> getAll(String category)
    {
        List<Location> owners = new ArrayList<Location>();
        for(Location location: output)
        {
            if (getOwner(location) != null) {
                if (getCategory(location).equals(category)) {
                    owners.add(location.clone());
                }
            }
        }
        return owners;
    }
    public List<Location> getAllAdmin()
    {
        List<Location> owners = new ArrayList<Location>();
        for(Location location: output)
        {
            if (isAdmin(location)) {
                owners.add(location.clone());
            }
        }
        return owners;
    }
    public List<Location> getAll(UUID owner)
    {
        List<Location> owners = new ArrayList<Location>();
        for(Location location: output)
        {
            if (getOwner(location) != null) {
                if (getOwner(location).equals(owner) && !isAdmin(location)) {
                    owners.add(location.clone());
                }
            }
        }
        return owners;
    }
    public List<Location> getAll()
    {
        return new ArrayList<Location>(output);
    }
}
