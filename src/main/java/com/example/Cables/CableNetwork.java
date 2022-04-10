package com.example.Cables;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CableNetwork {
    public final Set<AbstractCable> CABLES = new HashSet<>();
    public final Set<EnergyStorage> CONSUMERS = new HashSet<>();
    public final Set<EnergyStorage> PRODUCERS = new HashSet<>();

    public void tick() {
        ConcurrentHashMap<EnergyStorage,Integer> toProduceMap = new ConcurrentHashMap<>(PRODUCERS.parallelStream().collect(Collectors.toMap(energyStorage -> energyStorage,energyStorage -> energyStorage.extractEnergy(Integer.MAX_VALUE,true))));
        int sumToProduce = toProduceMap.reduceEntriesToInt(1000,(Map.Entry::getValue),0,((integer, integer2) -> integer+=integer2));
        ConcurrentHashMap<EnergyStorage,Integer> toReceiveMap = new ConcurrentHashMap<>(CONSUMERS.parallelStream().collect(Collectors.toMap(energyStorage -> energyStorage,energyStorage -> energyStorage.receiveEnergy(sumToProduce,true))));
        int sumToReceive = toReceiveMap.reduceEntriesToInt(1000,(Map.Entry::getValue),0,((integer, integer2) -> integer+=integer2));






    }

    public int canReceiveEnergy(int energyToReceive) {
        //TODO IMPLEMENT
        return 0;
    }

    public int receiveEnergy(int energyToReceive) {
        //TODO IMPLEMENT
        return 0;
    }

    public void distributeEnergy(HashMap<IEnergyStorage, Integer> amounts) {

    }

    public static CableNetwork retrieveCableNetworkFromPos(BlockPos pos){


        return null;
    }
    public boolean canReceive() {
        return CONSUMERS.parallelStream().anyMatch(IEnergyStorage::canReceive);
    }

    public void merge(CableNetwork network){
        this.CABLES.addAll(network.CABLES);
        this.CONSUMERS.addAll(network.CONSUMERS);
        this.PRODUCERS.addAll(network.PRODUCERS);
    }
    public static final Merger MERGE = new Merger();

    public static class Merger implements BinaryOperator<CableNetwork>{
        public Merger(){}
        @Override
        public CableNetwork apply(CableNetwork cableNetwork, CableNetwork cableNetwork2) {
            cableNetwork.merge(cableNetwork2);
            return cableNetwork;
        }
    }
}
