package com.example.Util.MultiBlocks.StructureChecks;

import com.example.MultiBlockStructure.AbstractMBStructure;
import com.example.MultiBlockStructure.MultiBlockSuper;
import com.example.Util.MultiBlocks.MultiBlockData;
import com.example.Util.MultiBlocks.StructureConfig;
import com.example.Util.MultiBlocks.StructureSave;
import com.example.Util.SimpleJsonDataManager.DataManager;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class StructureCheckMain {

    /**
     * Main method to check if a given placedBlock is generating a valid structure.
     *
     * @param w           The World the Block was placed in
     * @param pos         The Position the Block was placed in.
     * @param placedBlock The Placed {@link Block}
     * @return True if the block completes a valid structure
     */
    public static boolean checkStructure(ServerWorld w, BlockPos pos, Block placedBlock) {
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
                    if (computeIfValid(w, data, corner, size)) {
                        spawnStructure(w, data, corner, size);
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
                        cornersFixedSize.removeIf(corner -> !computeIfValid(w, data, corner, currentSize));
                        if (cornersFixedSize.isEmpty()) continue;
                        //*We found a corner, so we're spawning the structure
                        BlockPos corner = cornersFixedSize.get(0);
                        spawnStructure(w, data, corner, currentSize);
                        return true;
                    }
                    //*Not size-matching so any x-size can occur with any y or z size. So we need to check every possible combination
                } else {
                    for (int curXSize : data.xSizes) {
                        for (int curYSize : data.ySizes) {
                            for (int curZSize : data.zSizes) {
                                BlockPos currentSize = new BlockPos(curXSize, curYSize, curZSize);
                                //*Find the corners for the structure that might be valid
                                List<BlockPos> cornersFixedSize = findCornersFixedSize(placedBlock, w, pos, currentSize, data);
                                //*Remove the corner if it cannot be validated
                                cornersFixedSize.removeIf(corner -> !computeIfValid(w, data, corner, currentSize));
                                //*If there are no corners available, check another size
                                if (cornersFixedSize.isEmpty()) continue;
                                BlockPos corner = cornersFixedSize.get(0);
                                spawnStructure(w, data, corner, currentSize);
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
     * @param world  The {@link ServerWorld} this check is performed on
     * @param data   The {@link MultiBlockData} of the {@link AbstractMBStructure} that we want to check
     * @param corner The Corner {@link Block} of the Structure. This is the lowest value {@link BlockPos} of x,y,z in the {@link AbstractMBStructure}
     * @param size   The Size that is to Check
     * @return True if the Structure is valid
     */
    public static boolean computeIfValid(ServerWorld world, @Nonnull MultiBlockData data, @Nonnull BlockPos corner, @Nonnull BlockPos size) {
        BlockPos diagonalBlock = corner.offset(size.getX() - 1, 0, size.getZ() - 1);
        StructureConfig config = data.config;
        Map<ECalculationStep, List<Block>> stepToBlockMap = new HashMap<>();
        for (ECalculationStep step : ECalculationStep.values())
            stepToBlockMap.computeIfAbsent(step, config::getBlocknamesByStep);
        return StructureCheckMain.validateFixedSizeFrame(world, data, corner, size, stepToBlockMap.get(ECalculationStep.FRAME), diagonalBlock) &&
                StructureCheckMain.validateTopFixedSize(world, data, corner, size, stepToBlockMap.get(ECalculationStep.TOP), diagonalBlock) &&
                StructureCheckMain.validateBottomFixedSize(world, data, corner, size, stepToBlockMap.get(ECalculationStep.BOTTOM), diagonalBlock) &&
                StructureCheckMain.validateFacesFixedSize(world, data, corner, size, stepToBlockMap.get(ECalculationStep.SIDE), diagonalBlock) &&
                StructureCheckMain.validateInsideFixedSize(world, data, corner, size, stepToBlockMap.get(ECalculationStep.INSIDE), diagonalBlock);
    }

    /**
     * @param world The {@link ServerWorld} this check is performed on
     * @param data  The {@link MultiBlockData} of the {@link AbstractMBStructure} that we want to check
     * @param pos   The {@link BlockPos} the {@link AbstractMBStructure} shall be spawned at
     * @param size  The Size of the Structure we spawn in
     */
    public static void spawnStructure(ServerWorld world, @Nonnull MultiBlockData data, BlockPos pos, BlockPos size) {
        AbstractMBStructure structure;
        try {
            structure = (AbstractMBStructure) data.structure.newInstance();
            structure.setCorner(pos);
            structure.setSize(size);
            structure.setBlocksValid(world);
            structure.setData(data);
            if (structure.hasBlockEntity()) {
                world.setBlock(pos, world.getBlockState(pos).setValue(MultiBlockSuper.isCorner, true), 3);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new NullPointerException("Structure is Null");
        }
        StructureSave.putStructure(structure, world);
    }

    public static final HashSet<Chunk> CHUNKS = new HashSet<>();
    public static Chunk currentChunk = null;

    /**
     * @param world         The {@link ServerWorld} this check is performed on.
     * @param data          The {@link MultiBlockData} of the {@link AbstractMBStructure} that we want to check.
     * @param corner        The corner of the Structure.
     * @param size          The size of the Structure.
     * @param blockList     The List of all valid {@link Block} singletons for this {@link AbstractMBStructure}.
     * @param diagonalBlock The {@link BlockPos} diagonally opposite of the corner.
     * @return True if the Frame of the Structure is Valid.
     */
    public static boolean validateFixedSizeFrame(ServerWorld world, MultiBlockData data, BlockPos corner, @Nonnull BlockPos size, List<Block> blockList, BlockPos diagonalBlock) {
        //* Check for Bottom and Top Layer in x Direction (There will be overlap)
        for (int x = 0; x < size.getX(); ++x) {
            if (checkBlockNames(blockList, world, corner.offset(x, 0, 0))) return false;
            if (checkBlockNames(blockList, world, corner.offset(x, size.getY() - 1, 0))) return false;
            if (checkBlockNames(blockList, world, diagonalBlock.offset(-x, 0, 0))) return false;
            if (checkBlockNames(blockList, world, diagonalBlock.offset(-x, size.getY() - 1, 0))) return false;
        }
        //* Check for Bottom and Top Layer in Z Direction (There will be overlap)
        for (int z = 0; z < size.getZ(); ++z) {
            if (checkBlockNames(blockList, world, corner.offset(0, 0, z))) return false;
            if (checkBlockNames(blockList, world, corner.offset(0, size.getY() - 1, z))) return false;
            if (checkBlockNames(blockList, world, diagonalBlock.offset(0, 0, -z))) return false;
            if (checkBlockNames(blockList, world, diagonalBlock.offset(0, size.getY() - 1, -z))) return false;
        }
        //*Check the outer Frame for possible mismatches
        for (int y = 1; y < size.getY() - 2; ++y) {
            if (checkBlockNames(blockList, world, corner.offset(0, y, 0))) return false;
            if (checkBlockNames(blockList, world, diagonalBlock.offset(0, y, 0))) return false;
            if (checkBlockNames(blockList, world, corner.offset(size.getX() - 1, y, 0))) return false;
            if (checkBlockNames(blockList, world, corner.offset(0, y, size.getZ() - 1))) return false;
        }
        return true;

    }

    public static boolean validateFacesFixedSize(ServerWorld world, MultiBlockData data, BlockPos corner, @Nonnull BlockPos size, List<Block> blockNames, BlockPos diagonalBlock) {
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

    public static boolean validateInsideFixedSize(ServerWorld world, MultiBlockData data, BlockPos corner, BlockPos size, List<Block> blockNames, BlockPos diagonalBlock) {
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

    public static boolean validateTopFixedSize(ServerWorld world, MultiBlockData data, BlockPos corner, BlockPos size, List<Block> blockNames, BlockPos diagonalBlock) {
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


    public static boolean validateBottomFixedSize(ServerWorld world, MultiBlockData data, BlockPos corner, BlockPos size, List<Block> blockNames, BlockPos diagonalBlock) {
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

    public static List<BlockPos> findCornersFixedSize(Block block, ServerWorld world, BlockPos pos, BlockPos size, MultiBlockData data) {
        List<BlockPos> posList = new ArrayList<>();
        List<Block> blockList = data.config.getBlockNamesAsBlocks();
        if (blockList.stream().noneMatch(block::is)) return posList;
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    BlockPos newPos = pos.offset(-x, -y, -z);
                    Block posBlock = world.getBlockState(newPos).getBlock();
                    Block diagonalOpposite = world.getBlockState(newPos.offset(size.getX() - 1, size.getY() - 1, size.getZ() - 1)).getBlock();
                    if (blockList.stream().anyMatch(posBlock::is) && blockList.stream().anyMatch(diagonalOpposite::is))
                        posList.add(pos.offset(-x, -y, -z));
                }
            }
        }
        return posList;
    }

    /**
     * Checks if the {@link Block} at the {@link BlockPos} is actually in the List supplied
     *
     * @param blockNames List of valid Blocknames for the Step we want to Check
     * @param serverWorld The {@link ServerWorld} this check is performed
     * @param checkPos   The {@link BlockPos} we want to check
     * @return True if NONE of the Blocknames match the given {@link Block}
     */
    private static boolean checkBlockNames(List<Block> blockNames, ServerWorld serverWorld, BlockPos checkPos) {
        Block posBlock;
        //*Checks if the CurrentChunk is null, and if it is, sets a new one
        if (currentChunk == null) {
            currentChunk = serverWorld.getChunkAt(checkPos);
            CHUNKS.add(currentChunk);
            //*Checks if the currentChunk is the Chunk we currently Check in
        } else if (currentChunk.getPos().x == checkPos.getX() >> 4 && currentChunk.getPos().z == checkPos.getZ() >> 4) {
            posBlock = currentChunk.getBlockState(checkPos).getBlock();
            return blockNames.stream().noneMatch(posBlock::is);
            //*Checks if we have the Chunk Cached
        } else if (CHUNKS.parallelStream().anyMatch(k -> (k.getPos().x == checkPos.getX() >> 4) && k.getPos().z == checkPos.getZ() >> 4)) {
            CHUNKS.parallelStream().filter(k -> k.getPos().x == checkPos.getX() >> 4 && k.getPos().z == checkPos.getZ() >> 4).findFirst().ifPresent(chunk -> currentChunk = chunk);
            //*We dont have the Chunk cached. Nor is the currentChunk null, so we need to get the Chunk from the World and save it.
        } else {
            currentChunk = (Chunk) serverWorld.getChunk(checkPos);
            CHUNKS.add(currentChunk);
        }
        posBlock = currentChunk.getBlockState(checkPos).getBlock();
        //*If the Block we try to check here is null, that means it is unloaded, which is an illegal State
        return blockNames.stream().noneMatch(posBlock::is);
    }

    public enum ECalculationStep {
        TOP("top"),
        BOTTOM("bottom"),
        FRAME("frame"),
        SIDE("face"),
        INSIDE("inside");
        public final String name;

        ECalculationStep(String name) {
            this.name = name;
        }
    }
}
