package com.example.Util.MultiBlocks.StructureChecks;

import com.example.Util.MultiBlocks.MultiBlockData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.atomic.AtomicReference;

public class StructureFindCorners extends CountedCompleter<List<BlockPos>> {

    private final AtomicReference<List<BlockPos>> posList;


    public StructureFindCorners(CountedCompleter<List<BlockPos>> parent, MultiBlockData data, World w, BlockPos pos, AtomicReference<List<BlockPos>> posList) {
        this.posList = posList;
    }


    @Override
    public void compute() {

    }

    @Override
    public List<BlockPos> getRawResult() {
        return super.getRawResult();
    }
}
