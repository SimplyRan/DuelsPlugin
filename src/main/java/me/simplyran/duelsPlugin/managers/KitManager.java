package me.simplyran.duelsPlugin.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class KitManager {
    private final ItemStack[] items;
    private final ItemStack[] armor;
    private final String kitName;

    public KitManager(ItemStack[] items , String kitName , ItemStack[] armor){

        this.kitName = kitName;
        this.items = items;
        this.armor = armor;
    }

    public String getKitName(){
        return kitName;
    }
    public ItemStack[] getArmor(){
        return armor;
    }
    public ItemStack[] getItems(){
        return items;
    }

}
