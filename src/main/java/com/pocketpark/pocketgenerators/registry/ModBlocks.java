package com.pocketpark.pocketgenerators.registry;

import com.pocketpark.pocketgenerators.PocketGenerators;
import com.pocketpark.pocketgenerators.block.ResourceGeneratorBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PocketGenerators.MODID);

    public static final DeferredBlock<ResourceGeneratorBlock> RESOURCE_GENERATOR = BLOCKS.register("resource_generator",
            () -> new ResourceGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5f)));
}
