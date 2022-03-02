package com.firesoftitan.play.titanbox.telepads.managers;

import com.firesoftitan.play.titanbox.telepads.TitanTelePads;
import com.firesoftitan.play.titanbox.telepads.enums.TitanItemTypesEnum;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import static com.firesoftitan.play.titanbox.telepads.TitanTelePads.tools;

public class RecipeManager {
    public RecipeManager() {
        addTelePads();
        addWires();
        addWiringBox();
        addTeleporterBox();
    }

    private void addWires() {
        TitanItemTypesEnum teleporterBox = TitanItemTypesEnum.WIRES;
        ItemStack[] matrix = new ItemStack[9];
        matrix[0] = new ItemStack(Material.PAPER);
        matrix[1] = new ItemStack(Material.PAPER);
        matrix[2] = new ItemStack(Material.PAPER);
        matrix[3] = new ItemStack(Material.COPPER_INGOT);
        matrix[4] = new ItemStack(Material.COPPER_INGOT);
        matrix[5] = new ItemStack(Material.COPPER_INGOT);
        matrix[6] = new ItemStack(Material.PAPER);
        matrix[7] = new ItemStack(Material.PAPER);
        matrix[8] = new ItemStack(Material.PAPER);
        ItemStack partItem = TitanTelePads.getPartItem(teleporterBox);
        partItem.setAmount(3);
        tools.getRecipeTool().addAdvancedRecipe(partItem, matrix);

    }
    private void addTeleporterBox() {
        TitanItemTypesEnum teleporterBox = TitanItemTypesEnum.TELEPORTER_BOX;
        ItemStack[] matrix = new ItemStack[9];
        matrix[0] = TitanTelePads.getPartItem(TitanItemTypesEnum.WIRING_BOX);
        matrix[1] = new ItemStack(Material.IRON_INGOT);
        matrix[2] = TitanTelePads.getPartItem(TitanItemTypesEnum.WIRING_BOX);
        matrix[3] = new ItemStack(Material.ENDER_PEARL);
        matrix[4] = new ItemStack(Material.ENDER_PEARL);
        matrix[5] = new ItemStack(Material.ENDER_PEARL);
        matrix[6] = TitanTelePads.getPartItem(TitanItemTypesEnum.WIRING_BOX);
        matrix[7] = new ItemStack(Material.IRON_INGOT);
        matrix[8] = TitanTelePads.getPartItem(TitanItemTypesEnum.WIRING_BOX);

        ItemStack partItem = TitanTelePads.getPartItem(teleporterBox);
        tools.getRecipeTool().addAdvancedRecipe(partItem, matrix);
    }
    private void addWiringBox() {
        TitanItemTypesEnum wiringBox = TitanItemTypesEnum.WIRING_BOX;
        ItemStack[] matrix = new ItemStack[9];
        matrix[0] = new ItemStack(Material.IRON_INGOT);
        matrix[1] = new ItemStack(Material.IRON_INGOT);
        matrix[2] = new ItemStack(Material.IRON_INGOT);
        matrix[3] = TitanTelePads.getPartItem(TitanItemTypesEnum.WIRES);
        matrix[4] = TitanTelePads.getPartItem(TitanItemTypesEnum.WIRES);
        matrix[5] = TitanTelePads.getPartItem(TitanItemTypesEnum.WIRES);
        matrix[6] = new ItemStack(Material.IRON_INGOT);
        matrix[7] = new ItemStack(Material.IRON_INGOT);
        matrix[8] = new ItemStack(Material.IRON_INGOT);

        ItemStack partItem = TitanTelePads.getPartItem(wiringBox);
        tools.getRecipeTool().addAdvancedRecipe(partItem, matrix);
    }
    private void addTelePads() {
        TitanItemTypesEnum telepads = TitanItemTypesEnum.TELEPAD;

        ItemStack[] matrix = new ItemStack[9];
        matrix[0] = TitanTelePads.getPartItem(TitanItemTypesEnum.TELEPORTER_BOX);
        matrix[1] = new ItemStack(Material.IRON_INGOT);
        matrix[2] = TitanTelePads.getPartItem(TitanItemTypesEnum.TELEPORTER_BOX);

        matrix[3] = new ItemStack(Material.IRON_INGOT);
        matrix[4] = TitanTelePads.getPartItem(TitanItemTypesEnum.WIRING_BOX);
        matrix[5] = new ItemStack(Material.IRON_INGOT);

        matrix[6] = TitanTelePads.getPartItem(TitanItemTypesEnum.TELEPORTER_BOX);
        matrix[7] = new ItemStack(Material.IRON_INGOT);
        matrix[8] = TitanTelePads.getPartItem(TitanItemTypesEnum.TELEPORTER_BOX);

        ItemStack partItem = TitanTelePads.getPartItem(telepads);
        tools.getRecipeTool().addAdvancedRecipe(partItem, matrix);
    }
}
