package com.example.ReactorCraft.TileEntities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nonnull;

public class ReactorEnergyOutputTE extends TileEntity {
    LazyOptional<EnergyStorage> energyStorage = LazyOptional.of(this::createStorage);

    public ReactorEnergyOutputTE(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    @Nonnull
    private EnergyStorage createStorage() {
        return new EnergyStorage(7000, 2000);
    }
}
