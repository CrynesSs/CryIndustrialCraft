package com.example.MultiBlockStructure.ReactorStructure;

import com.example.Inits.TileEntityTypes;
import com.example.MultiBlockStructure.AbstractMBTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactorTE extends AbstractMBTileEntity<Reactor> {


    private final LazyOptional<IItemHandlerModifiable> inventory = LazyOptional.of(this::createInventory);

    @Nonnull
    public IItemHandlerModifiable createInventory() {
        return new ItemStackHandler(12) {
        };
    }

    public ReactorTE() {
        super(TileEntityTypes.REACTOR.get());
    }


    public IItemHandlerModifiable getInventory() {
        return inventory.orElseThrow(() -> new IllegalStateException("Inventory not initialized correctly"));
    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (Direction.Plane.HORIZONTAL.test(side) && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventory.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    public int cooldown = 0;

    public ReactorTE(Reactor reactor) {
        super(TileEntityTypes.REACTOR.get());
        structure = reactor;
    }
    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compoundNBT) {
        super.save(compoundNBT);
        compoundNBT.put("inventory", ((ItemStackHandler) getInventory()).serializeNBT());
        return compoundNBT;
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);
        ((ItemStackHandler) getInventory()).deserializeNBT((CompoundNBT) compoundNBT.get("inventory"));
        this.setChanged();
    }

    @Override
    public void tick() {
        if(cooldown == 20){
            System.out.println("I am a Reactor here");
            cooldown = 0;
        }
        cooldown++;

    }
}
