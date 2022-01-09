package com.example.Util.MultiBlocks.StructureChecks;

import com.example.MultiBlockStructure.AbstractMBStructure;
import com.example.Util.Helpers.ArrayHelpers;
import com.example.Util.MultiBlocks.MultiBlockData;
import com.example.Util.MultiBlocks.StructureSave;
import com.example.Util.SimpleJsonDataManager.DataManager;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class StructureCheck {

    /**
     * Checks if the Block Placed is completing a valid MultiBlockStructure
     *
     * @param block The Block Placed
     * @param world The World the Block was Placed in
     * @param pos   The BlockPos this took Place
     */
    public static boolean checkIfValidStructure(Block block, World world, BlockPos pos) {
        List<MultiBlockData> multiBlockDataList = new ArrayList<>(DataManager.MULTI_BLOCK_DATA.getAllData().values());
        //Filter out structures that don't contain this Block
        multiBlockDataList = multiBlockDataList.parallelStream().filter(k -> k.config.BLOCKKEYS.values().stream().anyMatch(l -> l.equals(Objects.requireNonNull(block.getRegistryName()).toString()))).collect(Collectors.toList());
        multiBlockDataList.parallelStream().forEach(multiBlockData -> {
            if (multiBlockData.isRegular) {
                if (multiBlockData.isFixedSize) {
                    StructureCheckFixedSize.checkBlocksFixedSize(block, world, pos, multiBlockData);
                } else {

                }
            }

        });
        return true;
    }

    private static List<BlockPos> findCorners(Block block, World world, BlockPos pos, MultiBlockData data) {
        int i = 0, k = 0, j = 0;
        List<BlockPos> posList = new ArrayList<>();
        for (; i < ArrayHelpers.findMaxValue(data.xSizes); i++) {
            for (; k < ArrayHelpers.findMaxValue(data.ySizes); k++) {
                for (; j < ArrayHelpers.findMaxValue(data.zSizes); j++) {
                    Block posBlock = world.getBlockState(pos.offset(-i, -j, -k)).getBlock();

                }
            }
        }
        return posList;
    }

    /**
     * @param world The World Object
     * @param pos   The Corner Pos of this Structure
     * @param data
     */
    public static void spawnStructure(World world, BlockPos pos, MultiBlockData data, BlockPos size) {
        AbstractMBStructure structure;
        try {
            structure = (AbstractMBStructure) data.structure.newInstance();
            structure.setCorner(pos);
            structure.setSize(size);
            structure.setBlocksValid(world);
            if (structure.hasBlockEntity()) {
                structure.setTileIntoWorld(world, pos);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new NullPointerException("Structure is Null");
        }
        StructureSave.STRUCTURES.add(structure);

    }
}
