package com.example.MultiBlockStructure.BigOvenStructure;

import com.example.MultiBlockStructure.AbstractMBStructure;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class BigOven extends AbstractMBStructure {

    @Override
    public ActionResultType interactWith(AbstractMBStructure structure, BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return null;
    }

    @Override
    public boolean hasBlockEntity() {
        return true;
    }

}
