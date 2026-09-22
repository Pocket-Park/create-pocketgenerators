package com.pocketpark.pocketgenerators;

import com.pocketpark.pocketgenerators.block.ResourceGeneratorBlockEntity;
import com.pocketpark.pocketgenerators.client.ResourceGeneratorRenderer;
import com.pocketpark.pocketgenerators.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.ShaftVisual;

import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * bus = MOD is required here: {@link EventBusSubscriber}'s default is the GAME bus, which never
 * receives mod-lifecycle events like {@link FMLClientSetupEvent} — without it this class's listener
 * silently never fires (found while chasing an unrelated bug: the shaft visual was never registered).
 */
@EventBusSubscriber(modid = PocketGenerators.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class PocketGeneratorsClient {

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> SimpleBlockEntityVisualizer.builder(ModBlockEntities.RESOURCE_GENERATOR.get())
                .factory(ShaftVisual<ResourceGeneratorBlockEntity>::new)
                .neverSkipVanillaRender()
                .apply());
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.RESOURCE_GENERATOR.get(), ResourceGeneratorRenderer::new);
    }
}
