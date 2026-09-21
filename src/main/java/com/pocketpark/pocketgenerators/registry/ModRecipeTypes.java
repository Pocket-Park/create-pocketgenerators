package com.pocketpark.pocketgenerators.registry;

import com.pocketpark.pocketgenerators.PocketGenerators;
import com.pocketpark.pocketgenerators.recipe.ResourceGeneratorRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, PocketGenerators.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ResourceGeneratorRecipe>> RESOURCE_GENERATOR =
            RECIPE_TYPES.register("resource_generating", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(PocketGenerators.MODID, "resource_generating")));
}
