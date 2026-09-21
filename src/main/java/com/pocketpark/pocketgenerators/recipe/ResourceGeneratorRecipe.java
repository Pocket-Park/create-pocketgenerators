package com.pocketpark.pocketgenerators.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pocketpark.pocketgenerators.block.Tier;
import com.pocketpark.pocketgenerators.registry.ModRecipeSerializers;
import com.pocketpark.pocketgenerators.registry.ModRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * A generator is a duplicator, not a mill: whatever sits in the filter slot is what comes out, this
 * recipe only acts as a whitelist (which items can be generated at all, and under which tier/cadence),
 * per design doc section 1 ("génère une ressource associée à cette entrée" = the entry itself).
 */
public class ResourceGeneratorRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient ingredient;
    private final Tier tierRequired;
    private final int baseTicks;

    public ResourceGeneratorRecipe(Ingredient ingredient, Tier tierRequired, int baseTicks) {
        this.ingredient = ingredient;
        this.tierRequired = tierRequired;
        this.baseTicks = baseTicks;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    /**
     * Netherite and End are exclusive side-branches (nether-only / end-only whitelists), not a
     * continuation of the Basic/Andesite/Brass ladder: an End generator shouldn't also unlock Netherite
     * recipes just because its ordinal is higher. The normal ladder still cascades as usual.
     */
    public boolean isUnlockedBy(Tier tier) {
        if (tierRequired == Tier.NETHERITE || tierRequired == Tier.END)
            return tier == tierRequired;
        return tier.ordinal() >= tierRequired.ordinal();
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Tier getTierRequired() {
        return tierRequired;
    }

    public int getBaseTicks() {
        return baseTicks;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return input.item().copyWithCount(1);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        // No single fixed result: the output depends on whatever is in the filter slot at generation time.
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.RESOURCE_GENERATOR.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.RESOURCE_GENERATOR.get();
    }

    public static class Serializer implements RecipeSerializer<ResourceGeneratorRecipe> {

        private static final MapCodec<ResourceGeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ResourceGeneratorRecipe::getIngredient),
                Tier.CODEC.fieldOf("tier_required").forGetter(ResourceGeneratorRecipe::getTierRequired),
                Codec.INT.optionalFieldOf("base_ticks", 200).forGetter(ResourceGeneratorRecipe::getBaseTicks)
        ).apply(builder, ResourceGeneratorRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ResourceGeneratorRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, ResourceGeneratorRecipe::getIngredient,
                Tier.STREAM_CODEC, ResourceGeneratorRecipe::getTierRequired,
                ByteBufCodecs.INT, ResourceGeneratorRecipe::getBaseTicks,
                ResourceGeneratorRecipe::new
        );

        @Override
        public MapCodec<ResourceGeneratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ResourceGeneratorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
