package com.example.SuperClasses;

import com.example.Insulations.IInsulation;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public abstract class AbstractCableBlock extends BreakableBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final IntegerProperty INSULATION = IntegerProperty.create("insulation", 0, 3);

    public AbstractCableBlock(Properties properties) {
        super(AbstractBlock.Properties.of(Material.METAL).strength(3,3).noOcclusion().harvestTool(ToolType.PICKAXE));
        this.registerDefaultState(defaultBlockState().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE).setValue(DOWN, Boolean.FALSE).setValue(INSULATION, 0));

    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN, INSULATION);
        super.createBlockStateDefinition(builder);
    }

    public boolean addInsulation(ItemStack stack, World world, BlockState state, BlockPos pos) {
        if (stack.getItem() instanceof IInsulation && !((IInsulation) stack.getItem()).requiresTool()) {
            int insulationlevels = ((IInsulation) stack.getItem()).getCoatingAmounts();
            if (state.getValue(INSULATION) != 3) {
                return world.setBlock(pos, state.setValue(INSULATION, MathHelper.clamp(state.getValue(INSULATION) + insulationlevels, 0, 3)), 3);
            }
        } else {
            return false;
        }
        return false;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        if (addInsulation(player.getItemInHand(handIn), world, state, pos)) {
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.CONSUME;
        }
    }
}
