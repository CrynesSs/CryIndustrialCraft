package com.example.EventHandles;

import com.example.Inits.ContainerTypes;
import com.example.Machines.BoneMealer.BoneMealerScreen;
import com.example.Machines.Planter.PlanterScreen;
import com.example.Machines.WoodCutter.WoodCutterScreen;
import com.example.examplemod.CryIndustry;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CryIndustry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void doClientStuff(final FMLClientSetupEvent event) {
        bindScreens();
    }

    private static void bindScreens() {
        ScreenManager.register(ContainerTypes.PLANTER_CONTAINER.get(), PlanterScreen::new);
        ScreenManager.register(ContainerTypes.WOODCUTTER_CONTAINER.get(), WoodCutterScreen::new);
        ScreenManager.register(ContainerTypes.BONEMEALER_CONTAINER.get(), BoneMealerScreen::new);
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            return;
        }
        event.addSprite(new ResourceLocation(CryIndustry.MOD_ID, "block/double_slab"));
    }
}
