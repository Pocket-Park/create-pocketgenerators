package com.pocketpark.pocketgenerators.registry;

import com.pocketpark.pocketgenerators.PocketGenerators;
import com.pocketpark.pocketgenerators.block.ResourceGeneratorBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PocketGenerators.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResourceGeneratorBlockEntity>> RESOURCE_GENERATOR =
            BLOCK_ENTITY_TYPES.register("resource_generator",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new ResourceGeneratorBlockEntity(ModBlockEntities.RESOURCE_GENERATOR.get(), pos, state),
                            ModBlocks.RESOURCE_GENERATOR.get())
                            .build(null));
}
