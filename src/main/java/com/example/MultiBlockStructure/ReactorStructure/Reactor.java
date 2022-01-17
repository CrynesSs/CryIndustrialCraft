package com.example.MultiBlockStructure.ReactorStructure;

import com.example.Inits.BlockInit;
import com.example.Inits.TileEntityTypes;
import com.example.MultiBlockStructure.AbstractMBStructure;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class Reactor extends AbstractMBStructure {

    public Reactor() {
        super();
    }

    @Override
    public ActionResultType interactWith(AbstractMBStructure structure, BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (state.is(BlockInit.REACTOR_GLASS.get())) {
            return ActionResultType.PASS;
        }
        System.out.println("Interacted with Reactor");
        ReactorTE te = getTileEntity(worldIn);
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasBlockEntity() {
        return true;
    }

    @Override
    public TileEntity createBlockEntity(AbstractMBStructure structure) {
        return new ReactorTE((Reactor) structure);
    }

    public ReactorTE getTileEntity(World world) {
        if (world.getBlockEntity(this.getCorner()) instanceof ReactorTE) {
            return (ReactorTE) world.getBlockEntity(this.getCorner());
        }
        return null;
    }
}
