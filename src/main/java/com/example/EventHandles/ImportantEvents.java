package com.example.EventHandles;

import com.example.MultiBlockStructure.AbstractMBStructure;
import com.example.Util.MultiBlocks.StructureChecks.StructureCheckMain;
import com.example.Util.MultiBlocks.StructureSave;
import com.example.examplemod.CryIndustry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = CryIndustry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ImportantEvents {
    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            System.out.println("Structure check was : " + StructureCheckMain.checkStructure((World) event.getWorld(), event.getPos(), event.getPlacedBlock().getBlock()));
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        List<AbstractMBStructure> structureList = StructureSave.STRUCTURES.parallelStream().filter(structure -> structure.isBlockInStructure(event.getPos())).collect(Collectors.toList());
        if (structureList.size() > 0) {
            structureList.stream().findFirst().ifPresent(structure -> structure.invalidate((World) event.getWorld()));
        }
    }
}
