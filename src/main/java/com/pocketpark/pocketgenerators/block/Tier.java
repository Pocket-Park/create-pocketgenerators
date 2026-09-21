package com.pocketpark.pocketgenerators.block;

import com.mojang.serialization.Codec;
import com.pocketpark.pocketgenerators.Config;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * The per-tier numbers below are only the config defaults; actual values are read from {@link Config}
 * at runtime so they can be retuned in-game without recompiling (design doc section 2 calibration).
 */
public enum Tier implements StringRepresentable {
    BASIC(16f, 4f, 1.0f),
    ANDESITE(32f, 8f, 2.0f),
    BRASS(64f, 16f, 4.0f),
    NETHERITE(128f, 32f, 8.0f),
    END(256f, 64f, 16.0f);

    public static final Codec<Tier> CODEC = StringRepresentable.fromEnum(Tier::values);
    public static final StreamCodec<ByteBuf, Tier> STREAM_CODEC =
            ByteBufCodecs.idMapper(id -> Tier.values()[id], Tier::ordinal);

    private final float defaultMinimumRpm;
    private final float defaultStressImpact;
    private final float defaultCadenceMultiplier;

    Tier(float defaultMinimumRpm, float defaultStressImpact, float defaultCadenceMultiplier) {
        this.defaultMinimumRpm = defaultMinimumRpm;
        this.defaultStressImpact = defaultStressImpact;
        this.defaultCadenceMultiplier = defaultCadenceMultiplier;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public float getDefaultMinimumRpm() {
        return defaultMinimumRpm;
    }

    public float getDefaultStressImpact() {
        return defaultStressImpact;
    }

    public float getDefaultCadenceMultiplier() {
        return defaultCadenceMultiplier;
    }

    public float getMinimumRpm() {
        return Config.MINIMUM_RPM.get(this).get().floatValue();
    }

    public float getStressImpact() {
        return Config.STRESS_IMPACT.get(this).get().floatValue();
    }

    public float getCadenceMultiplier() {
        return Config.CADENCE_MULTIPLIER.get(this).get().floatValue();
    }
}
