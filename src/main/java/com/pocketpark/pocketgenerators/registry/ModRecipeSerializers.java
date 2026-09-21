package com.pocketpark.pocketgenerators.registry;

import com.pocketpark.pocketgenerators.PocketGenerators;
import com.pocketpark.pocketgenerators.recipe.ResourceGeneratorRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, PocketGenerators.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, ResourceGeneratorRecipe.Serializer> RESOURCE_GENERATOR =
            RECIPE_SERIALIZERS.register("resource_generating", ResourceGeneratorRecipe.Serializer::new);
}
