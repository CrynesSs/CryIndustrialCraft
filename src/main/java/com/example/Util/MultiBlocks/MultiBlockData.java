package com.example.Util.MultiBlocks;

import com.example.MultiBlockStructure.ReactorStructure.Reactor;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Map;

public class MultiBlockData {
    public int[] xSizes;
    public int[] ySizes;
    public int[] zSizes;
    public int controllerAmount;
    public boolean sizeMatching;
    public boolean isHollow;
    public boolean isFixedSize;
    public boolean controllerNeeded;
    public boolean controllerFixed;
    public boolean isRegular;
    public String structureClassName;
    public String structureName;
    public String controllerName;
    public transient StructureConfig config;
    public transient Class<?> structure;

    /**
     * @param dataObject   The Multiblock Data Object
     * @param configObject The JSON used to generate the above Object
     */
    public static void setStructureConfig(MultiBlockData dataObject, JsonObject configObject) {
        dataObject.config = new StructureConfig(configObject);
        if(dataObject.structureClassName != null){
            try {
                dataObject.structure = Class.forName(dataObject.structureClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (dataObject.structure != null) {
            return;
        }
        switch (dataObject.structureName) {
            case "reactor": {
                dataObject.structure = Reactor.class;
                break;
            }
            case "fission_reactor" : {

            }
        }
        System.out.println(dataObject);
    }

    @Override
    public String toString() {
        return "MultiBlockData{" +
                "xSizes=" + Arrays.toString(xSizes) +
                ", ySizes=" + Arrays.toString(ySizes) +
                ", zSizes=" + Arrays.toString(zSizes) +
                ", controllerAmount=" + controllerAmount +
                ", sizeMatching=" + sizeMatching +
                ", isHollow=" + isHollow +
                ", isFixedSize=" + isFixedSize +
                ", controllerNeeded=" + controllerNeeded +
                ", controllerFixed=" + controllerFixed +
                ", structureName='" + structureName + '\'' +
                ", controllerName='" + controllerName + '\'' +
                ", config=" + config.toString() +
                '}';
    }

}
