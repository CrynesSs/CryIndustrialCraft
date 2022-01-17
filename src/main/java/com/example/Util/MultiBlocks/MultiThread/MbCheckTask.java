package com.example.Util.MultiBlocks.MultiThread;

import com.example.Util.MultiBlocks.MultiBlockData;
import com.example.Util.MultiBlocks.StructureChecks.StructureCheckMain;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MbCheckTask implements Callable<Boolean> {
    private final MultiBlockData data;
    private final BlockPos corner;
    private final World w;
    private final ECalculationStep step;
    private final BlockPos size;

    public MbCheckTask(MultiBlockData data, BlockPos corner, World world, ECalculationStep step, BlockPos size) {
        this.data = data;
        this.corner = corner;
        this.w = world;
        this.step = step;
        this.size = size;
    }

    @Override
    public Boolean call() {
        System.out.println("I am checking something i guess at " + step);
        BlockPos diagonalBlock = corner.offset(size.getX() - 1, 0, size.getZ() - 1);
        List<String> blockNames = Arrays.stream(data.config.BLOCKS.get(step.name)).map(k -> data.config.getBlocknameByKey(k)).collect(Collectors.toList());
        blockNames = addBlockGroupNames(blockNames, step).stream().distinct().filter(Objects::nonNull).collect(Collectors.toList());
        switch (step) {
            case FRAME: {
                return StructureCheckMain.validateFixedSizeFrame(corner, w, data, size, blockNames, diagonalBlock);
            }
            case TOP: {
                return StructureCheckMain.validateTopFixedSize(corner, w, data, size, blockNames, diagonalBlock);
            }
            case BOTTOM: {
                return StructureCheckMain.validateBottomFixedSize(corner, w, data, size, blockNames, diagonalBlock);
            }
            case SIDE: {
                return StructureCheckMain.validateFacesFixedSize(corner, w, data, size, blockNames, diagonalBlock);
            }
            case INSIDE: {
                List<String> blockNamesInside = new ArrayList<>();
                if (data.isHollow) {
                    blockNamesInside.add("minecraft:air");
                }
                return (StructureCheckMain.validateInsideFixedSize(corner, w, data, size, blockNamesInside, diagonalBlock));
            }
        }
        return false;
    }

    public List<String> addBlockGroupNames(List<String> currentBlockNames, ECalculationStep step) {
        if (Arrays.stream(data.config.BLOCKS.get(step.name)).anyMatch(data.config.BLOCKGROUPS.keySet()::contains)) {
            //*First we get the Data from the Blocks Map and stream the Data we received
            currentBlockNames.addAll(Arrays.stream(data.config.BLOCKS.get(step.name))
                    //*Filtering for any BlockGroup keys
                    .filter(data.config.BLOCKGROUPS.keySet()::contains)
                    //*Get the BlockGroups as String[]
                    .map(data.config.BLOCKGROUPS::get)
                    //*Flatmap the arrays into a single Stream<String>
                    .flatMap(Arrays::stream)
                    //*Map the Strings into usable BlockNames
                    .map(s -> data.config.getBlocknameByKey(s))
                    //*Collect into a List to add to the other List
                    .collect(Collectors.toList()));
        }
        return currentBlockNames;
    }

    public enum ECalculationStep {
        TOP("top"),
        BOTTOM("bottom"),
        FRAME("frame"),
        SIDE("side"),
        INSIDE("inside");
        public final String name;

        ECalculationStep(String name) {
            this.name = name;
        }
    }
}
