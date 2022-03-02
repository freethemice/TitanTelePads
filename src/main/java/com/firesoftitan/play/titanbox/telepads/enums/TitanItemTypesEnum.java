package com.firesoftitan.play.titanbox.telepads.enums;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum TitanItemTypesEnum {
    TELEPAD(Material.STRING, 70001, "TELEPAD", "Teleport Pad", true, ""),
    WIRES(Material.STRING, 70002, "WIRES", "Wires", false, ""),
    WIRING_BOX(Material.NETHERITE_INGOT, 70003, "WIRING_BOX", "Wiring Box",false, ""),
    TELEPORTER_BOX(Material.ENDER_PEARL, 70004, "TELEPORTER_BOX", "Teleporter Box", false,"");

    private boolean placeable;
    private Material material;
    private int dataID;
    private String id;
    private String name;
    private String[]  lore;

    TitanItemTypesEnum(Material material, int dataID, String id, String name, boolean placeable, String... lores) {
        this.material = material;
        this.placeable = placeable;
        this.dataID = dataID;
        this.id = id;
        this.name = name;
        this.lore = lores;
    }
    public static TitanItemTypesEnum getTypeByID(String id)
    {
        for(TitanItemTypesEnum typesEnum: TitanItemTypesEnum.values())
        {
            if (typesEnum.id.equals(id)) return typesEnum;
        }
        return null;
    }
    public Material getMaterial() {
        return material;
    }

    public int getDataID() {
        return dataID;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getLore() {
        return lore;
    }

    public boolean isPlaceable() {
        return placeable;
    }
}
