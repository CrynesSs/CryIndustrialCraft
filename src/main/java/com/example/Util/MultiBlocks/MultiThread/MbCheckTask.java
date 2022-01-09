package com.example.Util.MultiBlocks.MultiThread;

import com.example.Util.MultiBlocks.MultiBlockData;
import com.example.Util.MultiBlocks.StructureChecks.StructureCheckFixedSize;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MbCheckTask implements Callable<Boolean> {
    private MultiBlockData data;
    private BlockPos corner;
    private World w;
    private ECalculationStep step;
    private BlockPos size;

    public MbCheckTask(MultiBlockData data, BlockPos corner, World world, ECalculationStep step, BlockPos size) {
        this.data = data;
        this.corner = corner;
        this.w = world;
        this.step = step;
        this.size = size;
    }


    public ECalculationStep getStep() {
        return step;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("I am checking something i guess at " + step);
        BlockPos diagonalBlock = corner.offset(size.getX() - 1, 0, size.getZ() - 1);
        switch (step) {
            case FRAME: {
                List<String> blockNamesFrame = Arrays.stream(data.config.BLOCKS.get("frame")).map(k -> data.config.getBlocknameByKey(k)).collect(Collectors.toList());
                if (data.isRegular && data.isFixedSize) {
                    return (StructureCheckFixedSize.validateFixedSizeFrame(corner, w, data, size, blockNamesFrame, diagonalBlock));

                }
                break;
            }
            case TOP: {
                List<String> blockNamesTop = Arrays.stream(data.config.BLOCKS.get("top")).map(k -> data.config.getBlocknameByKey(k)).collect(Collectors.toList());
                if (data.isFixedSize && data.isRegular) {
                    return (StructureCheckFixedSize.validateTopFixedSize(corner, w, data, size, blockNamesTop, diagonalBlock));

                }
                break;
            }
            case BOTTOM: {
                List<String> blockNamesBottom = Arrays.stream(data.config.BLOCKS.get("bottom")).map(k -> data.config.getBlocknameByKey(k)).collect(Collectors.toList());
                if (data.isFixedSize && data.isRegular) {
                    return (StructureCheckFixedSize.validateBottomFixedSize(corner, w, data, size, blockNamesBottom, diagonalBlock));

                }
                break;
            }
            case SIDE: {
                List<String> blockNamesFaces = Arrays.stream(data.config.BLOCKS.get("face")).map(k -> data.config.getBlocknameByKey(k)).collect(Collectors.toList());
                if (data.isFixedSize && data.isRegular) {
                    return (StructureCheckFixedSize.validateFacesFixedSize(corner, w, data, size, blockNamesFaces, diagonalBlock));

                }
                break;
            }
            case INSIDE: {
                List<String> blockNamesInside = new ArrayList<>();
                if (data.isHollow) {
                    blockNamesInside.add("minecraft:air");
                }
                return (StructureCheckFixedSize.validateInsideFixedSize(corner, w, data, size, blockNamesInside, diagonalBlock));

            }
        }
        return false;
    }

    public enum ECalculationStep {
        TOP,
        BOTTOM,
        FRAME,
        SIDE,
        INSIDE;
    }


    public MbCheckTask setData(MultiBlockData data) {
        this.data = data;
        return this;
    }

    public MbCheckTask setCorner(BlockPos corner) {
        this.corner = corner;
        return this;
    }

    public MbCheckTask setW(World w) {
        this.w = w;
        return this;
    }

    public MbCheckTask setSize(BlockPos size) {
        this.size = size;
        return this;
    }





}
