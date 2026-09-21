package com.pocketpark.pocketgenerators.compat.jei;

import java.util.List;

import com.pocketpark.pocketgenerators.PocketGenerators;
import com.pocketpark.pocketgenerators.recipe.ResourceGeneratorRecipe;
import com.pocketpark.pocketgenerators.registry.ModItems;
import com.pocketpark.pocketgenerators.registry.ModRecipeTypes;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

@JeiPlugin
public class PocketGeneratorsJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(PocketGenerators.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ResourceGeneratingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;

        List<RecipeHolder<ResourceGeneratorRecipe>> recipes =
                minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.RESOURCE_GENERATOR.get());
        registration.addRecipes(ResourceGeneratingCategory.TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.RESOURCE_GENERATOR_BASIC.get()), ResourceGeneratingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.RESOURCE_GENERATOR_ANDESITE.get()), ResourceGeneratingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.RESOURCE_GENERATOR_BRASS.get()), ResourceGeneratingCategory.TYPE);
    }
}
