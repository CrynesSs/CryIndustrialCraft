package com.example.Util.MultiBlocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StructureConfig {
    public final Map<String, String[]> BLOCKS = new HashMap<>();
    public Map<String, String> BLOCKKEYS = new HashMap<>();
    public final Map<Integer, String[][]> BLOCKS_BY_LAYER = new HashMap<>();


    public String getBlocknameByKey(String key) {
        return BLOCKKEYS.get(key);
    }

    @Override
    public String toString() {
        return "StructureConfig{" +
                "BLOCKS=" + BLOCKS.entrySet().toString() +
                ", BLOCKKEYS=" + BLOCKKEYS +
                ", BLOCKS_BY_LAYER=" + BLOCKS_BY_LAYER +
                '}';
    }

    public StructureConfig(JsonObject data) {
        this.setUpConfig(data);
    }

    public void setUpConfig(JsonObject data) {
        if (data.has("blockKeys")) {
            JsonElement blockKeys = data.get("blockKeys");
            if (blockKeys.isJsonObject()) {
                HashMap<String, String> tempMap = new HashMap<>();
                blockKeys.getAsJsonObject().entrySet().parallelStream().forEach(e -> {
                    if (BLOCKKEYS.keySet().parallelStream().anyMatch(k -> k.equals(e.getKey()))) {
                        throw new ValueException("Duplicate Block Key Value");
                    }
                    tempMap.put(e.getKey(), e.getValue().getAsString());
                });
                BLOCKKEYS = tempMap;
            } else {
                throw new JsonParseException("blockKeys is not a valid Json Object");
            }
        }
        if (data.has("blocks")) {
            JsonElement blocks = data.get("blocks");
            if (blocks.isJsonObject()) {
                setBLOCKS(blocks.getAsJsonObject());
            } else {
                throw new JsonParseException("blocks is not a valid Json Object");
            }
        }

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
