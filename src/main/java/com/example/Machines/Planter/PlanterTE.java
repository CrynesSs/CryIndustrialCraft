package com.example.Machines.Planter;

import com.example.Inits.TileEntityTypes;
import com.example.Machines.MachineSupers.MachineTE;
import com.example.Machines.Upgrades.RangeUpgrade;
import com.example.Machines.Upgrades.SpeedUpgradeItem;
import com.example.Machines.Upgrades.UpgradeItem;
import com.example.SuperClasses.AbstractMachine;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlanterTE extends MachineTE {
    private boolean canPlant = false;
    private byte tick = 0;
    private byte delay = 100;
    private short energy_per_plant = 200;
    private int energy_stored = 0;
    public byte field = 3;
    private BlockPos lastPlant;

    public PlanterTE() {
        //Slot 0-8 = Plants, Slot 9-11 Upgrade Slots
        super(TileEntityTypes.PLANTER_TE.get());
    }

    public IItemHandlerModifiable getHandler() {
        return INVENTORY.orElseThrow(() -> new IllegalStateException("Inventory not Initialized"));
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getInventory() {
        return null;
    }


    private void updateTile() {
        this.requestModelDataUpdate();
        this.setChanged();
        if (this.getLevel() != null) {
            this.getLevel().sendBlockUpdated(worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (Direction.Plane.HORIZONTAL.test(side) && cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return getInventory().cast();
        }
        return super.getCapability(cap, side);
    }


    public boolean attemptPlant() {
        if (level == null) {
            return false;
        }
        BlockState state = level.getBlockState(worldPosition);
        Direction facing = state.getValue(AbstractMachine.FACING);
        BushBlock toPlant = null;
        int slotNum = -1;
        for (int k = 0; k < 9; k++) {
            if (!getHandler().getStackInSlot(k).isEmpty()) {
                toPlant = (BushBlock) ((BlockItem) getHandler().getStackInSlot(k).getItem()).getBlock();
                slotNum = k;
                break;
            }
        }
        if (toPlant == null) {
            return false;
        }
        byte newField = field;
        for (int k = 0; k < 3; k++) {
            ItemStack stack = getHandler().getStackInSlot(k + 9);
            if (stack.getItem() instanceof UpgradeItem) {
                if (stack.getItem() instanceof RangeUpgrade) {
                    newField += (byte) ((RangeUpgrade) stack.getItem()).getStrenght();
                }
            }
        }
        /*DNW
        if (lastPlant != null) {
            switch (facing) {
                case SOUTH:
                case NORTH: {
                    i += Math.abs(lastPlant.getX());
                    j += Math.abs(lastPlant.getZ());
                    break;
                }
                case EAST:
                case WEST: {
                    i += Math.abs(lastPlant.getZ());
                    j += Math.abs(lastPlant.getX());
                    break;
                }
            }
        }

         */
        for (int i = -(newField - 1) / 2; i <= (newField - 1) / 2; i++) {
            for (int j = 3; j <= newField - 1 + 3; j++) {
                switch (facing) {
                    case NORTH: {
                        // i, -j
                        if (plantHelper(i, -j, toPlant, slotNum)) {
                            return true;
                        }
                        break;
                    }
                    case SOUTH: {
                        if (plantHelper(i, j, toPlant, slotNum)) {
                            return true;
                        }
                        break;
                    }
                    case EAST: {
                        if (plantHelper(j, i, toPlant, slotNum)) {
                            return true;
                        }
                        break;
                    }
                    case WEST: {
                        if (plantHelper(-j, i, toPlant, slotNum)) {
                            return true;
                        }
                        break;
                    }
                }
            }
        }
        //lastPlant = null;
        return false;
    }

    private boolean plantHelper(int x, int z, BushBlock plant, int slotNum) {
        BlockState state = level.getBlockState(worldPosition.offset(x, 0, z));
        BlockState downState = level.getBlockState(worldPosition.offset(x, 0, z).below());
        if (state.is(Blocks.AIR) && isValidGround(downState)) {
            level.setBlock(worldPosition.offset(x, 0, z), plant.defaultBlockState(), 3);
            getHandler().getStackInSlot(slotNum).shrink(1);
            //lastPlant = new BlockPos(x, 0, z);
            return true;
        }
        return false;
    }


    protected boolean isValidGround(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.PODZOL) || state.is(Blocks.FARMLAND);
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }
        tick++;
        float speedfactor = 1;
        for (int i = 0; i < 3; i++) {
            ItemStack stack = getHandler().getStackInSlot(i + 9);
            if (stack.getItem() instanceof SpeedUpgradeItem) {
                speedfactor += ((SpeedUpgradeItem) stack.getItem()).getStrenght() * 0.1;
            }
        }
        if (tick >= delay / speedfactor) {
            canPlant = true;
        }
        if (canPlant) {
            if (attemptPlant()) {
                tick = 0;
                canPlant = false;
            } else {
                tick--;
            }

        }
    }

    //Method from Superclass only used in Superclass
    @Nonnull
    public IItemHandlerModifiable createInventory() {
        return new ItemStackHandler(12) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot < 9) {
                    return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock().is(BlockTags.SAPLINGS);
                } else {
                    return stack.getItem() instanceof UpgradeItem;
                }
            }

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
                updateTile();
            }
        };
    }

}
