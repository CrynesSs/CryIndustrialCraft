package com.example.Machines.MachineSupers;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public abstract class MachineTE extends TileEntity implements ITickableTileEntity {
    public final LazyOptional<IItemHandlerModifiable> INVENTORY = LazyOptional.of(this::createInventory);

    public abstract IItemHandlerModifiable createInventory();

    public MachineTE(TileEntityType<?> type) {
        super(type);
        this.setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        compound.put("inventory", ((ItemStackHandler) getHandler()).serializeNBT());
        return compound;
    }


    public void load(BlockState blockstate, CompoundNBT compound) {
        super.load(blockstate, compound);
        if (compound.contains("inventory")) {
            ((ItemStackHandler) getHandler()).deserializeNBT((CompoundNBT) compound.get("inventory"));
        }
        this.setChanged();
    }

    public IItemHandlerModifiable getHandler() {
        return INVENTORY.orElseThrow(() -> new IllegalStateException("Inventory not Initialized"));
    }

    public LazyOptional<IItemHandlerModifiable> getInventory() {
        return INVENTORY;
    }

    public int calculateComparatorOutput() {
        float itemCount = 0;
        for (int i = 0; i < getHandler().getSlots(); ++i) {
            itemCount += (float) getHandler().getStackInSlot(i).getCount() / (float) getHandler().getStackInSlot(i).getMaxStackSize();
        }
        return (int) Math.floor(itemCount * 15);
    }

    @Override
    public void tick() {

    }
}
