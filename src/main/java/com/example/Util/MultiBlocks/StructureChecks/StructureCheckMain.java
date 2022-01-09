package com.example.Util.MultiBlocks.StructureChecks;

import com.example.Util.MultiBlocks.MultiBlockData;
import com.example.Util.SimpleJsonDataManager.DataManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StructureCheckMain {
    public static boolean checkStructure(World w, BlockPos pos, Block placedBlock) {
        List<MultiBlockData> multiBlockDataList = new ArrayList<>(DataManager.MULTI_BLOCK_DATA.getAllData().values());
        multiBlockDataList = multiBlockDataList.parallelStream().filter(k -> k.config.BLOCKKEYS.values().parallelStream().anyMatch(l -> l.equals(Objects.requireNonNull(placedBlock.getBlock().getRegistryName()).toString()))).collect(Collectors.toList());
        if (multiBlockDataList.isEmpty()) {
            return false;
        }
        for (MultiBlockData data : multiBlockDataList) {
            //*Check here when the Data is regular(Cuboid) and is of fixed Size (Only one possible Size like 3x3x3)
            if (data.isRegular && data.isFixedSize) {
                List<BlockPos> cornersFixedSize = StructureCheckFixedSize.findCornersFixedSize(placedBlock, w, pos, data);
                if (cornersFixedSize.isEmpty()) return false;
                for (BlockPos corner : cornersFixedSize) {
                    if (StructureCheckFixedSize.computeIfValid(data, corner, w, new BlockPos(data.xSizes[0], data.ySizes[0], data.zSizes[0]))) {
                        StructureCheckFixedSize.spawnStructure(w, corner, data, new BlockPos(data.xSizes[0], data.ySizes[0], data.zSizes[0]));
                        return true;
                    }
                }
                //*Check here when the Data is regular(Cuboid) and is not of fixed Size (multiple possible sizes like 3x3x3 OR 4x4x4)
            } else if (data.isRegular) {
                //*Sizematching means that the first value of the x sizes corresponds to the first value in the y and z sizes as well
                if (data.sizeMatching) {
                    com.mojang.datafixers.util.Pair<BlockPos, BlockPos> cornerAndSizePair = new Pair<>(BlockPos.ZERO, BlockPos.ZERO);
                    for (AtomicInteger i = new AtomicInteger(0); i.get() < data.xSizes.length; i.incrementAndGet()) {
                        //*Sets the current size of the Structure we are checking
                        BlockPos currentSize = new BlockPos(data.xSizes[i.get()], data.ySizes[i.get()], data.zSizes[i.get()]);
                        //*Find the corners for the structure that might be valid
                        List<BlockPos> cornersFixedSize = StructureCheckFixedSize.findCornersFixedSize(placedBlock, w, pos, data);
                        //*If there are no corners available, check another size
                        cornersFixedSize.removeIf(corner -> !StructureCheckFixedSize.computeIfValid(data, corner, w, currentSize));
                        if (cornersFixedSize.isEmpty()) continue;
                        BlockPos corner = cornersFixedSize.get(0);
                        StructureCheckFixedSize.spawnStructure(w, corner, data, currentSize);
                        return true;
                    }
                    //*Not sizematching so any xsize can occur with any y or z size. So we need to check every possible combination
                } else {
                    for (int curXSize : data.xSizes) {
                        for (int curYSize : data.ySizes) {
                            for (int curZSize : data.zSizes) {
                                BlockPos currentSize = new BlockPos(curXSize, curYSize, curZSize);
                                //*Find the corners for the structure that might be valid
                                List<BlockPos> cornersFixedSize = StructureCheckFixedSize.findCornersFixedSize(placedBlock, w, pos, data);
                                //*Remove the corner if it cannot be validated
                                cornersFixedSize.removeIf(corner -> !StructureCheckFixedSize.computeIfValid(data, corner, w, currentSize));
                                //*If there are no corners available, check another size
                                if (cornersFixedSize.isEmpty()) continue;
                                BlockPos corner = cornersFixedSize.get(0);
                                StructureCheckFixedSize.spawnStructure(w, corner, data, currentSize);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
