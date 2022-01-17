package com.example.MultiBlockStructure;

import com.example.Util.MultiBlocks.StructureSave;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MultiBlockSuper extends Block {
    public static final BooleanProperty inValidStructure = BooleanProperty.create("invalidstructure");

    public MultiBlockSuper() {
        super(AbstractBlock.Properties.of(Material.METAL));
        registerDefaultState(this.defaultBlockState().setValue(inValidStructure, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(inValidStructure);
        super.createBlockStateDefinition(builder);
    }
    @Nonnull
    @Override
    public ActionResultType use(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        if (!state.getValue(inValidStructure)) {
            return ActionResultType.PASS;
        }
        if (!(worldIn instanceof ServerWorld)) return ActionResultType.CONSUME;
        StructureSave save = StructureSave.getData((ServerWorld) worldIn);
        List<AbstractMBStructure> structures = save.STRUCTURES.parallelStream().filter(k -> k.isBlockInStructure(pos)).collect(Collectors.toList());
        if (!structures.isEmpty()) {
            AtomicReference<AbstractMBStructure> structure = new AtomicReference<>();
            structures.stream().findFirst().ifPresent(structure::set);
            return structure.get().interactWith(structure.get(), state, worldIn, pos, player, handIn, hit);

        }
        return ActionResultType.PASS;
    }
}
