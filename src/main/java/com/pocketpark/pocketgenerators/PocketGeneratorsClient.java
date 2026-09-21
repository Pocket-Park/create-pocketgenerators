package com.pocketpark.pocketgenerators;

import com.pocketpark.pocketgenerators.block.ResourceGeneratorBlockEntity;
import com.pocketpark.pocketgenerators.block.Tier;
import com.pocketpark.pocketgenerators.client.ResourceGeneratorRenderer;
import com.pocketpark.pocketgenerators.network.SetOutputCountPayload;
import com.pocketpark.pocketgenerators.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.ShaftVisual;

import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * bus = MOD is required here: {@link EventBusSubscriber}'s default is the GAME bus, which never
 * receives mod-lifecycle events like {@link FMLClientSetupEvent} — without it this class's listener
 * silently never fires (found while chasing an unrelated bug: the shaft visual was never registered).
 */
@EventBusSubscriber(modid = PocketGenerators.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class PocketGeneratorsClient {

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // A visible rotating shaft segment poking through the block, standing in for a real
            // custom-modeled rotating part until proper art exists (design doc step 7).
            SimpleBlockEntityVisualizer.builder(ModBlockEntities.RESOURCE_GENERATOR.get())
                    .factory(ShaftVisual<ResourceGeneratorBlockEntity>::new)
                    .neverSkipVanillaRender()
                    .apply();

            // Batch size (Brass tier): scroll while looking at the block. Plain input event instead of
            // Create's ScrollValueBehaviour/Outliner, which rendered incorrectly for us and whose
            // right-click gesture collided with the filter-setting interaction. No modifier key needed:
            // the event is only cancelled (blocking the normal hotbar-scroll) when actually looking at
            // a Brass generator, so it doesn't interfere with hotbar scrolling anywhere else.
            NeoForge.EVENT_BUS.addListener(PocketGeneratorsClient::onMouseScroll);
        });
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.RESOURCE_GENERATOR.get(), ResourceGeneratorRenderer::new);
    }

    private static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null)
            return;
        if (!(minecraft.hitResult instanceof BlockHitResult blockHit) || blockHit.getType() == HitResult.Type.MISS)
            return;
        if (minecraft.level == null)
            return;
        if (!(minecraft.level.getBlockEntity(blockHit.getBlockPos()) instanceof ResourceGeneratorBlockEntity generator))
            return;
        if (generator.getTier() != Tier.BRASS)
            return;

        event.setCanceled(true);

        int step = event.getScrollDeltaY() > 0 ? 1 : -1;
        int newCount = Mth.clamp(generator.getOutputCount() + step, 1, 64);
        if (newCount == generator.getOutputCount())
            return;

        PacketDistributor.sendToServer(new SetOutputCountPayload(blockHit.getBlockPos(), newCount));
        minecraft.player.displayClientMessage(Component.translatable("pocketgenerators.batch_size.changed", newCount), true);
    }
}
