package com.example.Util.MultiBlocks.StructureChecks;

import com.example.Util.MultiBlocks.MultiBlockData;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;
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
        posList.removeIf(k -> !validateFixedSize(k, world, data, size));
        if (true) {
            System.out.println("LOL");
        }
        if (posList.size() == 0) {
            return;
        } else if (posList.size() == 1) {
            StructureCheck.spawnStructure(world, posList.stream().findFirst().get(), data, size);
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
        if (currentChunk == null) {
            currentChunk = (Chunk) w.getChunk(checkPos);
            CHUNKS.add(currentChunk);
            Block posBlock = currentChunk.getBlockState(checkPos).getBlock();
            return blockNames.stream().noneMatch(s -> s.equals(posBlock.getRegistryName().toString()));
        }
        if (currentChunk.getPos().x == checkPos.getX() >> 4 && currentChunk.getPos().z == checkPos.getZ() >> 4) {
            Block posBlock = currentChunk.getBlockState(checkPos).getBlock();
            return blockNames.stream().noneMatch(s -> s.equals(posBlock.getRegistryName().toString()));
        } else if (!CHUNKS.parallelStream().noneMatch(k -> (k.getPos().x == checkPos.getX() >> 4) && k.getPos().z == checkPos.getZ() >> 4)) {
            currentChunk = CHUNKS.parallelStream().filter(k -> k.getPos().x == checkPos.getX() >> 4 && k.getPos().z == checkPos.getZ() >> 4).findFirst().get();
            Block posBlock = currentChunk.getBlockState(checkPos).getBlock();
            return blockNames.stream().noneMatch(s -> s.equals(posBlock.getRegistryName().toString()));
        } else {
            currentChunk = (Chunk) w.getChunk(checkPos);
            CHUNKS.add(currentChunk);
            Block posBlock = currentChunk.getBlockState(checkPos).getBlock();
            return blockNames.stream().noneMatch(s -> s.equals(posBlock.getRegistryName().toString()));
        }
    }
}
