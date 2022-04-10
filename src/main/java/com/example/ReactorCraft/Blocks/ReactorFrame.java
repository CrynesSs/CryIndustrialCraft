package com.example.ReactorCraft.Blocks;

import com.example.Inits.TileEntityTypes;
import com.example.MultiBlockStructure.MultiBlockSuper;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class ReactorFrame extends MultiBlockSuper {

    public ReactorFrame() {
        super();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(isCorner);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileEntityTypes.REACTOR.get().create();
    }
}
