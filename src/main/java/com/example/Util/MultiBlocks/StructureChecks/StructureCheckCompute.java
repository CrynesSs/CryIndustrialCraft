package com.example.Util.MultiBlocks.StructureChecks;

import com.example.Util.MultiBlocks.MultiBlockData;
import com.example.Util.MultiBlocks.MultiThread.MbCheckTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureCheckCompute {
    public static boolean computeIfValid(MultiBlockData data, BlockPos corner, World world, BlockPos size) {
        MbCheckTask BOTTOM = new MbCheckTask(data, corner, world, MbCheckTask.ECalculationStep.BOTTOM, size);
        MbCheckTask TOP = new MbCheckTask(data, corner, world, MbCheckTask.ECalculationStep.TOP, size);
        MbCheckTask FRAME = new MbCheckTask(data, corner, world, MbCheckTask.ECalculationStep.FRAME, size);
        MbCheckTask INSIDE = new MbCheckTask(data, corner, world, MbCheckTask.ECalculationStep.INSIDE, size);
        MbCheckTask SIDE = new MbCheckTask(data, corner, world, MbCheckTask.ECalculationStep.SIDE, size);
        try {
            return BOTTOM.call() && TOP.call() && FRAME.call() && SIDE.call() && INSIDE.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
