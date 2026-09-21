package com.pocketpark.pocketgenerators.network;

import com.pocketpark.pocketgenerators.PocketGenerators;
import com.pocketpark.pocketgenerators.block.ResourceGeneratorBlockEntity;
import com.pocketpark.pocketgenerators.block.Tier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client -> server: apply a new batch size, picked by shift + scroll while looking at a Brass-tier
 * generator (see PocketGeneratorsClient). Only Brass-tier generators accept this.
 */
public record SetOutputCountPayload(BlockPos pos, int count) implements CustomPacketPayload {

    public static final Type<SetOutputCountPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PocketGenerators.MODID, "set_output_count"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetOutputCountPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetOutputCountPayload::pos,
            ByteBufCodecs.INT, SetOutputCountPayload::count,
            SetOutputCountPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetOutputCountPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player().level() instanceof ServerLevel level))
                return;
            if (!level.isLoaded(payload.pos()))
                return;

            BlockEntity blockEntity = level.getBlockEntity(payload.pos());
            if (!(blockEntity instanceof ResourceGeneratorBlockEntity generator))
                return;
            if (generator.getTier() != Tier.BRASS)
                return;

            generator.setOutputCount(payload.count());
        });
    }
}
