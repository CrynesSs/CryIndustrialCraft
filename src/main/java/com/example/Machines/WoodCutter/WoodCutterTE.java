package com.example.Machines.WoodCutter;

import com.example.Inits.TileEntityTypes;
import com.example.Machines.MachineSupers.MachineTE;
import com.example.Machines.Planter.PlanterBlock;
import com.example.Machines.Upgrades.RangeUpgrade;
import com.example.Machines.Upgrades.SmartModule;
import com.example.Machines.Upgrades.SpeedUpgradeItem;
import com.example.Machines.Upgrades.UpgradeItem;
import com.example.SuperClasses.AbstractMachine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MapItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class WoodCutterTE extends MachineTE {
    private boolean canBreak = false;
    private short tick = 0;
    private short energy_per_break = 250;
    private int energy_stored = 0;
    public short field = 3;
    private boolean saplingGotten = false;
    private int logsBrokenSinceLastSapling = 0;
    private short maxLogsPerSapling = 16;
    private BlockPos lastBreak = null;


    public WoodCutterTE() {
        super(TileEntityTypes.WOODCUTTER_TE.get());
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }
        tick++;
        float speed_factor = 1;
        for (int i = 0; i < 3; i++) {
            ItemStack stack = getHandler().getStackInSlot(i);
            if (stack.getItem() instanceof SpeedUpgradeItem) {
                speed_factor += ((SpeedUpgradeItem) stack.getItem()).getStrenght() * 0.1;
            }
        }
        short delay = 10;
        if (tick >= (delay / speed_factor)) {
            canBreak = true;
        }
        if (canBreak) {
            if (attemptBreak()) {
                tick = 0;
                canBreak = false;
            } else {
                tick--;
            }

        }
    }

    private boolean attemptBreak() {
        if (level == null) {
            return false;
        }
        BlockState state = level.getBlockState(worldPosition);
        Direction facing = state.getValue(PlanterBlock.FACING);
        short newField = field;
        boolean smart = false;
        for (int k = 0; k < 3; k++) {
            ItemStack stack = getHandler().getStackInSlot(k);
            if (stack.getItem() instanceof UpgradeItem) {
                if (stack.getItem() instanceof RangeUpgrade) {
                    newField += (short) ((RangeUpgrade) stack.getItem()).getStrenght();
                } else if (stack.getItem() instanceof SmartModule) {
                    smart = true;
                }
            }
        }
        if (smart && smartBreakHelper(newField)) {
            return true;
        }
        for (int i = -(newField - 1) / 2; i <= (newField - 1) / 2; i++) {
            for (int j = 3; j <= newField - 1 + 3; j++) {
                switch (facing) {
                    case NORTH: {
                        // i, -j
                        if (!smart && breakHelper(i, -j, newField)) {
                            return true;
                        }
                        break;
                    }
                    case SOUTH: {
                        if (!smart && breakHelper(i, j, newField)) {
                            return true;
                        }
                        break;
                    }
                    case EAST: {
                        if (!smart && breakHelper(j, i, newField)) {
                            return true;
                        }
                        break;
                    }
                    case WEST: {
                        if (!smart && breakHelper(-j, i, newField)) {
                            return true;
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (Direction.Plane.HORIZONTAL.test(side) && cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return getInventory().cast();
        }
        return super.getCapability(cap, side);
    }

    private boolean smartBreakHelper(short range) {
        //If we don't have a Sapling, try to break Leaves
        if (!saplingGotten && logsBrokenSinceLastSapling >= maxLogsPerSapling) {
            //If there are no leaves to be broken, break Wood. We never want to break more Leaves than necessary.
            if (!breakLeaves(range)) {
                //Return if we broke wood
                return breakWood(range);
            } else {
                return true;
            }
        } else {
            return breakWood(range);
        }
    }

    private boolean breakLeaves(short range) {
        if (level == null) {
            return false;
        }
        Direction facing = level.getBlockState(worldPosition).getValue(WoodCutterBlock.FACING);
        Vector3i dir = facing.getNormal();
        BlockPos startPos = worldPosition.offset(new BlockPos(dir.getX() * 3, dir.getY(), dir.getZ() * 3));
        //Going forward in direction of the Facing
        for (int i = 0; i < range; ++i) {
            //Going Sideways 90 Degrees from the Facing
            for (int k = -(range - 1) / 2; k <= (range - 1) / 2; k++) {
                //Y value check
                for (int j = 0; j < range; j++) {
                    //Switch on the Different facing Values as they need to be treated differently
                    BlockPos checkPos;
                    switch (facing) {
                        case NORTH: {
                            //North forward is (0,0,-1) sideways is (a,0,0) or (-a,0,0)
                            checkPos = startPos.offset(k, j, -i);
                            break;
                        }
                        case SOUTH: {
                            checkPos = startPos.offset(k, j, i);
                            break;
                        }
                        case EAST: {
                            checkPos = startPos.offset(i, j, k);
                            break;
                        }
                        case WEST: {
                            checkPos = startPos.offset(-i, j, k);
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unexpected value: " + facing);
                    }
                    BlockState state = level.getBlockState(checkPos);
                    //Check if the Block is Leaves
                    if (state.is(BlockTags.LEAVES)) {
                        //Get the Drops from the Destroyed Block
                        List<ItemStack> stacks = Block.getDrops(state, (ServerWorld) level, worldPosition, null);
                        level.setBlock(checkPos, Blocks.AIR.defaultBlockState(), 3);
                        if (!stacks.isEmpty()) {
                            boolean isSapling = stacks.get(0).getItem() instanceof BlockItem && ((BlockItem) stacks.get(0).getItem()).getBlock() instanceof SaplingBlock;
                            putItemInInventory(stacks.get(0));
                            if (isSapling) {
                                saplingGotten = true;
                                logsBrokenSinceLastSapling = 0;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Puts one item inside a Inventory next to the WoodCutter
    private void putItemInInventory(ItemStack stack) {
        AtomicReference<ItemStack> stackstack = new AtomicReference<>(stack);
        if (level == null) {
            return;
        }
        for (Direction d : Direction.Plane.HORIZONTAL) {
            TileEntity entity = level.getBlockEntity(worldPosition.offset(d.getNormal()));
            if (entity == null || entity.isRemoved() || stackstack.get().isEmpty()) {
                continue;
            }
            LazyOptional<IItemHandler> capability = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (capability.resolve().isPresent()) {
                capability.resolve().ifPresent(k -> {
                    for (int i = 0; i < k.getSlots(); ++i) {
                        if (stackstack.get().isEmpty()) {
                            return;
                        }
                        stackstack.set(k.insertItem(i, stack, false));
                    }
                });
            }

        }
        Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(this.level.random);
        BlockPos s = worldPosition.offset(level.getBlockState(worldPosition).getValue(AbstractMachine.FACING).getOpposite().getNormal());
        InventoryHelper.dropItemStack(level, s.getX(), s.getY(), s.getZ(), stack);
    }

    private boolean breakWood(short range) {
        if (level == null) {
            return false;
        }
        Direction facing = level.getBlockState(worldPosition).getValue(AbstractMachine.FACING);
        Vector3i dir = facing.getNormal();
        BlockPos startPos = worldPosition.offset(new BlockPos(dir.getX() * 3, dir.getY(), dir.getZ() * 3));
        if (lastBreak != null) {
            for (int j = 0; j < range; j++) {
                if (level.getBlockState(lastBreak.offset(0, j, 0)).is(BlockTags.LOGS)) {
                    List<ItemStack> stacks = Block.getDrops(level.getBlockState(lastBreak.offset(0, j, 0)), (ServerWorld) level, worldPosition, null);
                    level.setBlock(lastBreak.offset(0, j, 0), Blocks.AIR.defaultBlockState(), 3);
                    logsBrokenSinceLastSapling++;
                    if (!stacks.isEmpty()) {
                        putItemInInventory(stacks.get(0));
                    }
                    return true;
                }
            }
            lastBreak = null;
            saplingGotten = false;
            return false;
        }
        //This locates a Tree and saves the position inside lastBreak
        //Going forward in direction of the Facing
        for (int i = 0; i < range; ++i) {
            //Going Sideways 90 Degrees from the Facing
            for (int k = -(range - 1) / 2; k <= (range - 1) / 2; k++) {
                //Y value check
                for (int j = 0; j < range; j++) {
                    //Switch on the Different facing Values as they need to be treated differently
                    BlockPos checkPos;
                    switch (facing) {
                        case NORTH: {
                            //North forward is (0,0,-1) sideways is (a,0,0) or (-a,0,0)
                            checkPos = startPos.offset(k, j, -i);
                            break;
                        }
                        case SOUTH: {
                            checkPos = startPos.offset(k, j, i);
                            break;
                        }
                        case EAST: {
                            checkPos = startPos.offset(i, j, k);
                            break;
                        }
                        case WEST: {
                            checkPos = startPos.offset(-i, j, k);
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unexpected value: " + facing);
                    }
                    BlockState state = level.getBlockState(checkPos);
                    if (state.is(BlockTags.LOGS)) {
                        List<ItemStack> stacks = Block.getDrops(state, (ServerWorld) level, worldPosition, null);
                        level.setBlock(checkPos, Blocks.AIR.defaultBlockState(), 3);
                        if (!stacks.isEmpty()) {
                            putItemInInventory(stacks.get(0));
                            lastBreak = checkPos.offset(0, -j, 0);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Helps with Block Breaking
    public boolean breakHelper(int x, int z, int range) {
        if (level == null) {
            return false;
        }
        for (int i = 0; i < range + 2; i++) {
            //Get the Blockstate of the Block that could be broken
            BlockState s = level.getBlockState(worldPosition.offset(x, i, z));
            //check if it is a Leaves or Logs Block
            if (s.is(BlockTags.LOGS) || s.is(BlockTags.LEAVES)) {
                Vector3i v = level.getBlockState(worldPosition).getValue(WoodCutterBlock.FACING).getOpposite().getNormal();
                v = worldPosition.offset(v);
                //Generate Drops depending on if they are Leaves
                if (s.is(BlockTags.LEAVES)) {
                    List<ItemStack> stacks = Block.getDrops(s, (ServerWorld) level, worldPosition, null);
                    Vector3i finalV = v;
                    //Check all around the Block for valid Inventories
                    for (Direction d : Direction.Plane.HORIZONTAL) {
                        TileEntity entity = level.getBlockEntity(worldPosition.offset(d.getNormal()));
                        AtomicBoolean flag = new AtomicBoolean(false);
                        if (entity instanceof LockableLootTileEntity) {
                            stacks.forEach(k -> {
                                if (!flag.get()) {
                                    flag.set(HopperTileEntity.addItem(null, (IInventory) entity, k, level.getBlockState(worldPosition).getValue(WoodCutterBlock.FACING).getOpposite()).isEmpty());
                                }
                            });
                            if (flag.get()) {
                                level.setBlock(worldPosition.offset(x, i, z), Blocks.AIR.defaultBlockState(), 3);
                                return true;
                            }
                        }
                    }
                    stacks.forEach(k -> InventoryHelper.dropItemStack(level, finalV.getX(), finalV.getY(), finalV.getZ(), k));
                } else {
                    for (Direction d : Direction.Plane.HORIZONTAL) {
                        TileEntity entity = level.getBlockEntity(worldPosition.offset(d.getNormal()));
                        if (entity instanceof LockableLootTileEntity) {
                            boolean flag = HopperTileEntity.addItem(null, (IInventory) entity, new ItemStack(s.getBlock(), 1), level.getBlockState(worldPosition).getValue(WoodCutterBlock.FACING).getOpposite()).isEmpty();
                            if (flag) {
                                level.setBlock(worldPosition.offset(x, i, z), Blocks.AIR.defaultBlockState(), 3);
                                return true;
                            }
                        }
                    }
                    InventoryHelper.dropItemStack(level, v.getX(), v.getY(), v.getZ(), new ItemStack(s.getBlock(), 1));
                }
                level.setBlock(worldPosition.offset(x, i, z), Blocks.AIR.defaultBlockState(), 3);
                return true;
            }
        }
        return false;
    }

    @Override
    public IItemHandlerModifiable createInventory() {
        return new ItemStackHandler(3) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof UpgradeItem;
            }
        };
    }


}
