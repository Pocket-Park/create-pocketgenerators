package com.pocketpark.pocketgenerators.compat.jei;

import com.pocketpark.pocketgenerators.PocketGenerators;
import com.pocketpark.pocketgenerators.recipe.ResourceGeneratorRecipe;
import com.pocketpark.pocketgenerators.registry.ModItems;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * A generator duplicates whatever is in its filter, so input and output are the same ingredient —
 * shown side by side with an arrow, which reads clearly even though both slots show identical items.
 */
public class ResourceGeneratingCategory implements IRecipeCategory<RecipeHolder<ResourceGeneratorRecipe>> {

    public static final RecipeType<RecipeHolder<ResourceGeneratorRecipe>> TYPE =
            RecipeType.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(PocketGenerators.MODID, "resource_generating"));

    private static final int WIDTH = 100;
    private static final int HEIGHT = 26;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;

    public ResourceGeneratingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(ModItems.RESOURCE_GENERATOR_BASIC.get().getDefaultInstance());
        this.arrow = guiHelper.drawableBuilder(ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png"), 82, 34, 24, 16)
                .setTextureSize(256, 256)
                .build();
    }

    @Override
    public RecipeType<RecipeHolder<ResourceGeneratorRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.pocketgenerators.resource_generating");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ResourceGeneratorRecipe> holder, IFocusGroup focuses) {
        ResourceGeneratorRecipe recipe = holder.value();

        builder.addSlot(RecipeIngredientRole.INPUT, 5, 4)
                .addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 4)
                .addIngredients(recipe.getIngredient());
    }

    @Override
    public void draw(RecipeHolder<ResourceGeneratorRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 29, 5);

        ResourceGeneratorRecipe recipe = holder.value();
        Component tierLabel = Component.translatable("pocketgenerators.tier." + recipe.getTierRequired().getSerializedName());
        graphics.drawString(Minecraft.getInstance().font,
                Component.translatable("category.pocketgenerators.tier_required", tierLabel), 0, HEIGHT - 8, 0x555555, false);
    }
}
