package com.example.Inits;

import com.example.Machines.Upgrades.RangeUpgrade;
import com.example.Machines.Upgrades.SmartModule;
import com.example.Machines.Upgrades.SpeedUpgradeItem;
import com.example.examplemod.CryIndustry;
import com.example.examplemod.ItemGroups;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CryIndustry.MOD_ID);
    //BlockItems
    public static final RegistryObject<Item> PLANTER_ITEM = ITEMS.register("planter", () -> new BlockItem(BlockInit.PLANTER.get(), new Item.Properties().tab(ItemGroups.MASCHINES)));
    public static final RegistryObject<Item> WOODCUTTER_ITEM = ITEMS.register("woodcutter", () -> new BlockItem(BlockInit.WOODCUTTER.get(), new Item.Properties().tab(ItemGroups.MASCHINES)));
    public static final RegistryObject<Item> BONEMEALER_ITEM = ITEMS.register("bonemealer", () -> new BlockItem(BlockInit.BONEMEALER_BLOCK.get(), new Item.Properties().tab(ItemGroups.MASCHINES)));

    //Ores
    public static final RegistryObject<BlockItem> LITHIUM_ORE_ITEM = ITEMS.register("lithium_ore", () -> new BlockItem(BlockInit.ORE_LITHIUM.get(), new Item.Properties().tab(ItemGroups.MASCHINES)));
    public static final RegistryObject<BlockItem> ZINK_ORE_ITEM = ITEMS.register("zink_ore", () -> new BlockItem(BlockInit.ORE_ZINC.get(), new Item.Properties().tab(ItemGroups.MASCHINES)));
    //ReactorItems
    public static final RegistryObject<Item> REACTOR_GLASS = ITEMS.register("reactor_glass", () -> new BlockItem(BlockInit.REACTOR_GLASS.get(), new Item.Properties().tab(ItemGroups.MASCHINES)));
    public static final RegistryObject<Item> REACTOR_FRAME = ITEMS.register("reactor_frame", () -> new BlockItem(BlockInit.REACTOR_FRAME.get(), new Item.Properties().tab(ItemGroups.MASCHINES)));

    //UpgradeItems
    public static final RegistryObject<Item> RANGE_1_UPGRADE = ITEMS.register("r1upgrade", () -> new RangeUpgrade(2));
    public static final RegistryObject<Item> RANGE_2_UPGRADE = ITEMS.register("r2upgrade", () -> new RangeUpgrade(4));
    public static final RegistryObject<Item> RANGE_3_UPGRADE = ITEMS.register("r3upgrade", () -> new RangeUpgrade(8));
    public static final RegistryObject<Item> RANGE_4_UPGRADE = ITEMS.register("r4upgrade", () -> new RangeUpgrade(16));
    public static final RegistryObject<Item> RANGE_5_UPGRADE = ITEMS.register("r5upgrade", () -> new RangeUpgrade(32));
    public static final RegistryObject<Item> SMART_UPGRADE = ITEMS.register("smart_upgrade", SmartModule::new);
    public static final RegistryObject<Item> SPEED_1_UPGRADE = ITEMS.register("speed1upgrade", () -> new SpeedUpgradeItem(10));
    public static final RegistryObject<Item> SPEED_2_UPGRADE = ITEMS.register("speed2upgrade", () -> new SpeedUpgradeItem(20));
    public static final RegistryObject<Item> SPEED_3_UPGRADE = ITEMS.register("speed3upgrade", () -> new SpeedUpgradeItem(30));
    public static final RegistryObject<Item> SPEED_4_UPGRADE = ITEMS.register("speed4upgrade", () -> new SpeedUpgradeItem(50));
    public static final RegistryObject<Item> SPEED_5_UPGRADE = ITEMS.register("speed5upgrade", () -> new SpeedUpgradeItem(100));
}
