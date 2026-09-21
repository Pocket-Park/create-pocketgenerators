package com.pocketpark.pocketgenerators.registry;

import com.pocketpark.pocketgenerators.PocketGenerators;
import com.pocketpark.pocketgenerators.block.ResourceGeneratorBlockItem;
import com.pocketpark.pocketgenerators.block.Tier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PocketGenerators.MODID);

    public static final DeferredItem<BlockItem> RESOURCE_GENERATOR_BASIC = ITEMS.register("resource_generator_basic",
            () -> new ResourceGeneratorBlockItem(ModBlocks.RESOURCE_GENERATOR.get(), Tier.BASIC, new Item.Properties()));

    public static final DeferredItem<BlockItem> RESOURCE_GENERATOR_ANDESITE = ITEMS.register("resource_generator_andesite",
            () -> new ResourceGeneratorBlockItem(ModBlocks.RESOURCE_GENERATOR.get(), Tier.ANDESITE, new Item.Properties()));

    public static final DeferredItem<BlockItem> RESOURCE_GENERATOR_BRASS = ITEMS.register("resource_generator_brass",
            () -> new ResourceGeneratorBlockItem(ModBlocks.RESOURCE_GENERATOR.get(), Tier.BRASS, new Item.Properties()));

    public static final DeferredItem<BlockItem> RESOURCE_GENERATOR_NETHERITE = ITEMS.register("resource_generator_netherite",
            () -> new ResourceGeneratorBlockItem(ModBlocks.RESOURCE_GENERATOR.get(), Tier.NETHERITE, new Item.Properties().fireResistant()));

    public static final DeferredItem<BlockItem> RESOURCE_GENERATOR_END = ITEMS.register("resource_generator_end",
            () -> new ResourceGeneratorBlockItem(ModBlocks.RESOURCE_GENERATOR.get(), Tier.END, new Item.Properties()));
}
