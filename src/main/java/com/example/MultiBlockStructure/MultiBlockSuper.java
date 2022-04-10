package com.example.MultiBlockStructure;

import com.example.Inits.TileEntityTypes;
import com.example.Util.MultiBlocks.StructureSave;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IExtensibleEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MultiBlockSuper extends Block {
    public static final BooleanProperty inValidStructure = BooleanProperty.create("in_valid_structure");
    public static final BooleanProperty isCorner = BooleanProperty.create("is_corner");
    public static final EnumProperty<EStructureType> structureType = EnumProperty.create("structure_type", EStructureType.class);

    public MultiBlockSuper() {
        super(AbstractBlock.Properties.of(Material.METAL));
        registerDefaultState(this.defaultBlockState().setValue(inValidStructure, false).setValue(isCorner, false).setValue(structureType, EStructureType.NONE));
    }

    private enum EStructureType implements IStringSerializable {
        NONE("none"),
        REACTOR("reactor"),
        FUSION_REACTOR("fusion_reactor"),
        FISSION_REACTOR("fission_reactor");

        public final String name;

        EStructureType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return name;
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(isCorner);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        switch (state.getValue(structureType)) {
            case REACTOR:
                return TileEntityTypes.REACTOR.get().create();
            case FUSION_REACTOR:
                return TileEntityTypes.REACTOR.get().create();
            case FISSION_REACTOR:
                return TileEntityTypes.REACTOR.get().create();
            default:
                return null;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(inValidStructure, isCorner,structureType);
        super.createBlockStateDefinition(builder);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
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
