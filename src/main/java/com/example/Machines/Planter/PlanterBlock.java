package com.example.Machines.Planter;


import com.example.Inits.TileEntityTypes;
import com.example.SuperClasses.AbstractMachine;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class PlanterBlock extends AbstractMachine {
    public PlanterBlock() {
        super();
    }

    @Override
    public ActionResultType use(BlockState p_225533_1_, World world, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof PlanterTE && player instanceof ServerPlayerEntity) {
            PlanterTE te = (PlanterTE) tile;
            IContainerProvider provider = PlanterContainer.getServerContainerProvider(te, pos);
            INamedContainerProvider namedProvider = new SimpleNamedContainerProvider(provider, new StringTextComponent("PLANTER"));
            NetworkHooks.openGui((ServerPlayerEntity) player, namedProvider);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileEntityTypes.PLANTER_TE.get().create();
    }
}

