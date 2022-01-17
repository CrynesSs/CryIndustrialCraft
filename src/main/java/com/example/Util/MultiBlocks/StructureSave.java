package com.example.Util.MultiBlocks;

import com.example.MultiBlockStructure.AbstractMBStructure;
import com.example.examplemod.CryIndustry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class StructureSave extends WorldSavedData {

    public final List<AbstractMBStructure> STRUCTURES = new ArrayList<>();
    public static final String NAME = CryIndustry.MOD_ID + "_structures";

    public StructureSave() {
        super(NAME);
    }

    public static void putStructure(AbstractMBStructure structure, ServerWorld world) {
        StructureSave save = world.getDataStorage().computeIfAbsent(StructureSave::new, NAME);
        save.STRUCTURES.add(structure);
        save.setDirty();
    }

    public static StructureSave getData(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(StructureSave::new, NAME);
    }

    @Override
    public void load(CompoundNBT nbt) {
        int i = 0;
        while (nbt.contains("s" + i)) {
            STRUCTURES.add(AbstractMBStructure.deserialize(nbt.getCompound("s" + i)));
            ++i;
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        for (ListIterator<AbstractMBStructure> listIterator = STRUCTURES.listIterator(); listIterator.hasNext(); ) {
            compound.put("s" + listIterator.nextIndex(), listIterator.next().serialize());
        }
        return compound;
    }
}
