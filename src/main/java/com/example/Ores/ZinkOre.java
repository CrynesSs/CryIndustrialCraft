package com.example.Ores;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;

public class ZinkOre extends OreBlock {
    public ZinkOre() {
        super(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
    }
}
