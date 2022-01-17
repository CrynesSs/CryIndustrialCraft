package com.example.Util.MultiBlocks;

import com.google.gson.*;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StructureConfig {

    public final Map<String, String[]> BLOCKS = new HashMap<>();
    public Map<String, String> BLOCKKEYS = new HashMap<>();
    public final Map<String, String[]> BLOCKGROUPS = new HashMap<>();
    public final Map<Integer, String[][]> BLOCKS_BY_LAYER = new HashMap<>();

    public static final Gson gson = new Gson();

    public String getBlocknameByKey(String key) {
        return BLOCKKEYS.get(key);
    }

    @Override
    public String toString() {
        return "StructureConfig{" +
                "BLOCKS=" + BLOCKS.entrySet() +
                ", BLOCKKEYS =" + BLOCKKEYS +
                ", BLOCKS_BY_LAYER =" + BLOCKS_BY_LAYER +
                '}';
    }

    public StructureConfig(JsonObject data) {
        this.setUpConfig(data);
    }

    public void setUpConfig(JsonObject data) {
        //*If there is a field blockKeys in our Data we want to serialize that into a usable Object
        if (data.has("blockKeys")) {
            JsonElement blockKeys = data.get("blockKeys");
            //*If the field is not a valid jsonObject we want to immediately throw an exception to warn the user, that their data is invalid
            if (!blockKeys.isJsonObject())
                throw new JsonParseException("blockKeys is not a valid Json Object");
            //*Separate Method to serialize, as it improves readability by a mile
            setBLOCKKEYS(data.getAsJsonObject("blockKeys"));
        }
        //*If there is a field blocks in our Data we want to serialize that into a usable Object
        if (data.has("blocks")) {
            //*If the field is not a valid jsonObject we want to immediately throw an exception to warn the user, that their data is invalid
            if (!data.get("blocks").isJsonObject())
                throw new JsonParseException("blocks is not a valid Json Object");
            //*Separate Method to serialize, as it improves readability by a mile
            setBLOCKS(data.get("blocks").getAsJsonObject());
        }
        if (data.has("layers")) {
            //TODO IMPLEMENT LAYERS
            //*If the field is not a valid jsonObject we want to immediately throw an exception to warn the user, that their data is invalid
            if (!data.get("layers").isJsonObject()) throw new JsonParseException("layers is not a valid Json Object");
            //*Separate Method to serialize, as it improves readability by a mile
            setBLOCKS_BY_LAYER(data.getAsJsonObject("layers"));

        }
        if (data.has("blockGroups")) {
            //*If the field is not a valid jsonObject we want to immediately throw an exception to warn the user, that their data is invalid
            if (!data.get("blockGroups").isJsonObject())
                throw new JsonParseException("blockGroups is not a valid Json Object");
            //*Separate Method to serialize, as it improves readability by a mile
            setBLOCKGROUPS(data.getAsJsonObject("blockGroups"));
        }
    }

    private void setBLOCKS_BY_LAYER(JsonObject layers) {
        layers.entrySet().forEach(entry -> {
            if(BLOCKS_BY_LAYER.keySet().parallelStream().anyMatch(k->k.equals(Integer.parseInt(entry.getKey())))){
                throw new JsonParseException("Duplicate Key in Layers");
            }
            BLOCKS_BY_LAYER.put(Integer.parseInt(entry.getKey()), gson.fromJson(entry.getValue(), String[][].class));
        });
    }

    private void setBLOCKGROUPS(JsonObject blockgroups) {
        blockgroups.entrySet().parallelStream().forEach(element -> {
            if (BLOCKGROUPS.keySet().parallelStream().anyMatch(key -> key.equals(element.getKey()))) {
                throw new ValueException("Duplicate Key Value in BLOCKGROUPS");
            }
            if (!element.getValue().isJsonArray())
                throw new JsonParseException("Invalid Array found while parsing BLOCKGROUPS");
            String[] BLOCKKEYS = gson.fromJson(element.getValue(), String[].class);
            BLOCKGROUPS.put(element.getKey(), BLOCKKEYS);
        });
    }

    private void setBLOCKKEYS(JsonObject blockKeys) {
        HashMap<String, String> tempMap = new HashMap<>();
        blockKeys.getAsJsonObject().entrySet().parallelStream().forEach(e -> {
            if (BLOCKKEYS.keySet().parallelStream().anyMatch(k -> k.equals(e.getKey()))) {
                throw new ValueException("Duplicate Block Key Value");
            }
            tempMap.put(e.getKey(), e.getValue().getAsString());
        });
        BLOCKKEYS = tempMap;
    }


    private void setBLOCKS(JsonObject blockObject) {
        if (blockObject.has("frame")) {
            JsonArray arr = blockObject.getAsJsonArray("frame");
            String[] strings = new String[arr.size()];
            AtomicInteger i = new AtomicInteger(0);
            arr.forEach(k -> {
                strings[i.get()] = k.getAsString();
                i.getAndIncrement();
            });
            BLOCKS.put("frame", strings);
        }
        if (blockObject.has("top")) {
            JsonArray arr = blockObject.getAsJsonArray("top");
            String[] strings = new String[arr.size()];
            AtomicInteger i = new AtomicInteger(0);
            arr.forEach(k -> {
                strings[i.get()] = k.getAsString();
                i.getAndIncrement();
            });
            BLOCKS.put("top", strings);
        }
        if (blockObject.has("bottom")) {
            JsonArray arr = blockObject.getAsJsonArray("bottom");
            String[] strings = new String[arr.size()];
            AtomicInteger i = new AtomicInteger(0);
            arr.forEach(k -> {
                strings[i.get()] = k.getAsString();
                i.getAndIncrement();
            });
            BLOCKS.put("bottom", strings);
        }
        if (blockObject.has("face")) {
            JsonArray arr = blockObject.getAsJsonArray("face");
            String[] strings = new String[arr.size()];
            AtomicInteger i = new AtomicInteger(0);
            arr.forEach(k -> {
                strings[i.get()] = k.getAsString();
                i.getAndIncrement();
            });
            BLOCKS.put("face", strings);
        }
    }
}
