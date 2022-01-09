package com.example.MultiBlockStructure;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public abstract class AbstractMBStructure implements TileEntityMB {
    private BlockPos corner;
    private BlockPos size;
    private boolean isRemoved = false;

    public AbstractMBStructure() {
        this.corner = null;
        this.size = null;
    }

    public AbstractMBStructure(BlockPos corner, BlockPos size) {
        this.corner = corner;
        this.size = size;

    }

    public CompoundNBT save(CompoundNBT compound) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("corner", corner.asLong());
        nbt.putLong("size", size.asLong());
        nbt.putBoolean("removed", isRemoved);
        compound.put("basedata", nbt);
        return compound;
    }

    public void load(CompoundNBT compoundNBT) {
        if (compoundNBT == null || !compoundNBT.contains("basedata")) {
            throw new IllegalStateException("baseData was not Saved correctly!!!");
        }
        CompoundNBT nbt = (CompoundNBT) compoundNBT.get("basedata");
        if (nbt == null) return;
        this.corner = BlockPos.of(nbt.getLong("corner"));
        this.size = BlockPos.of(nbt.getLong("size"));
        this.isRemoved = nbt.getBoolean("removed");

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

    /*
    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        CompoundNBT size = nbt.getCompound("size");
        CompoundNBT corner = nbt.getCompound("corner");
        this.size = new BlockPos(size.getInt("x"), size.getInt("y"), size.getInt("z"));
        this.corner = new BlockPos(corner.getInt("x"), corner.getInt("y"), corner.getInt("z"));
        super.load(state, nbt);
        if (!StructureSave.STRUCTURES.contains(this)) {
            StructureSave.STRUCTURES.add(this);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        CompoundNBT size = new CompoundNBT();
        size.putInt("x", this.size.getX());
        size.putInt("y", this.size.getY());
        size.putInt("z", this.size.getZ());
        CompoundNBT corner = new CompoundNBT();
        corner.putInt("x", this.corner.getX());
        corner.putInt("y", this.corner.getY());
        corner.putInt("z", this.corner.getZ());
        compound.put("size", size);
        compound.put("corner", corner);
        return super.save(compound);
    }

     */

    private boolean isBetween(int lowerBound, int upperBound, int toCheck) {
        return lowerBound <= toCheck && upperBound >= toCheck;
    }

    public boolean isBlockInStructure(BlockPos pos) {
        return isBetween(corner.getX(), corner.getX() + size.getX() - 1, pos.getX()) &&
                isBetween(corner.getY(), corner.getY() + size.getY() - 1, pos.getY()) &&
                isBetween(corner.getZ(), corner.getZ() + size.getZ() - 1, pos.getZ());
    }

    public abstract void interactWith(AbstractMBStructure structure, BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit);

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
