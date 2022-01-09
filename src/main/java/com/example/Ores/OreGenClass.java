package com.example.Ores;

import com.example.Inits.BlockInit;
import com.example.examplemod.CryIndustry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CryIndustry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OreGenClass {
    private static final int veinSize = 2;
    private static final int maxHeight = 30;
    private static final int minHeight = 23;
    private static final int veinsPerChunk = 10;

    @SubscribeEvent
    public static void addFeaturesToBiomes(BiomeLoadingEvent event) {
        if (!(event.getCategory().equals(Biome.Category.NETHER) && event.getCategory().equals(Biome.Category.THEEND))) {
            //Copper Ore Generation
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                    Feature.ORE.configured(
                                    new OreFeatureConfig(
                                            OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                                            BlockInit.ORE_COPPER.get().defaultBlockState(),
                                            6))
                            .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(13, 0, 55)))
                            .squared()
                            .count(8)
            );
            //Lithium Ore Generation
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                    Feature.ORE.configured(
                                    new OreFeatureConfig(
                                            OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                                            BlockInit.ORE_LITHIUM.get().defaultBlockState(),
                                            //Veinsize
                                            9))
                            //Min/Max Height
                            .range(16)
                            .squared()
                            .count(4)
            );
            //Zink Ore Generation
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                    Feature.ORE.configured(
                                    new OreFeatureConfig(
                                            OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                                            BlockInit.ORE_ZINC.get().defaultBlockState(),
                                            10))
                            .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(25, 0, 50)))
                            .squared()
                            .count(8)
            );
        }
    }
}
