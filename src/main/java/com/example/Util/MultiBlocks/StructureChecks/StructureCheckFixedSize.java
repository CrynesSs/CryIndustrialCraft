package com.example.Util.MultiBlocks.StructureChecks;

import com.example.MultiBlockStructure.AbstractMBStructure;
import com.example.Util.MultiBlocks.MultiBlockData;
import com.example.Util.MultiBlocks.MultiThread.MbCheckTask;
import com.example.Util.MultiBlocks.StructureSave;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class StructureCheckFixedSize {

    public static final HashSet<Chunk> CHUNKS = new HashSet<>();
    public static Chunk currentChunk = null;

    public static void checkBlocksFixedSize(Block block, World world, BlockPos pos, MultiBlockData data) {
        //List of all possible Corners, that this fixed size Structure could have(in the End only one will be Valid tho)
        List<BlockPos> posList = findCornersFixedSize(block, world, pos, data);
        //Define the Size of the Structure (in this Case there is only one possibility, making those checks insanely fast, even in the Worst Case scenario
        BlockPos size = new BlockPos(data.xSizes[0], data.ySizes[0], data.zSizes[0]);
        //removes every Pos that does not validate
        if (posList.size() == 1) {
            spawnStructure(world, posList.stream().findFirst().get(), data, size);
        } else {
            System.out.println("Someting went terrbily wrong");
        }
    }

    public static boolean validateFixedSize(BlockPos corner, World world, MultiBlockData data, BlockPos size) {
        List<String> blockNamesFrame = Arrays.stream(data.config.BLOCKS.get("frame")).map(k -> data.config.getBlocknameByKey(k)).collect(Collectors.toList());
        BlockPos diagonalBlock = corner.offset(size.getX() - 1, 0, size.getZ() - 1);
        if (data.isHollow) {
            //*Checking the Frame of the Structure
            if (!validateFixedSizeFrame(corner, world, data, size, blockNamesFrame, diagonalBlock)) {
                return false;
            }
            List<String> blockNamesFaces = Arrays.stream(data.config.BLOCKS.get("face")).map(k -> data.config.getBlocknameByKey(k)).collect(Collectors.toList());
            if (!validateFacesFixedSize(corner, world, data, size, blockNamesFaces, diagonalBlock)) {
                return false;
            }
            List<String> blockNamesTop = Arrays.stream(data.config.BLOCKS.get("top")).map(k -> data.config.getBlocknameByKey(k)).collect(Collectors.toList());
            if (!validateTopFixedSize(corner, world, data, size, blockNamesTop, diagonalBlock)) {
                return false;
            }
            List<String> blockNamesBottom = Arrays.stream(data.config.BLOCKS.get("bottom")).map(k -> data.config.getBlocknameByKey(k)).collect(Collectors.toList());
            return validateBottomFixedSize(corner, world, data, size, blockNamesBottom, diagonalBlock);
        }
        return true;
    }

    public static boolean validateFixedSizeFrame(BlockPos corner, World world, MultiBlockData data, BlockPos size, List<String> blockNames, BlockPos diagonalBlock) {
        for (int k = 0; k < size.getY(); k++) {
            for (int i = 0, z = 0; i < size.getX() && z < size.getZ(); i++, z++) {
                //*Checking the Bottom Frame of the Structure
                if (k == 0 || k == size.getY() - 1) {
                    if (checkBlockNames(blockNames, world, corner.offset(i, k, 0)) && i < size.getX() ||
                            checkBlockNames(blockNames, world, corner.offset(0, k, z)) && z < size.getZ() ||
                            checkBlockNames(blockNames, world, diagonalBlock.offset(-i, k, 0)) && i < size.getX() ||
                            checkBlockNames(blockNames, world, diagonalBlock.offset(0, k, -z)) && z < size.getZ()) {
                        return false;
                    }
                    //*Here we check the middle layer. This layer only consists of 4 Frame Blocks
                } else {
                    if (checkBlockNames(blockNames, world, corner.offset(0, k, 0)) ||
                            checkBlockNames(blockNames, world, corner.offset(size.getX() - 1, k, 0)) ||
                            checkBlockNames(blockNames, world, diagonalBlock.offset(0, k, 0)) ||
                            checkBlockNames(blockNames, world, diagonalBlock.offset(0, k, -size.getZ() + 1))
                    ) {
                        return false;
                    }
                    break;
                }
            }
        }
        return true;
    }

    //this should also work for rectangular structures now with the new Checks in Place.Doing this Part by Part reduces time spent with useless calculations
    public static boolean validateFacesFixedSize(BlockPos corner, World world, MultiBlockData data, BlockPos size, List<String> blockNames, BlockPos diagonalBlock) {
        for (int i = 1, j = 1; i < size.getX() - 1 && j < size.getZ() - 1; i++, j++) {
            for (int k = 1; k < size.getY() - 1; k++) {
                if (checkBlockNames(blockNames, world, corner.offset(i, k, 0)) && i < size.getX() - 1 ||
                        checkBlockNames(blockNames, world, corner.offset(0, k, j)) && j < size.getZ() - 1 ||
                        checkBlockNames(blockNames, world, diagonalBlock.offset(-i, k, 0)) && i < size.getX() - 1 ||
                        checkBlockNames(blockNames, world, diagonalBlock.offset(0, k, -j)) && j < size.getZ() - 1) {
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

    public static List<BlockPos> findCornersFixedSize(Block block, World world, BlockPos pos, MultiBlockData data) {
        List<BlockPos> posList = new ArrayList<>();
        List<String> blocknames = Arrays.asList(data.config.BLOCKS.get("frame")).parallelStream().map(k -> data.config.getBlocknameByKey(k)).collect(Collectors.toList());
        for (int i = 0; i < data.xSizes[0]; i++) {
            for (int k = 0; k < data.ySizes[0]; k++) {
                for (int j = 0; j < data.zSizes[0]; j++) {
                    BlockPos newPos = pos.offset(-i, -k, -j);
                    Block posBlock = world.getBlockState(newPos).getBlock();
                    Block diagonalOpposite = world.getBlockState(newPos.offset(data.xSizes[0] - 1, data.ySizes[0] - 1, data.zSizes[0] - 1)).getBlock();
                    if (blocknames.stream().anyMatch(s -> s.equals(Objects.requireNonNull(posBlock.getRegistryName()).toString())) && blocknames.stream().anyMatch(s -> s.equals(diagonalOpposite.getRegistryName().toString()))) {
                        posList.add(pos.offset(-i, -k, -j));
                    }
                }
            }
        }
        return posList;
    }

    /**
     * Checks if the Block is actually in the List supplied
     *
     * @param blockNames List of Blocknames
     * @param w          World
     * @param checkPos   The Pos to Check
     * @return
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
