package com.example.EventHandles;

import com.example.Inits.VillagerInit;
import com.example.examplemod.CryIndustry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CryIndustry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetupEvent {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(VillagerInit::registerPointOfInterests);
        VillagerInit.populateTrades();
    }
    @SubscribeEvent
    public static void setAttributes(EntityAttributeCreationEvent event){
    }
}
