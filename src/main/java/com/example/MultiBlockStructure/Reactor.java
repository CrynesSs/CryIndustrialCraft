package com.example.MultiBlockStructure;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class Reactor extends AbstractMBStructure {

    public Reactor(){
        super();
    }
    @Override
    public void interactWith(AbstractMBStructure structure, BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

    }

    @Override
    public boolean hasBlockEntity() {
        return false;
    }

    @Override
    public TileEntity createBlockEntity() {
        return null;
    }
}
