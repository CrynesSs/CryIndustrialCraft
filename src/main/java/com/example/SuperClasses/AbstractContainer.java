package com.example.SuperClasses;

import com.example.Machines.Upgrades.UpgradeItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public abstract class AbstractContainer extends Container {
    public final IItemHandlerModifiable PLAYERINVENTORY;
    protected final IWorldPosCallable worldPosCallable;


    protected AbstractContainer(ContainerType<?> containerTypeIn, int id, IItemHandlerModifiable playerInventory, PlayerEntity player, BlockPos pos) {
        super(containerTypeIn, id);
        this.worldPosCallable = IWorldPosCallable.create(player.level, pos);
        this.PLAYERINVENTORY = playerInventory;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new SlotItemHandler(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new SlotItemHandler(playerInventory, k, 8 + k * 18, 142));
        }

    }

    public static class UpgradeSlot extends SlotItemHandler {
        public UpgradeSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return stack.getItem() instanceof UpgradeItem;
        }
    }

    public static class TakeOnlySlot extends SlotItemHandler {

        public TakeOnlySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return false;
        }
    }

}
