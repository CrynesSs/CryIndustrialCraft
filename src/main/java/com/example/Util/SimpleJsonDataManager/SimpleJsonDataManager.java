package com.example.Util.SimpleJsonDataManager;

import com.example.Util.MultiBlocks.MultiBlockData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleJsonDataManager<T> extends JsonReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public Map<ResourceLocation, JsonObject> objects = new HashMap<>();

    /**
     * The raw data that we parsed from json last time resources were reloaded
     **/
    protected Map<ResourceLocation, T> data = new HashMap<>();

    private final Class<T> dataClass;

    /**
     * @param folder This is the name of the folders that the resource loader looks in, e.g. assets/modid/FOLDER
     */
    public SimpleJsonDataManager(String folder, Class<T> dataClass) {
        super(GSON, folder);
        this.dataClass = dataClass;
    }

    /**
     * Get the data object represented by the json at the given resource location
     **/
    public T getData(ResourceLocation id) {
        return this.data.get(id);
    }

    /**
     * Use a json object (presumably one from an assets/modid/mondobooks folder) to generate a data object
     **/
    protected T getJsonAsData(JsonObject json) {
        return GSON.fromJson(json, this.dataClass);
    }

    public Map<ResourceLocation, T> getAllData() {
        return this.data;
    }

    /**
     * Converts all the values in a map to new values; the new map uses the same keys as the old map
     **/

    public static <Key, In, Out> Map<Key, Out> mapValues(Map<Key, In> inputs, Function<In, Out> mapper) {
        Map<Key, Out> newMap = new HashMap<>();

        inputs.forEach((key, input) -> newMap.put(key, mapper.apply(input)));

        return newMap;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, @Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn) {
        Map<ResourceLocation, JsonObject> objectMap = new HashMap<>();
        jsons.entrySet().parallelStream().forEach(k -> {
            if (k.getValue().isJsonObject()) {
                objectMap.put(k.getKey(), k.getValue().getAsJsonObject());
            }
        });
        this.objects = objectMap;
        this.data = SimpleJsonDataManager.mapValues(objectMap, this::getJsonAsData);
        if (this.dataClass.equals(MultiBlockData.class)) {
            this.data.forEach((k, v) -> {
                if (jsons.get(k).isJsonObject()) {
                    MultiBlockData.setStructureConfig((MultiBlockData) v, jsons.get(k).getAsJsonObject().getAsJsonObject("config"));
                }
            });
        }

    }
}
