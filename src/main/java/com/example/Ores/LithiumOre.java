package com.example.Ores;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class LithiumOre extends OreBlock {
    public LithiumOre() {
        super(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
    }
    @Override
    protected int xpOnDrop(Random rand) {
        return MathHelper.nextInt(rand, 3, 7);
    }
}
