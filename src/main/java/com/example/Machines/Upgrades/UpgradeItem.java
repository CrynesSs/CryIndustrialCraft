package com.example.Machines.Upgrades;

import com.example.examplemod.ItemGroups;
import net.minecraft.item.Item;

public abstract class UpgradeItem extends Item {
    private final int strenght;
    public UpgradeItem(int strength) {
        super(new Item.Properties().tab(ItemGroups.UPGRADES).stacksTo(1).durability(512));
        this.strenght = strength;
    }

    public int getStrenght() {
        return strenght;
    }
}
