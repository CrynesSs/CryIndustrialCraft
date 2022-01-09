package com.example.MultiBlockStructure;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface TileEntityMB {
    boolean hasBlockEntity();
    TileEntity createBlockEntity();
    default void setTileIntoWorld(World world, BlockPos pos){
        TileEntity blockEntity = createBlockEntity();
        blockEntity.setLevelAndPosition(world,pos);
    }


}
