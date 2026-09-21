package com.pocketpark.pocketgenerators.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pocketpark.pocketgenerators.block.ResourceGeneratorBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Rests the filter item on top of the block, Basin-style, instead of floating high above it. The
 * current batch size is shown separately by hovering the block (Create's ScrollValueBehaviour overlay,
 * same as the Speed Controller), not as a text label rendered here.
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

        Minecraft minecraft = Minecraft.getInstance();

        poseStack.pushPose();
        poseStack.translate(0.5, 1.05, 0.5);
        float rotation = (be.getLevel().getGameTime() % 360) + partialTick;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation * 2f));
        poseStack.scale(0.4f, 0.4f, 0.4f);
        minecraft.getItemRenderer().renderStatic(filterStack, ItemDisplayContext.GROUND, packedLight, packedOverlay,
                poseStack, bufferSource, be.getLevel(), 0);
        poseStack.popPose();
    }
}
