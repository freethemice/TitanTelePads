package com.firesoftitan.play.titanbox.telepads.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PressureManager {
    private static HashMap<UUID, PressureManager> activeMoves = new HashMap<UUID, PressureManager>();
    public static PressureManager get(Player player)
    {
        return activeMoves.get(player.getUniqueId());
    }
    public static void remove(Player player)
    {
        activeMoves.remove(player.getUniqueId());
    }
    public static boolean contains(Player player)
    {
        return activeMoves.containsKey(player.getUniqueId());
    }


    private Location location;
    private Long time;
    private Player owner;
    private boolean teleporting = false;

    public PressureManager(Location location, Long time, Player owner) {
        this.location = location;
        this.time = time;
        this.owner = owner;
        activeMoves.put(owner.getUniqueId(), this);
    }

    public boolean isTeleporting() {
        return teleporting;
    }

    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }

    public boolean isOnPad(Location player)
    {
        for(int y = -2; y < 5; y++)
        {
            if (TelePadsManager.instants.isTelePad(player.clone().add(0, y, 0))) return true;
        }
        return false;
    }
    public String getWorldName()
    {
        return location.getWorld().getName();
    }
    public Location getLocation() {
        return location;
    }

    public Long getTime() {
        return time;
    }

    public Player getOwner() {
        return owner;
    }
    public UUID getOwnerUUID() {
        return owner.getUniqueId();
    }
}
