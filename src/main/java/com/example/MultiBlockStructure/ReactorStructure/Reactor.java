package com.example.MultiBlockStructure.ReactorStructure;

import com.example.Inits.BlockInit;
import com.example.MultiBlockStructure.AbstractMBStructure;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class Reactor extends AbstractMBStructure {

    public Reactor() {
        super();
    }

    @Override
    public ActionResultType interactWith(AbstractMBStructure structure, BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (state.is(BlockInit.REACTOR_GLASS.get())) {
            return ActionResultType.PASS;
        }
        System.out.println("Interacted with Reactor");
        ReactorTE te = getTileEntity(worldIn);
        IContainerProvider provider = ReactorContainer.getServerContainerProvider(te, pos);
        INamedContainerProvider namedProvider = new SimpleNamedContainerProvider(provider, ReactorContainer.TITLE);
        NetworkHooks.openGui((ServerPlayerEntity) player, namedProvider);
        player.awardStat(Stats.INTERACT_WITH_FURNACE);
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasBlockEntity() {
        return true;
    }

    public ReactorTE getTileEntity(World world) {
        if (world.getBlockEntity(this.getCorner()) instanceof ReactorTE) {
            return (ReactorTE) world.getBlockEntity(this.getCorner());
        }
        return null;
    }
}
