package com.example.Util.SimpleJsonDataManager;

import com.example.Util.MultiBlocks.MultiBlockData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;

public class DataManager {
    public static final SimpleJsonDataManager<MultiBlockData> MULTI_BLOCK_DATA = new SimpleJsonDataManager<>("structure_plans", MultiBlockData.class);

    public static void onClientInit() {
        IResourceManager manager = Minecraft.getInstance().getResourceManager();
        if (manager instanceof IReloadableResourceManager) {
            IReloadableResourceManager reloader = (IReloadableResourceManager) manager;
            reloader.registerReloadListener(MULTI_BLOCK_DATA);
        }
    }
}
