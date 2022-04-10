package com.example.MultiBlockStructure.ReactorStructure;

import com.example.Inits.BlockInit;
import com.example.Inits.ContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactorContainer extends Container {
    public static final TranslationTextComponent TITLE = new TranslationTextComponent("container.reactor");

    public static IContainerProvider getServerContainerProvider(ReactorTE te, BlockPos activationPos) {
        return (id, playerInventory, serverPlayer) -> new ReactorContainer(id, new InvWrapper(playerInventory), te.getInventory(), serverPlayer, activationPos);
    }

    public static ReactorContainer getClientContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        return new ReactorContainer(id, new InvWrapper(playerInventory), new ItemStackHandler(12), playerInventory.player, BlockPos.ZERO);
    }
    private final IWorldPosCallable worldPosCallable;
    protected ReactorContainer(int id, IItemHandlerModifiable playerInv, IItemHandlerModifiable storageInv, PlayerEntity player, BlockPos pos) {
        super(ContainerTypes.REACTOR_CONTAINER.get(), id);
        worldPosCallable = IWorldPosCallable.create(player.level, pos);
        //PlayerInventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new SlotItemHandler(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        //Playerhotbar
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new SlotItemHandler(playerInv, k, 8 + k * 18, 142));
        }
        //ContainerSlots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new BoneMealSlot(storageInv, i, 9 + (i / 3) * 18, 18 + (i % 3) * 18));
        }
        for (int k = 0; k < 3; ++k) {
            this.addSlot(new SlotItemHandler(storageInv, k + 9, 151, 18 + k * 18));
        }
    }
    public static class BoneMealSlot extends SlotItemHandler {
        public BoneMealSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return stack.getItem() == Items.BONE_MEAL;
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return stillValid(worldPosCallable, player, BlockInit.REACTOR_FRAME.get()) || stillValid(worldPosCallable,player,BlockInit.REACTOR_GLASS.get());
    }
    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int index) {
        if (index >= 36) {
            moveItemStackTo(this.getItems().get(index), 0, 35, false);
        } else {
            moveItemStackTo(this.getItems().get(index), 36, 48, false);
        }
        return ItemStack.EMPTY;
    }
}
