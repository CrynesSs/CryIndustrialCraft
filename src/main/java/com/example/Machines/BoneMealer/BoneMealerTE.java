package com.example.Machines.BoneMealer;

import com.example.Inits.TileEntityTypes;
import com.example.Machines.MachineSupers.MachineTE;
import com.example.Machines.Upgrades.RangeUpgrade;
import com.example.Machines.Upgrades.SpeedUpgradeItem;
import com.example.Machines.Upgrades.UpgradeItem;
import com.example.SuperClasses.AbstractMachine;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BoneMealerTE extends MachineTE {
    public BoneMealerTE() {
        super(TileEntityTypes.BONEMEALER_TE.get());
    }

    private short tick = 0;
    private boolean canBoneMeal = false;
    public short field = 3;
    private BlockPos curTarget = null;


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (Direction.Plane.HORIZONTAL.test(side) && cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return getInventory().cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }
        tick++;
        if (!hasBoneMeal(getHandler())) {
            if (tick == 200) {
                tick--;
            }
            return;
        }
        float speed_factor = 1;
        for (int i = 0; i < 3; i++) {
            ItemStack stack = getHandler().getStackInSlot(i);
            if (stack.getItem() instanceof SpeedUpgradeItem) {
                speed_factor += ((SpeedUpgradeItem) stack.getItem()).getStrenght() * 0.1;
            }
        }
        short delay = 200;
        if (tick >= (delay / speed_factor)) {
            canBoneMeal = true;
        }
        if (canBoneMeal) {
            if (attemptBoneMeal()) {
                tick = 0;
                canBoneMeal = false;
            } else {
                tick--;
            }

        }
    }

    private void removeBonemeal() {
        for (int i = 0; i < getHandler().getSlots(); ++i) {
            if (getHandler().getStackInSlot(i).getItem() == Items.BONE_MEAL) {
                ItemStack stack = getHandler().getStackInSlot(i);
                stack.shrink(1);
                getHandler().setStackInSlot(i, stack);
                return;
            }
        }
    }

    private boolean hasBoneMeal(IItemHandlerModifiable handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            if (handler.getStackInSlot(i).getItem() == Items.BONE_MEAL) {
                return true;
            }
        }
        return false;
    }

    private boolean attemptBoneMeal() {
        if (level == null) {
            return false;
        }
        short newField = field;
        BlockState state = level.getBlockState(worldPosition);
        Direction facing = state.getValue(AbstractMachine.FACING);
        if (curTarget != null && level.getBlockState(curTarget).getBlock() instanceof IGrowable) {
            if (((IGrowable) level.getBlockState(curTarget).getBlock()).isValidBonemealTarget(level, curTarget, level.getBlockState(curTarget), false)) {
                ((IGrowable) level.getBlockState(curTarget).getBlock()).performBonemeal((ServerWorld) level, level.random, curTarget, level.getBlockState(curTarget));
                removeBonemeal();
                return true;
            } else {
                curTarget = null;
            }
        }
        for (int k = 0; k < 3; k++) {
            ItemStack stack = getHandler().getStackInSlot(k);
            if (stack.getItem() instanceof RangeUpgrade) {
                newField += (short) ((RangeUpgrade) stack.getItem()).getStrenght();
            }
        }
        List<BlockPos> bonemealTargets = new ArrayList<>();
        for (int i = -(newField - 1) / 2; i <= (newField - 1) / 2; i++) {
            for (int j = 3; j <= newField - 1 + 3; j++) {
                switch (facing) {
                    case NORTH: {
                        // i, -j
                        if (level.getBlockState(worldPosition.offset(i, 0, -j)).getBlock() instanceof IGrowable && ((IGrowable) level.getBlockState(worldPosition.offset(i, 0, -j)).getBlock()).isValidBonemealTarget(level, worldPosition.offset(-j, 0, i), level.getBlockState(worldPosition.offset(-j, 0, i)), false)) {
                            bonemealTargets.add(worldPosition.offset(i, 0, -j));
                        }
                        break;
                    }
                    case SOUTH: {
                        //i,j
                        if (level.getBlockState(worldPosition.offset(i, 0, j)).getBlock() instanceof IGrowable && ((IGrowable) level.getBlockState(worldPosition.offset(i, 0, j)).getBlock()).isValidBonemealTarget(level, worldPosition.offset(-j, 0, i), level.getBlockState(worldPosition.offset(-j, 0, i)), false)) {
                            bonemealTargets.add(worldPosition.offset(i, 0, j));
                        }
                        break;
                    }
                    case EAST: {
                        //j,i
                        if (level.getBlockState(worldPosition.offset(j, 0, i)).getBlock() instanceof IGrowable && ((IGrowable) level.getBlockState(worldPosition.offset(j, 0, i)).getBlock()).isValidBonemealTarget(level, worldPosition.offset(-j, 0, i), level.getBlockState(worldPosition.offset(-j, 0, i)), false)) {
                            bonemealTargets.add(worldPosition.offset(j, 0, i));
                        }
                        break;
                    }
                    case WEST: {
                        //-j,i
                        if (level.getBlockState(worldPosition.offset(-j, 0, i)).getBlock() instanceof IGrowable && ((IGrowable) level.getBlockState(worldPosition.offset(-j, 0, i)).getBlock()).isValidBonemealTarget(level, worldPosition.offset(-j, 0, i), level.getBlockState(worldPosition.offset(-j, 0, i)), false)) {
                            bonemealTargets.add(worldPosition.offset(-j, 0, i));
                        }
                        break;
                    }
                }
            }
        }
        if (bonemealTargets.isEmpty()) {
            return false;
        }
        BlockPos toBoneMeal = bonemealTargets.get((int) (Math.random() * bonemealTargets.size()));
        ((IGrowable) level.getBlockState(toBoneMeal).getBlock()).performBonemeal((ServerWorld) level, level.random, toBoneMeal, level.getBlockState(toBoneMeal));
        removeBonemeal();
        if (curTarget == null) {
            curTarget = toBoneMeal;
        }
        return true;
    }

    @Override
    public IItemHandlerModifiable createInventory() {
        return new ItemStackHandler(12) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot < 9) {
                    return stack.getItem() == Items.BONE_MEAL || stack.getItem() == Items.BONE_BLOCK || stack.getItem() == Items.BONE;
                } else {
                    return stack.getItem() instanceof UpgradeItem;
                }
            }
        };
    }
}
