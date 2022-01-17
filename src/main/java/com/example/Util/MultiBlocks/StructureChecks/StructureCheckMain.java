package com.example.Util.MultiBlocks.StructureChecks;

import com.example.MultiBlockStructure.AbstractMBStructure;
import com.example.Util.MultiBlocks.MultiBlockData;
import com.example.Util.MultiBlocks.MultiThread.MbCheckTask;
import com.example.Util.MultiBlocks.StructureSave;
import com.example.Util.SimpleJsonDataManager.DataManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
                //*Create a new Variable for the Size
                BlockPos size = new BlockPos(data.xSizes[0], data.ySizes[0], data.zSizes[0]);
                //*Find the possible Corners of this Size
                List<BlockPos> cornersFixedSize = findCornersFixedSize(placedBlock, w, pos, size, data);
                if (cornersFixedSize.isEmpty()) return false;

                for (BlockPos corner : cornersFixedSize) {
                    if (computeIfValid(data, corner, w, size)) {
                        spawnStructure(w, corner, data, size);
                        return true;
                    }
                }
                //*Check here when the Data is regular(Cuboid) and is not of fixed Size (multiple possible sizes like 3x3x3 OR 4x4x4)
            } else if (data.isRegular) {
                //*Sizematching means that the first value of the x sizes corresponds to the first value in the y and z sizes as well
                if (data.sizeMatching) {
                    for (AtomicInteger i = new AtomicInteger(0); i.get() < data.xSizes.length; i.incrementAndGet()) {
                        //*Sets the current size of the Structure we are checking
                        BlockPos currentSize = new BlockPos(data.xSizes[i.get()], data.ySizes[i.get()], data.zSizes[i.get()]);
                        //*Find the corners for the structure that might be valid
                        List<BlockPos> cornersFixedSize = findCornersFixedSize(placedBlock, w, pos, currentSize, data);
                        //*If there are no corners available, check another size
                        cornersFixedSize.removeIf(corner -> !computeIfValid(data, corner, w, currentSize));
                        if (cornersFixedSize.isEmpty()) continue;
                        BlockPos corner = cornersFixedSize.get(0);
                        spawnStructure(w, corner, data, currentSize);
                        return true;
                    }
                    //*Not sizematching so any xsize can occur with any y or z size. So we need to check every possible combination
                } else {
                    for (int curXSize : data.xSizes) {
                        for (int curYSize : data.ySizes) {
                            for (int curZSize : data.zSizes) {
                                BlockPos currentSize = new BlockPos(curXSize, curYSize, curZSize);
                                //*Find the corners for the structure that might be valid
                                List<BlockPos> cornersFixedSize = findCornersFixedSize(placedBlock, w, pos, currentSize, data);
                                //*Remove the corner if it cannot be validated
                                cornersFixedSize.removeIf(corner -> !computeIfValid(data, corner, w, currentSize));
                                //*If there are no corners available, check another size
                                if (cornersFixedSize.isEmpty()) continue;
                                BlockPos corner = cornersFixedSize.get(0);
                                spawnStructure(w, corner, data, currentSize);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param data   The MultiblockStructureData of the Structure that we want to check
     * @param corner The Corner Block of the Structure. This is the lowest value Blockpos of x,y,z in the Structure
     * @param world  The Serverworld this check is performed on
     * @param size   The Size that is to Check
     * @return True if the Structure is valid
     */
    public static boolean computeIfValid(MultiBlockData data, BlockPos corner, World world, BlockPos size) {
        MbCheckTask BOTTOM = new MbCheckTask(data, corner, world, MbCheckTask.ECalculationStep.BOTTOM, size);
        MbCheckTask TOP = new MbCheckTask(data, corner, world, MbCheckTask.ECalculationStep.TOP, size);
        MbCheckTask FRAME = new MbCheckTask(data, corner, world, MbCheckTask.ECalculationStep.FRAME, size);
        MbCheckTask INSIDE = new MbCheckTask(data, corner, world, MbCheckTask.ECalculationStep.INSIDE, size);
        MbCheckTask SIDE = new MbCheckTask(data, corner, world, MbCheckTask.ECalculationStep.SIDE, size);

        try {
            System.out.println("Bottom : " + BOTTOM.call() + " Top : " + TOP.call() + " Frame : " + FRAME.call() + " Inside : " + INSIDE.call() + " SIDE : " + SIDE.call());
            return BOTTOM.call() && TOP.call() && FRAME.call() && SIDE.call() && INSIDE.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void spawnStructure(World world, BlockPos pos, MultiBlockData data, BlockPos size) {
        AbstractMBStructure structure;
        try {
            structure = (AbstractMBStructure) data.structure.newInstance();
            structure.setCorner(pos);
            structure.setSize(size);
            structure.setBlocksValid(world);
            structure.setData(data);
            if (structure.hasBlockEntity()) {
                structure.setTileIntoWorld(world, pos, structure);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new NullPointerException("Structure is Null");
        }
        if (world instanceof ServerWorld) {
            StructureSave.putStructure(structure, (ServerWorld) world);
        }
    }

    public static final HashSet<Chunk> CHUNKS = new HashSet<>();
    public static Chunk currentChunk = null;

    public static boolean validateFixedSizeFrame(BlockPos corner, World world, MultiBlockData data, BlockPos size, List<String> blockNames, BlockPos diagonalBlock) {
        //* Check for Bottom and Top Layer in x Direction (There will be overlap)
        for (int x = 0; x < size.getX(); ++x) {
            if (checkBlockNames(blockNames, world, corner.offset(x, 0, 0)) || checkBlockNames(blockNames, world, corner.offset(x, size.getY() - 1, 0)) || checkBlockNames(blockNames, world, diagonalBlock.offset(-x, 0, 0)) || checkBlockNames(blockNames, world, diagonalBlock.offset(-x, size.getY() - 1, 0))) {
                return false;
            }
        }
        //* Check for Bottom and Top Layer in Z Direction (There will be overlap)
        for (int z = 0; z < size.getZ(); ++z) {
            if (checkBlockNames(blockNames, world, corner.offset(0, 0, z)) || checkBlockNames(blockNames, world, corner.offset(0, size.getY() - 1, z)) || checkBlockNames(blockNames, world, diagonalBlock.offset(0, 0, -z)) || checkBlockNames(blockNames, world, diagonalBlock.offset(0, size.getY() - 1, -z))) {
                return false;
            }
        }
        //*Check the outer Frame for possible mismatches
        for (int y = 1; y < size.getY() - 2; ++y) {
            if (checkBlockNames(blockNames, world, corner.offset(0, y, 0)) || checkBlockNames(blockNames, world, diagonalBlock.offset(0, y, 0)) || checkBlockNames(blockNames, world, corner.offset(size.getX() - 1, y, 0)) || checkBlockNames(blockNames, world, corner.offset(0, y, size.getZ() - 1))) {
                return false;
            }
        }
        return true;

    }

    //this should also work for rectangular structures now with the new Checks in Place.Doing this Part by Part reduces time spent with useless calculations
    public static boolean validateFacesFixedSize(BlockPos corner, World world, MultiBlockData data, @Nonnull BlockPos size, List<String> blockNames, BlockPos diagonalBlock) {
        for (int y = 1; y < size.getY() - 1; ++y) {
            for (int x = 1; x < size.getX() - 1; ++x) {
                if (checkBlockNames(blockNames, world, corner.offset(x, y, 0)) || checkBlockNames(blockNames, world, diagonalBlock.offset(-x, y, 0))) {
                    return false;
                }
            }
            for (int z = 1; z < size.getZ() - 1; ++z) {
                if (checkBlockNames(blockNames, world, corner.offset(0, y, z)) || checkBlockNames(blockNames, world, diagonalBlock.offset(0, y, -z))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean validateInsideFixedSize(BlockPos corner, World world, MultiBlockData data, BlockPos size, List<String> blockNames, BlockPos diagonalBlock) {
        if (data.isHollow) {
            BlockPos offsetCorner = corner.offset(1, 1, 1);
            for (int i = 0; i < size.getX() - 2; ++i) {
                for (int j = 0; j < size.getZ() - 2; ++j) {
                    for (int k = 0; k < size.getY() - 2; ++k) {
                        if (checkBlockNames(blockNames, world, offsetCorner.offset(i, k, j))) {
                            return false;
                        }
                    }
                }
            }
            return true;

        } //TODO Check here if Blocks are Inside
        else {
            return false;
        }
    }

    public static boolean validateTopFixedSize(BlockPos corner, World world, MultiBlockData data, BlockPos size, List<String> blockNames, BlockPos diagonalBlock) {
        BlockPos offsetCorner = corner.offset(1, size.getY() - 1, 1);
        for (int i = 0; i < size.getX() - 2; ++i) {
            for (int j = 0; j < size.getZ() - 2; ++j) {
                if (checkBlockNames(blockNames, world, offsetCorner.offset(i, 0, j))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean validateBottomFixedSize(BlockPos corner, World world, MultiBlockData data, BlockPos size, List<String> blockNames, BlockPos diagonalBlock) {
        BlockPos offsetCorner = corner.offset(1, 0, 1);
        for (int i = 0; i < size.getX() - 2; ++i) {
            for (int j = 0; j < size.getZ() - 2; ++j) {
                if (checkBlockNames(blockNames, world, offsetCorner.offset(i, 0, j))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<BlockPos> findCornersFixedSize(Block block, World world, BlockPos pos, BlockPos size, MultiBlockData data) {
        List<BlockPos> posList = new ArrayList<>();
        List<String> blockNames = new ArrayList<>(data.config.BLOCKKEYS.values());
        if (blockNames.stream().noneMatch(k -> k.equals(block.getRegistryName().toString()))) return posList;
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    BlockPos newPos = pos.offset(-x, -y, -z);
                    Block posBlock = world.getBlockState(newPos).getBlock();
                    Block diagonalOpposite = world.getBlockState(newPos.offset(size.getX() - 1, size.getY() - 1, size.getZ() - 1)).getBlock();
                    if (blockNames.stream().anyMatch(s -> s.equals(posBlock.getRegistryName().toString())) && blockNames.stream().anyMatch(s -> s.equals(diagonalOpposite.getRegistryName().toString()))) {
                        posList.add(pos.offset(-x, -y, -z));
                    }
                }
            }
        }
        return posList;
    }

    /**
     * Checks if the Block is actually in the List supplied
     *
     * @param blockNames List of valid Blocknames for the Step we want to Check
     * @param w          World
     * @param checkPos   The Pos to Check
     * @return True if the NONE of the Blocknames match the given Block
     */
    private static boolean checkBlockNames(List<String> blockNames, World w, BlockPos checkPos) {
        AtomicReference<Block> posBlock = new AtomicReference<>();
        //*Checks if the CurrentChunk is null
        if (currentChunk == null) {
            currentChunk = w.getChunkAt(checkPos);
            CHUNKS.add(currentChunk);
            posBlock.set(currentChunk.getBlockState(checkPos).getBlock());
            //*Checks if the currentChunk is the Chunk we currently Check in
        } else if (currentChunk.getPos().x == checkPos.getX() >> 4 && currentChunk.getPos().z == checkPos.getZ() >> 4) {
            posBlock.set(currentChunk.getBlockState(checkPos).getBlock());
            //*Checks if we have the Chunk Cached
        } else if (CHUNKS.parallelStream().anyMatch(k -> (k.getPos().x == checkPos.getX() >> 4) && k.getPos().z == checkPos.getZ() >> 4)) {
            CHUNKS.parallelStream().filter(k -> k.getPos().x == checkPos.getX() >> 4 && k.getPos().z == checkPos.getZ() >> 4).findFirst().ifPresent(chunk -> {
                currentChunk = chunk;
                posBlock.set(currentChunk.getBlockState(checkPos).getBlock());
            });
            //*We dont have the Chunk cached. Nor is the currentChunk null, so we need to get the Chunk from the World and save it.
        } else {
            currentChunk = (Chunk) w.getChunk(checkPos);
            CHUNKS.add(currentChunk);
            posBlock.set(currentChunk.getBlockState(checkPos).getBlock());
        }
        Block finalPosBlock = posBlock.get();
        //*If the Block we try to check here is null, that means it is unloaded, which is an illegal State
        if (finalPosBlock == null) {
            throw new IllegalStateException("Posblock is null.Expected a Block to never be null");
        }
        //*Return true if none of the Names match
        return blockNames.stream().noneMatch(s -> s.equals(finalPosBlock.getRegistryName().toString()));
    }
}
