package com.example.Inits;

import com.example.Cables.MachineEnergyInterface;
import com.example.Machines.BoneMealer.BoneMealerBlock;
import com.example.Machines.Planter.PlanterBlock;
import com.example.Machines.WoodCutter.WoodCutterBlock;
import com.example.Ores.CopperOre;
import com.example.Ores.LithiumOre;
import com.example.Ores.ZinkOre;
import com.example.ReactorCraft.Blocks.ReactorController;
import com.example.ReactorCraft.Blocks.ReactorFrame;
import com.example.ReactorCraft.Blocks.ReactorGlass;
import com.example.examplemod.CryIndustry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CryIndustry.MOD_ID);
    public static final RegistryObject<PlanterBlock> PLANTER = BLOCKS.register("planter", PlanterBlock::new);
    public static final RegistryObject<WoodCutterBlock> WOODCUTTER = BLOCKS.register("woodcutter", WoodCutterBlock::new);
    public static final RegistryObject<Block> BONEMEALER_BLOCK = BLOCKS.register("bonemealer", BoneMealerBlock::new);

    //Reactor
    public static final RegistryObject<Block> REACTOR_GLASS = BLOCKS.register("reactor_glass", ReactorGlass::new);
    public static final RegistryObject<Block> REACTOR_FRAME = BLOCKS.register("reactor_frame", ReactorFrame::new);
    public static final RegistryObject<Block> REACTOR_CONTROLLER = BLOCKS.register("reactor_controller", ReactorController::new);
    public static final RegistryObject<MachineEnergyInterface> MACHINE_ENERGY_INTERFACE = BLOCKS.register("machine_energy_interface", MachineEnergyInterface::new);
    //ORES
    public static final RegistryObject<OreBlock> ORE_LITHIUM = BLOCKS.register("lithium_ore", LithiumOre::new);
    public static final RegistryObject<OreBlock> ORE_ZINC = BLOCKS.register("zinc_ore", ZinkOre::new);
    public static final RegistryObject<OreBlock> ORE_COPPER = BLOCKS.register("copper_ore", CopperOre::new);

}
