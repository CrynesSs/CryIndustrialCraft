package com.example.Cables;

import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CableNetwork {
    public final Set<AbstractCable> CABLES = new HashSet<>();
    public final Set<IEnergyStorage> CONSUMERS = new HashSet<>();
    public final Set<IEnergyStorage> PRODUCERS = new HashSet<>();

    public void tick() {

    }
    public int canReceiveEnergy(int energyToReceive) {

    }
    public int receiveEnergy(int energyToReceive) {

    }
    public void distributeEnergy(HashMap<IEnergyStorage, Integer> amounts) {

    }
    public boolean canReceive() {
        return CONSUMERS.parallelStream().anyMatch(IEnergyStorage::canReceive);
    }
}
