package com.pocketpark.pocketgenerators.block;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pocketpark.pocketgenerators.Config;
import com.pocketpark.pocketgenerators.recipe.ResourceGeneratorRecipe;
import com.pocketpark.pocketgenerators.registry.ModBlockEntities;
import com.pocketpark.pocketgenerators.registry.ModRecipeTypes;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

public class ResourceGeneratorBlockEntity extends KineticBlockEntity {

    // Reference item, never consumed: the block "duplicates" it per design doc section 3, it doesn't burn it as fuel.
    public final ItemStackHandler filterInv = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            lastRecipe = null;
            setChanged();
        }
    };
    public final ItemStackHandler outputInv = new ItemStackHandler(9);
    public final IItemHandler capability = new GeneratorInventoryHandler();

    // Batch size, Brass tier only. Create's own value-settings dial: hold right-click on the top face
    // to open it, same widget/gesture as the Speed Controller or Sequenced Gearshift. Stress cost
    // scales with it directly.
    private ScrollValueBehaviour batchSize;

    private ResourceGeneratorRecipe lastRecipe;
    private int timer;

    public ResourceGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.RESOURCE_GENERATOR.get(),
                (be, side) -> be.capability);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        batchSize = new ScrollValueBehaviour(CreateLang.translateDirect("pocketgenerators.batch_size"), this, new BatchSizeSlot())
                .between(1, 64)
                .onlyActiveWhen(() -> getTier() == Tier.BRASS);
        batchSize.value = 1;
        behaviours.add(batchSize);
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide)
            return;

        pushOutput();

        if (getSpeed() == 0 || !isSpeedRequirementFulfilled())
            return;
        if (isOutputFull())
            return;

        Optional<ResourceGeneratorRecipe> recipe = findRecipe();
        if (recipe.isEmpty() || !recipe.get().isUnlockedBy(getTier())) {
            timer = 0;
            return;
        }

        if (timer > 0) {
            timer--;
            return;
        }

        generate(recipe.get());
        timer = getTicksPerItem(recipe.get());
    }

    private Optional<ResourceGeneratorRecipe> findRecipe() {
        ItemStack filterStack = filterInv.getStackInSlot(0);
        if (filterStack.isEmpty())
            return Optional.empty();

        SingleRecipeInput input = new SingleRecipeInput(filterStack);
        if (lastRecipe != null && lastRecipe.matches(input, level))
            return Optional.of(lastRecipe);

        Optional<RecipeHolder<ResourceGeneratorRecipe>> found =
                level.getRecipeManager().getRecipeFor(ModRecipeTypes.RESOURCE_GENERATOR.get(), input, level);
        lastRecipe = found.map(RecipeHolder::value).orElse(null);
        return Optional.ofNullable(lastRecipe);
    }

    /**
     * Actively inserts buffered output into whatever IItemHandler capability sits at the output
     * position (a belt, a chest, a hopper...), same mechanism as inserting into a chest. Belts don't
     * pull on their own, so we have to push. Anything that doesn't fit stays buffered for next tick.
     */
    private void pushOutput() {
        Direction outputDirection = getBlockState().getValue(ResourceGeneratorBlock.OUTPUT_FACING);
        BlockPos targetPos = worldPosition.relative(outputDirection);
        IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, targetPos, outputDirection.getOpposite());
        if (target == null)
            return;

        for (int slot = 0; slot < outputInv.getSlots(); slot++) {
            ItemStack stack = outputInv.getStackInSlot(slot);
            if (stack.isEmpty())
                continue;

            ItemStack remainder = ItemHandlerHelper.insertItem(target, stack, false);
            if (remainder.getCount() != stack.getCount()) {
                outputInv.setStackInSlot(slot, remainder);
                setChanged();
            }
        }
    }

    /**
     * Overridden instead of registering into Create's BlockStressValues registry: that registry keys
     * purely by Block class with no state context, so it cannot express a per-tier value for a single
     * shared Block. Reading the TIER blockstate property here does.
     */
    @Override
    public float calculateStressApplied() {
        float impact = getTier().getStressImpact() * getOutputCount();
        this.lastStressApplied = impact;
        return impact;
    }

    /**
     * Same reasoning as {@link #calculateStressApplied()}: IRotate#getMinimumRequiredSpeedLevel() has
     * no state context either, so the per-tier RPM threshold from the design doc is enforced here.
     */
    @Override
    public boolean isSpeedRequirementFulfilled() {
        return Math.abs(getSpeed()) >= getTier().getMinimumRpm();
    }

    public Tier getTier() {
        return getBlockState().getValue(ResourceGeneratorBlock.TIER);
    }

    /**
     * ticksParItem = max(minTicks, baseTicks / (rpmActuel * tierMultiplier)), per design doc section 2.
     * baseTicks now comes from the matched recipe instead of a block-wide constant.
     */
    public int getTicksPerItem(ResourceGeneratorRecipe recipe) {
        float effectiveRpm = Math.abs(getSpeed()) * getTier().getCadenceMultiplier();
        return (int) Math.max(Config.MIN_TICKS.get(), recipe.getBaseTicks() / effectiveRpm);
    }

    private boolean isOutputFull() {
        for (int i = 0; i < outputInv.getSlots(); i++)
            if (outputInv.getStackInSlot(i).getCount() < outputInv.getSlotLimit(i))
                return false;
        return true;
    }

    private void generate(ResourceGeneratorRecipe recipe) {
        ItemStack result = recipe.assemble(new SingleRecipeInput(filterInv.getStackInSlot(0)), level.registryAccess());
        result.setCount(Math.min(getOutputCount(), result.getMaxStackSize()));
        ItemHandlerHelper.insertItemStacked(outputInv, result, false);
        setChanged();
        sendData();
    }

    /**
     * ScrollValueBehaviour#read() does an unguarded nbt.getInt("ScrollValue"), which defaults to 0 for
     * any save that predates this field (or hasn't round-tripped through write() yet). Clamping here
     * keeps a stray 0 from producing an empty stack and zeroing out stress on every tier, not just Brass.
     */
    public int getOutputCount() {
        return Math.max(1, batchSize.getValue());
    }

    public void setFilterItem(Player player, ItemStack heldStack) {
        ItemStack previous = filterInv.getStackInSlot(0);
        filterInv.setStackInSlot(0, heldStack.copyWithCount(1));
        heldStack.shrink(1);
        if (!previous.isEmpty())
            player.getInventory().placeItemBackInInventory(previous);
        setChanged();
        sendData();
    }

    public void emptyFilterInto(Consumer<ItemStack> target) {
        ItemStack stack = filterInv.getStackInSlot(0);
        if (!stack.isEmpty())
            target.accept(stack);
        filterInv.setStackInSlot(0, ItemStack.EMPTY);
        setChanged();
        sendData();
    }

    public void emptyOutputInto(Consumer<ItemStack> target) {
        IItemHandlerModifiable inv = outputInv;
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty())
                target.accept(stack);
            inv.setStackInSlot(slot, ItemStack.EMPTY);
        }
        setChanged();
        sendData();
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, filterInv);
        ItemHelper.dropContents(level, worldPosition, outputInv);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("Timer", timer);
        compound.put("FilterInventory", filterInv.serializeNBT(registries));
        compound.put("OutputInventory", outputInv.serializeNBT(registries));
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        timer = compound.getInt("Timer");
        filterInv.deserializeNBT(registries, compound.getCompound("FilterInventory"));
        outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
        super.read(compound, registries, clientPacket);
    }

    private class GeneratorInventoryHandler extends CombinedInvWrapper {

        public GeneratorInventoryHandler() {
            super(filterInv, outputInv);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (getHandlerFromIndex(getIndexForSlot(slot)) == outputInv)
                return false;
            return super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (getHandlerFromIndex(getIndexForSlot(slot)) == outputInv)
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (getHandlerFromIndex(getIndexForSlot(slot)) == filterInv)
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }
    }

    /** Hotspot for the batch size dial: the whole top face, generously sized so it's easy to hit. */
    private static class BatchSizeSlot extends ValueBoxTransform {
        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return new Vec3(.5, 15 / 16f, .5);
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            TransformStack.of(ms).rotateXDegrees(90);
        }

        @Override
        public float getScale() {
            return 0.9f;
        }
    }
}
