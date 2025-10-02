package igentuman.world_balance;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Config {
    
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    // Block Entity Throttle Configuration
    public static final ForgeConfigSpec.DoubleValue BLOCK_ENTITY_THROTTLE_THRESHOLD_MS;
    public static final ForgeConfigSpec.DoubleValue CHUNK_THROTTLE_THRESHOLD_MS;
    
    // Chunk-based throttling intervals
    public static final ForgeConfigSpec.IntValue THROTTLE_INTERVAL_SLOW_CHUNK;
    public static final ForgeConfigSpec.IntValue THROTTLE_INTERVAL_VERY_SLOW_CHUNK;
    public static final ForgeConfigSpec.IntValue THROTTLE_INTERVAL_EXTREMELY_SLOW_CHUNK;
    
    // Chunk slowness thresholds
    public static final ForgeConfigSpec.DoubleValue CHUNK_SLOW_THRESHOLD_MS;
    public static final ForgeConfigSpec.DoubleValue CHUNK_VERY_SLOW_THRESHOLD_MS;
    
    static {
        BUILDER.push("throttling");
        BUILDER.comment("Block Entity and Chunk Throttling Configuration");
        
        BLOCK_ENTITY_THROTTLE_THRESHOLD_MS = BUILDER
                .comment("Average tick time threshold (in milliseconds) for individual block entities.",
                        "Block entities exceeding this threshold will be throttled.",
                        "Default: 1.0 ms")
                .defineInRange("blockEntityThrottleThresholdMs", 1.0, 0.1, 100.0);
        
        CHUNK_THROTTLE_THRESHOLD_MS = BUILDER
                .comment("Average tick time threshold (in milliseconds) for chunks.",
                        "Chunks exceeding this threshold will have their block entities throttled.",
                        "Default: 5.0 ms")
                .defineInRange("chunkThrottleThresholdMs", 5.0, 0.1, 100.0);
        
        CHUNK_SLOW_THRESHOLD_MS = BUILDER
                .comment("Threshold (in milliseconds) to consider a chunk as 'slow'.",
                        "Default: 5.0 ms")
                .defineInRange("chunkSlowThresholdMs", 5.0, 0.1, 100.0);
        
        CHUNK_VERY_SLOW_THRESHOLD_MS = BUILDER
                .comment("Threshold (in milliseconds) to consider a chunk as 'very slow'.",
                        "Default: 10.0 ms")
                .defineInRange("chunkVerySlowThresholdMs", 10.0, 0.1, 100.0);
        
        THROTTLE_INTERVAL_SLOW_CHUNK = BUILDER
                .comment("Throttle interval for slow block entities in normal chunks.",
                        "Block entities will tick every N ticks (e.g., 5 means tick once every 5 ticks).",
                        "Default: 5")
                .defineInRange("throttleIntervalSlowChunk", 5, 1, 100);
        
        THROTTLE_INTERVAL_VERY_SLOW_CHUNK = BUILDER
                .comment("Throttle interval for slow block entities in slow chunks (above chunkSlowThresholdMs).",
                        "Default: 10")
                .defineInRange("throttleIntervalVerySlowChunk", 10, 1, 100);
        
        THROTTLE_INTERVAL_EXTREMELY_SLOW_CHUNK = BUILDER
                .comment("Throttle interval for slow block entities in very slow chunks (above chunkVerySlowThresholdMs).",
                        "Default: 20")
                .defineInRange("throttleIntervalExtremelySlowChunk", 20, 1, 100);
        
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
    
    public static void register(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, SPEC, "world_balance-server.toml");
    }
}