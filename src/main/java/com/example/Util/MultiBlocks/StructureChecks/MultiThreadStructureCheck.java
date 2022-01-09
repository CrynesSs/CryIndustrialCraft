package com.example.Util.MultiBlocks.StructureChecks;

import com.example.Util.MultiBlocks.MultiBlockData;
import com.example.Util.SimpleJsonDataManager.DataManager;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MultiThreadStructureCheck {

    public static boolean checkStructure(World w, BlockPos pos, Block placedBlock) {
        List<MultiBlockData> multiBlockDataList = new ArrayList<>(DataManager.MULTI_BLOCK_DATA.getAllData().values());
        multiBlockDataList = multiBlockDataList.parallelStream().filter(k -> k.config.BLOCKKEYS.values().parallelStream().anyMatch(l -> l.equals(Objects.requireNonNull(placedBlock.getBlock().getRegistryName()).toString()))).collect(Collectors.toList());
        if (multiBlockDataList.isEmpty()) {
            return false;
        }
        AtomicBoolean valid = new AtomicBoolean(true);
        List<BlockPos> valids = new ArrayList<>();
        multiBlockDataList.parallelStream().forEach(multiBlockData -> {
            if (!valid.get() || valids.size() > 0) {
                return;
            }
            if (multiBlockData.isRegular) {
                if (multiBlockData.isFixedSize) {
                    List<BlockPos> cornersFixedSize = StructureCheckFixedSize.findCornersFixedSize(placedBlock, w, pos, multiBlockData);
                    if (cornersFixedSize.isEmpty()) {
                        valid.set(false);
                        return;
                    }
                    cornersFixedSize.forEach(corner -> {
                        if (valids.size() > 0) {
                            return;
                        }
                        if (StructureCheckCompute.computeIfValid(multiBlockData,corner,w,new BlockPos(multiBlockData.xSizes[0], multiBlockData.ySizes[0], multiBlockData.zSizes[0]))) {
                            valids.add(corner);
                        }
                    });
                    if (valids.size() == 1) {
                        StructureCheck.spawnStructure(w,valids.get(0),multiBlockData,new BlockPos(multiBlockData.xSizes[0], multiBlockData.ySizes[0], multiBlockData.zSizes[0]));
                    }
                    System.out.println(cornersFixedSize);
                } else {

                }
            }
        });
        return valid.get();

    }
}
