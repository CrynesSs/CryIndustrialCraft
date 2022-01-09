package com.example.Util.MultiBlocks;

import com.example.MultiBlockStructure.AbstractMBStructure;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.List;

public class StructureSave extends WorldSavedData {

    public static final List<AbstractMBStructure> STRUCTURES = new ArrayList<>();
    public StructureSave(String name) {
        super(name);
    }

    @Override
    public void load(CompoundNBT nbt) {

    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {

        return null;
    }
}
