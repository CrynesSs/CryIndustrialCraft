package com.example.MultiBlockStructure;

import com.example.Util.MultiBlocks.StructureSave;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.List;
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

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!state.getValue(inValidStructure)) {
            return ActionResultType.PASS;
        }
        List<AbstractMBStructure> structures = StructureSave.STRUCTURES.parallelStream().filter(k -> k.isBlockInStructure(pos)).collect(Collectors.toList());
        if (!structures.isEmpty()) {
            structures.stream().findFirst().ifPresent(k -> k.interactWith(k, state, worldIn, pos, player, handIn, hit));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.CONSUME;
    }
}
