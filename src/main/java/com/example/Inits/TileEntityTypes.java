package com.example.Inits;

import com.example.Machines.BoneMealer.BoneMealerTE;
import com.example.Machines.Planter.PlanterTE;
import com.example.Machines.WoodCutter.WoodCutterTE;
import com.example.examplemod.CryIndustry;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class TileEntityTypes {
    private static final Set<Block> validMBF = new HashSet<>();

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CryIndustry.MOD_ID);
    public static final RegistryObject<TileEntityType<PlanterTE>> PLANTER_TE = TILE_ENTITY_TYPES.register("planter",()->TileEntityType.Builder.of(PlanterTE::new,BlockInit.PLANTER.get()).build(null));
    public static final RegistryObject<TileEntityType<WoodCutterTE>> WOODCUTTER_TE = TILE_ENTITY_TYPES.register("woodcutter",()->TileEntityType.Builder.of(WoodCutterTE::new,BlockInit.WOODCUTTER.get()).build(null));
    public static final RegistryObject<TileEntityType<BoneMealerTE>> BONEMEALER_TE = TILE_ENTITY_TYPES.register("bonemealer",()->TileEntityType.Builder.of(BoneMealerTE::new,BlockInit.BONEMEALER_BLOCK.get()).build(null));
   // public static final RegistryObject<TileEntityType<ReactorTE>> ABSTRACT_MBF_TE = TILE_ENTITY_TYPES.register("mbf",()->TileEntityType.Builder.of(ReactorTE::new,BlockInit.REACTOR_FRAME.get()).build(null));



}
