package com.pocketpark.pocketgenerators.block;

import com.pocketpark.pocketgenerators.registry.ModBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ResourceGeneratorBlock extends RotatedPillarKineticBlock implements IBE<ResourceGeneratorBlockEntity> {

    public static final EnumProperty<Tier> TIER = EnumProperty.create("tier", Tier.class);
    public static final EnumProperty<Direction> OUTPUT_FACING = EnumProperty.create("output_facing", Direction.class);

    public ResourceGeneratorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(AXIS, Axis.Y).setValue(TIER, Tier.BASIC)
                .setValue(OUTPUT_FACING, Direction.DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TIER, OUTPUT_FACING);
    }

    /**
     * Wrenching normally rotates a kinetic block's shaft axis (IWrenchable's default), but that gesture
     * is more useful here for picking which side the generator dumps its output on, so we repurpose it
     * instead of stacking a second control scheme on top (design doc section 9's open question).
     */
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction current = state.getValue(OUTPUT_FACING);
        Direction next = Direction.values()[(current.ordinal() + 1) % Direction.values().length];
        BlockState newState = updateAfterWrenched(state.setValue(OUTPUT_FACING, next), context);

        KineticBlockEntity.switchToBlockState(level, pos, newState);

        if (level.getBlockState(pos) != state) {
            IWrenchable.playRotateSound(level, pos);
            // Only the server side sends this, to avoid a duplicate line in singleplayer (both sides run onWrenched).
            if (!level.isClientSide && context.getPlayer() instanceof ServerPlayer serverPlayer)
                serverPlayer.displayClientMessage(Component.translatable("pocketgenerators.output_facing.changed",
                        Component.translatable("pocketgenerators.direction." + next.getSerializedName())), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == getRotationAxis(state);
    }

    /**
     * Right-click with an item sets/replaces the filter (the reference item to duplicate). Right-click
     * empty-handed empties the output buffer; sneaking + empty-handed instead withdraws the filter item.
     * Stand-in for a proper GUI (design doc step 6, deliberately skipped since we chose a physical slot).
     * The Brass batch size is set separately, by hovering the block and scrolling (ScrollValueBehaviour).
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof WrenchItem)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        if (!stack.isEmpty()) {
            withBlockEntityDo(level, pos, generator -> generator.setFilterItem(player, stack));
            return ItemInteractionResult.SUCCESS;
        }

        withBlockEntityDo(level, pos, generator -> {
            if (player.isShiftKeyDown())
                generator.emptyFilterInto(item -> player.getInventory().placeItemBackInInventory(item));
            else
                generator.emptyOutputInto(item -> player.getInventory().placeItemBackInInventory(item));
        });

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public Class<ResourceGeneratorBlockEntity> getBlockEntityClass() {
        return ResourceGeneratorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ResourceGeneratorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.RESOURCE_GENERATOR.get();
    }
}
