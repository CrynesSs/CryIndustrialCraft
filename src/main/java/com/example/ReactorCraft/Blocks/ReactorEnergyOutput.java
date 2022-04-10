package com.example.ReactorCraft.Blocks;

import com.example.MultiBlockStructure.MultiBlockSuper;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class ReactorEnergyOutput extends MultiBlockSuper {
    public ReactorEnergyOutput() {
        super();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(inValidStructure);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return super.createTileEntity(state, world);
    }
}
