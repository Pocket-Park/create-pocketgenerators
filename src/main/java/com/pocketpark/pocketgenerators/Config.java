package com.pocketpark.pocketgenerators;

import java.util.EnumMap;
import java.util.Map;

import com.pocketpark.pocketgenerators.block.Tier;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Server-side, in-game-tunable version of the per-tier numbers from design doc section 2, so balance
 * can be retuned without recompiling. Tier's own constants only serve as the defaults defined here.
 */
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MIN_TICKS = BUILDER
            .comment("Minimum ticks between two generated items, regardless of RPM/tier multiplier.")
            .defineInRange("minTicks", 20, 1, 6000);

    public static final Map<Tier, ModConfigSpec.DoubleValue> MINIMUM_RPM = new EnumMap<>(Tier.class);
    public static final Map<Tier, ModConfigSpec.DoubleValue> STRESS_IMPACT = new EnumMap<>(Tier.class);
    public static final Map<Tier, ModConfigSpec.DoubleValue> CADENCE_MULTIPLIER = new EnumMap<>(Tier.class);

    static {
        BUILDER.push("tiers");
        for (Tier tier : Tier.values()) {
            BUILDER.push(tier.getSerializedName());

            MINIMUM_RPM.put(tier, BUILDER
                    .comment("Minimum shaft speed (RPM) required for this tier to generate anything.")
                    .defineInRange("minimumRpm", tier.getDefaultMinimumRpm(), 0, 25600));

            STRESS_IMPACT.put(tier, BUILDER
                    .comment("Stress (SU) this tier's generator consumes at 1 RPM.")
                    .defineInRange("stressImpact", tier.getDefaultStressImpact(), 0, 25600));

            CADENCE_MULTIPLIER.put(tier, BUILDER
                    .comment("Cadence multiplier applied to this tier's generation speed.")
                    .defineInRange("cadenceMultiplier", (double) tier.getDefaultCadenceMultiplier(), 0.01, 100));

            BUILDER.pop();
        }
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
