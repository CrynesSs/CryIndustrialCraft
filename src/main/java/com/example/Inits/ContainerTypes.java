package com.example.Inits;

import com.example.Machines.BoneMealer.BoneMealerContainer;
import com.example.Machines.Planter.PlanterContainer;
import com.example.Machines.WoodCutter.WoodCutterContainer;
import com.example.MultiBlockStructure.ReactorStructure.ReactorContainer;
import com.example.examplemod.CryIndustry;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.launch.GlobalProperties;

public class ContainerTypes {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CryIndustry.MOD_ID);
    public static final RegistryObject<ContainerType<PlanterContainer>> PLANTER_CONTAINER = CONTAINER_TYPES.register("planter",
            () -> IForgeContainerType.create(PlanterContainer::getClientContainer));
    public static final RegistryObject<ContainerType<WoodCutterContainer>> WOODCUTTER_CONTAINER = CONTAINER_TYPES.register("woodcutter",
            () -> IForgeContainerType.create(WoodCutterContainer::getClientContainer));

    public static final RegistryObject<ContainerType<BoneMealerContainer>> BONEMEALER_CONTAINER = CONTAINER_TYPES.register("bonemealer",
            () -> IForgeContainerType.create(BoneMealerContainer::getClientContainer));
    public static final RegistryObject<ContainerType<ReactorContainer>> REACTOR_CONTAINER = CONTAINER_TYPES.register("reactor",
            () -> IForgeContainerType.create(ReactorContainer::getClientContainer));
}
