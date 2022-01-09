package com.example.Cables;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class MachineEnergyTE extends TileEntity {

    public final Set<Direction> MACHINEDIRECTION;

    private final LazyOptional<IEnergyStorage> ENERGYSTORAGE = LazyOptional.of(this::createEnergyStorage);

    private CableNetwork network;

    public MachineEnergyTE() {
        super(type);
        MACHINEDIRECTION = null;
    }

    public MachineEnergyTE(Set<Direction> directions) {
        super(type);
        MACHINEDIRECTION = directions;
    }

    @Nonnull
    private IEnergyStorage createEnergyStorage() {
        return new EnergyStorage(0) {
            @Override
            public boolean canReceive() {
                return network != null && network.canReceive();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if (simulate) {
                    return network.canReceiveEnergy(maxReceive);
                }
                if (network.canReceiveEnergy(maxReceive) != maxReceive) {
                    return network.receiveEnergy(maxReceive);
                }
                return maxReceive;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return ENERGYSTORAGE.cast();
        }
        return super.getCapability(cap, side);
    }
}
