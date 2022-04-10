package com.example.MultiBlockStructure;

import com.example.Util.MultiBlocks.MultiBlockData;
import com.example.Util.SimpleJsonDataManager.DataManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractMBStructure implements TileEntityMB {
    private BlockPos corner;
    private BlockPos size;
    private boolean isRemoved = false;
    private MultiBlockData data;

    public AbstractMBStructure() {
        this.corner = null;
        this.size = null;
        this.data = null;
    }

    public AbstractMBStructure(BlockPos corner, BlockPos size, MultiBlockData data) {
        this.corner = corner;
        this.size = size;
        this.data = data;

    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void remove() {
        isRemoved = true;
    }

    public void setCorner(BlockPos corner) {
        this.corner = corner;
    }

    public void setSize(BlockPos size) {
        this.size = size;
    }

    public void setData(MultiBlockData data) {
        this.data = data;
    }

    public BlockPos getCorner() {
        return corner;
    }

    public CompoundNBT serialize() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("size", size.asLong());
        nbt.putLong("corner", corner.asLong());
        nbt.putString("structure_name", data.structureName);
        return nbt;
    }

    public static AbstractMBStructure deserialize(CompoundNBT nbt) {
        AtomicReference<MultiBlockData> data = new AtomicReference<>();
        DataManager.MULTI_BLOCK_DATA.getAllData().values().parallelStream().filter(k -> k.structureName.equals(nbt.getString("structure_name"))).findFirst().ifPresent(data::set);
        try {
            Class<?> mbClass = data.get().structure;
            AbstractMBStructure structure = (AbstractMBStructure) mbClass.newInstance();
            structure.setData(data.get());
            structure.setCorner(BlockPos.of(nbt.getLong("corner")));
            structure.setSize(BlockPos.of(nbt.getLong("size")));
            return structure;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean isBetween(int lowerBound, int upperBound, int toCheck) {
        return lowerBound <= toCheck && upperBound >= toCheck;
    }

    public boolean isBlockInStructure(BlockPos pos) {
        return isBetween(corner.getX(), corner.getX() + size.getX() - 1, pos.getX()) &&
                isBetween(corner.getY(), corner.getY() + size.getY() - 1, pos.getY()) &&
                isBetween(corner.getZ(), corner.getZ() + size.getZ() - 1, pos.getZ());
    }

    public abstract ActionResultType interactWith(AbstractMBStructure structure, BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit);

    @Override
    public String toString() {
        return "AbstractMBStructure{" +
                "corner=" + corner +
                ", size=" + size +
                '}';
    }

    public void invalidate(World world) {
        for (int i = 0; i < size.getX(); i++) {
            for (int k = 0; k < size.getY(); k++) {
                for (int j = 0; j < size.getZ(); j++) {
                    BlockPos newPos = corner.offset(i, k, j);
                    BlockState state = world.getBlockState(newPos);
                    if (state.getBlock() instanceof MultiBlockSuper) {
                        world.setBlock(newPos, state.setValue(MultiBlockSuper.inValidStructure, false), 3);
                    }
                }
            }
        }
        if (this.hasBlockEntity()) {
            if (world.getBlockEntity(corner) != null) {
                world.removeBlockEntity(corner);
                if (world.getBlockState(corner).hasProperty(MultiBlockSuper.isCorner)) {
                    world.setBlock(corner, world.getBlockState(corner).setValue(MultiBlockSuper.isCorner, false), 3);
                }
            }
        }
        isRemoved = true;
    }

    public void setBlocksValid(World world) {
        for (int i = 0; i < size.getX(); i++) {
            for (int k = 0; k < size.getY(); k++) {
                for (int j = 0; j < size.getZ(); j++) {
                    BlockPos newPos = corner.offset(i, k, j);
                    BlockState state = world.getBlockState(newPos);
                    if (state.getBlock() instanceof MultiBlockSuper) {
                        world.setBlock(newPos, state.setValue(MultiBlockSuper.inValidStructure, true), 3);
                    }
                }
            }
        }
    }
}
