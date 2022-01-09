package com.example.Machines.Planter;

import com.example.Inits.BlockInit;
import com.example.Inits.ContainerTypes;
import com.example.SuperClasses.AbstractContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class PlanterContainer extends AbstractContainer {

    public static PlanterContainer getClientContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        // init client inventory with dummy slots
        return new PlanterContainer(id, new InvWrapper(playerInventory), new ItemStackHandler(12), BlockPos.ZERO, playerInventory.player);
    }


    public static IContainerProvider getServerContainerProvider(PlanterTE te, BlockPos activationPos) {
        return (id, playerInventory, serverPlayer) -> new PlanterContainer(id, new InvWrapper(playerInventory), te.getHandler(), activationPos, serverPlayer);
    }

    public PlanterContainer(int id, IItemHandlerModifiable playerInv, IItemHandlerModifiable containerInv, BlockPos pos, PlayerEntity player) {
        super(ContainerTypes.PLANTER_CONTAINER.get(), id, playerInv, player, pos);
        for (int i = 0; i < 9; i++) {
            this.addSlot(new PlantSlot(containerInv, i, 9 + (i / 3) * 18, 18 + (i % 3) * 18));
        }
        for (int k = 0; k < 3; k++) {
            this.addSlot(new UpgradeSlot(containerInv, k + 9, 151, 18 + k * 18));
        }
    }


    //0-35 Player_inventory
    //36-44 PlantSlots
    //45-47 UpgradeSlots

    public class PlantSlot extends SlotItemHandler {
        public PlantSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock().is(BlockTags.SAPLINGS);
        }
    }

    @Override
    protected boolean moveItemStackTo(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
        return super.moveItemStackTo(p_75135_1_, p_75135_2_, p_75135_3_, p_75135_4_);
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

    @Override
    public boolean stillValid(PlayerEntity p_75145_1_) {
        return stillValid(worldPosCallable, p_75145_1_, BlockInit.PLANTER.get());
    }
}
