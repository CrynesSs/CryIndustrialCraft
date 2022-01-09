package com.example.Machines.BoneMealer;

import com.example.Inits.BlockInit;
import com.example.Inits.ContainerTypes;
import com.example.SuperClasses.AbstractContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class BoneMealerContainer extends AbstractContainer {
    public static final ITextComponent TITLE = new TranslationTextComponent("contaienr.bonemealer");

    public BoneMealerContainer(int id, IItemHandlerModifiable inventoryIn, IItemHandlerModifiable containerInv, BlockPos pos, PlayerEntity player) {
        super(ContainerTypes.BONEMEALER_CONTAINER.get(), id, inventoryIn, player, pos);
        for (int i = 0; i < 9; i++) {
            this.addSlot(new BoneMealerSlot(containerInv, i, 9 + (i / 3) * 18, 18 + (i % 3) * 18));
        }
        for (int k = 0; k < 3; k++) {
            this.addSlot(new UpgradeSlot(containerInv, k + 9, 151, 18 + k * 18));
        }
    }

    public static class BoneMealerSlot extends SlotItemHandler {
        public BoneMealerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }
        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return stack.getItem() == Items.BONE_MEAL || stack.getItem() == Items.BONE_BLOCK || stack.getItem() == Items.BONE;
        }
    }

    public static BoneMealerContainer getClientContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        // init client inventory with dummy slots
        return new BoneMealerContainer(id, new InvWrapper(playerInventory), new ItemStackHandler(12), BlockPos.ZERO, playerInventory.player);
    }

    public static IContainerProvider getServerContainerProvider(BoneMealerTE te, BlockPos activationPos) {
        return (id, playerInventory, serverPlayer) -> new BoneMealerContainer(id, new InvWrapper(playerInventory), te.getHandler(), activationPos, serverPlayer);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(worldPosCallable, player, BlockInit.BONEMEALER_BLOCK.get());
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity playerIn, int index) {
        if (index >= 36) {
            moveItemStackTo(this.getItems().get(index), 0, 35, false);
        } else {
            moveItemStackTo(this.getItems().get(index), 36, 48, false);
        }
        return ItemStack.EMPTY;
    }
}
