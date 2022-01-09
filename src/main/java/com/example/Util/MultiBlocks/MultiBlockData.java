package com.example.Util.MultiBlocks;

import com.example.MultiBlockStructure.Reactor;
import com.google.gson.JsonObject;

import java.util.Arrays;

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
    public String structureName;
    public String controllerName;
    public transient StructureConfig config;
    public transient Class<?> structure;

    public static void setStructureConfig(MultiBlockData dataObject, JsonObject configObject) {
        dataObject.config = new StructureConfig(configObject);
        try {
            dataObject.structure = Class.forName(dataObject.structureName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (dataObject.structure != null) {
            return;
        }
        switch (dataObject.structureName) {
            case "reactor": {
                dataObject.structure = Reactor.class;
            }
        }
        System.out.println(dataObject.toString());
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
