package com.example.MultiBlockStructure;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface TileEntityMB {
    boolean hasBlockEntity();

    TileEntity createBlockEntity(AbstractMBStructure structure);

    default void setTileIntoWorld(World world, BlockPos pos, AbstractMBStructure structure) {
        TileEntity blockEntity = createBlockEntity(structure);
        blockEntity.setLevelAndPosition(world, pos);
        System.out.println(world.addBlockEntity(blockEntity));


    }


}
