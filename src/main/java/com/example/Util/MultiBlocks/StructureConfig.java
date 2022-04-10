package com.example.Util.MultiBlocks;

import com.example.Util.MultiBlocks.StructureChecks.StructureCheckMain;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StructureConfig {

    public final Map<StructureCheckMain.ECalculationStep, List<String>> BLOCKS = new ConcurrentHashMap<>();
    public Map<String, String> BLOCKKEYS = new ConcurrentHashMap<>();
    public final Map<String, List<String>> BLOCKGROUPS = new ConcurrentHashMap<>();
    public final Map<Integer, String[][]> BLOCKS_BY_LAYER = new ConcurrentHashMap<>();

    public static final Gson gson = new Gson();

    public String getBlocknameByKey(String key) {
        if (key.equals("A")) {
            return "minecraft:air";
        }
        return BLOCKKEYS.get(key);
    }

    /**
     *
     * @param step The calculatingstep we are currently on
     * @return A List containing all valid {@link Block} for the Step
     */
    public List<Block> getBlocknamesByStep(StructureCheckMain.ECalculationStep step){
        return Stream.concat(BLOCKS.get(step).stream().filter(BLOCKGROUPS::containsKey).map(BLOCKGROUPS::get).flatMap(List::stream),BLOCKS.get(step).stream().filter(s -> !BLOCKGROUPS.containsKey(s))).distinct().map(this::getBlocknameByKey).map((s)->ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s))).collect(Collectors.toList());
    }
    public List<String> getBlockKeys(String blockGroupName){
        return BLOCKGROUPS.get(blockGroupName);
    }
    public List<Block> getBlockNamesAsBlocks(){
        return BLOCKKEYS.values().parallelStream().map(s -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s))).collect(Collectors.toList());
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
        if(data.has("states")){
            //TODO implement States
        }
    }

    private void setBLOCKS_BY_LAYER(JsonObject layers) {
        layers.entrySet().parallelStream().forEach(entry -> BLOCKS_BY_LAYER.computeIfAbsent(Integer.parseInt(entry.getKey()), (key) -> gson.fromJson(entry.getValue(), String[][].class)));
    }

    private void setBLOCKGROUPS(JsonObject blockgroups) {
        blockgroups.entrySet().parallelStream().forEach(element -> BLOCKGROUPS.computeIfAbsent(element.getKey(), key -> ImmutableList.copyOf(gson.fromJson(element.getValue(), String[].class))));
    }

    private void setBLOCKKEYS(JsonObject blockKeys) {
        blockKeys.entrySet().parallelStream().forEach(element -> BLOCKKEYS.computeIfAbsent(element.getKey(), key -> gson.fromJson(element.getValue(), String.class)));
    }


    private void setBLOCKS(JsonObject blockObject) {
        blockObject.entrySet().parallelStream().forEach(entry -> BLOCKS.put(StructureCheckMain.ECalculationStep.valueOf(entry.getKey().toUpperCase(Locale.ROOT)), ImmutableList.copyOf(gson.fromJson(entry.getValue(), String[].class))));
        if (!blockObject.has("inside")) {
            BLOCKS.put(StructureCheckMain.ECalculationStep.INSIDE, ImmutableList.of("A"));
        }
    }
}
