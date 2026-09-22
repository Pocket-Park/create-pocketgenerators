package com.pocketpark.pocketgenerators;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.pocketpark.pocketgenerators.block.ResourceGeneratorBlockEntity;
import com.pocketpark.pocketgenerators.registry.ModBlockEntities;
import com.pocketpark.pocketgenerators.registry.ModBlocks;
import com.pocketpark.pocketgenerators.registry.ModItems;
import com.pocketpark.pocketgenerators.registry.ModRecipeSerializers;
import com.pocketpark.pocketgenerators.registry.ModRecipeTypes;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(PocketGenerators.MODID)
public class PocketGenerators {

    public static final String MODID = "pocketgenerators";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("pocket_generators",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pocketgenerators"))
                    .withTabsBefore(CreativeModeTabs.REDSTONE_BLOCKS)
                    .icon(() -> ModItems.RESOURCE_GENERATOR_BASIC.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.RESOURCE_GENERATOR_BASIC.get());
                        output.accept(ModItems.RESOURCE_GENERATOR_ANDESITE.get());
                        output.accept(ModItems.RESOURCE_GENERATOR_BRASS.get());
                        output.accept(ModItems.RESOURCE_GENERATOR_NETHERITE.get());
                        output.accept(ModItems.RESOURCE_GENERATOR_END.get());
                    })
                    .build());

    public PocketGenerators(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(ResourceGeneratorBlockEntity::registerCapabilities);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
