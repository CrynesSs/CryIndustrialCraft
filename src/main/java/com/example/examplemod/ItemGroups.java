package com.example.examplemod;

import com.example.Inits.ItemInit;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroups {
    public static final ItemGroup MASCHINES = new ItemGroup("RedstoneEnhancements") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemInit.PLANTER_ITEM.get());
        }
    };
    public static final ItemGroup UPGRADES = new ItemGroup("RedstoneEnhancements") {
        @Override
        public ItemStack makeIcon() {
            //*This Sets the Item that will be in the Thumbnail of the Item Tab
            return new ItemStack(ItemInit.SMART_UPGRADE.get());
        }
    };
}
