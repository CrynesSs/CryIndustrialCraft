package com.example.Cables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class AbstractCable extends Block {
    public AbstractCable(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public void onPlace(@Nonnull BlockState state_now, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state_before, boolean p_220082_5_) {
        List<CableNetwork> blockList = Arrays.stream(Direction.values()).map(pos::relative).map(CableNetwork::retrieveCableNetworkFromPos).collect(Collectors.toList());
        blockList.parallelStream().reduce(CableNetwork.MERGE).ifPresent((d)->System.out.println("Merge Success yo"));
    }
}
