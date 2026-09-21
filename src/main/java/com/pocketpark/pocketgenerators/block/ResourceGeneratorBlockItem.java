package com.pocketpark.pocketgenerators.block;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A BlockItem for one specific Tier of {@link ResourceGeneratorBlock}. All tiers share the same
 * Block; this item only differs in which TIER blockstate value it places.
 */
public class ResourceGeneratorBlockItem extends BlockItem {

    private final Tier tier;

    public ResourceGeneratorBlockItem(Block block, Tier tier, Properties properties) {
        super(block, properties);
        this.tier = tier;
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        return state == null ? null : state.setValue(ResourceGeneratorBlock.TIER, tier);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.translatable("item.pocketgenerators.resource_generator.tooltip.summary")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.pocketgenerators.resource_generator.tooltip.tier",
                        Component.translatable("pocketgenerators.tier." + tier.getSerializedName()))
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable("item.pocketgenerators.resource_generator.tooltip.min_rpm", formatNumber(tier.getMinimumRpm()))
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.pocketgenerators.resource_generator.tooltip.stress", formatNumber(tier.getStressImpact()))
                .withStyle(ChatFormatting.DARK_GRAY));

        if (tier == Tier.NETHERITE)
            tooltip.add(Component.translatable("item.pocketgenerators.resource_generator.tooltip.nether_only")
                    .withStyle(ChatFormatting.RED));
        else if (tier == Tier.END)
            tooltip.add(Component.translatable("item.pocketgenerators.resource_generator.tooltip.end_only")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        else if (tier == Tier.BRASS)
            tooltip.add(Component.translatable("item.pocketgenerators.resource_generator.tooltip.batch_selector")
                    .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("item.pocketgenerators.resource_generator.tooltip.usage")
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
    }

    private static String formatNumber(float value) {
        return value == Math.floor(value) ? String.valueOf((int) value) : String.valueOf(value);
    }
}
