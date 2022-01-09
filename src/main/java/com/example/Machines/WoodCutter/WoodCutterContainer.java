package com.example.Machines.WoodCutter;

import com.example.Inits.BlockInit;
import com.example.Inits.ContainerTypes;
import com.example.SuperClasses.AbstractContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class WoodCutterContainer extends AbstractContainer {
    public static final ITextComponent TITLE = new TranslationTextComponent("container.woodcutter");

    public WoodCutterContainer(int id, IItemHandlerModifiable playerInv, IItemHandlerModifiable itemStackHandler, BlockPos zero, PlayerEntity player) {
        super(ContainerTypes.WOODCUTTER_CONTAINER.get(), id, playerInv, player, zero);
        for (int k = 0; k < 3; k++) {
            this.addSlot(new UpgradeSlot(itemStackHandler, k, 9 + k * 25, 23));
        }
    }

    public static WoodCutterContainer getClientContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        // init client inventory with dummy slots
        return new WoodCutterContainer(id, new InvWrapper(playerInventory), new ItemStackHandler(12), BlockPos.ZERO, playerInventory.player);
    }

    public static IContainerProvider getServerContainerProvider(WoodCutterTE te, BlockPos activationPos) {
        return (id, playerInventory, serverPlayer) -> new WoodCutterContainer(id, new InvWrapper(playerInventory), te.getHandler(), activationPos, serverPlayer);
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity playerIn, int index) {
        if (index >= 36) {
            moveItemStackTo(this.getItems().get(index), 0, 35, false);
        } else {
            moveItemStackTo(this.getItems().get(index), 36, 39, false);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return stillValid(worldPosCallable, player, BlockInit.WOODCUTTER.get());
    }
}
