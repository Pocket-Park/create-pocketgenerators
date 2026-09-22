package com.pocketpark.pocketgenerators.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pocketpark.pocketgenerators.block.ResourceGeneratorBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Rests the filter item on top of the block, Basin/Depot-style, instead of floating high above it.
 * Uses the same lower-level ItemRenderer#render(...) + explicit BakedModel + FIXED context that
 * Create's own DepotRenderer uses; the higher-level renderStatic(..., GROUND, ...) rendered pure black
 * for us here.
 */
public class ResourceGeneratorRenderer implements BlockEntityRenderer<ResourceGeneratorBlockEntity> {

    public ResourceGeneratorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ResourceGeneratorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
            int packedLight, int packedOverlay) {
        ItemStack filterStack = be.filterInv.getStackInSlot(0);
        if (filterStack.isEmpty())
            return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(filterStack, be.getLevel(), null, 0);

        poseStack.pushPose();
        poseStack.translate(0.5, 1.05, 0.5);
        float rotation = (be.getLevel().getGameTime() % 360) + partialTick;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation * 2f));
        poseStack.scale(0.4f, 0.4f, 0.4f);
        itemRenderer.render(filterStack, ItemDisplayContext.FIXED, false, poseStack, bufferSource,
                packedLight, packedOverlay, bakedModel);
        poseStack.popPose();
    }
}
