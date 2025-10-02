package igentuman.world_balance.mixin;

import igentuman.world_balance.Config;
import igentuman.world_balance.api.BlockEntityTickDataAccessor;
import igentuman.world_balance.api.ChunkTickDataAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to measure tick time for TickingBlockEntity and implement throttling
 */
@Mixin(targets = "net.minecraft.world.level.chunk.LevelChunk$BoundTickingBlockEntity")
public abstract class TickingBlockEntityMixin implements BlockEntityTickDataAccessor {
    
    @Shadow
    private BlockEntity blockEntity;
    
    @Unique
    private long world_balance$tickStartTime = 0L;
    
    @Unique
    private double world_balance$avgTickTime = 0.0;
    
    @Unique
    private long world_balance$tickCount = 0L;
    
    @Unique
    private long world_balance$tickAttempts = 0L;
    
    @Unique
    private boolean world_balance$shouldThrottle = false;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void beforeTick(CallbackInfo ci) {
        world_balance$tickStartTime = 0;
        if (blockEntity == null) {
            return;
        }
        
        Level level = blockEntity.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }
        
        world_balance$tickAttempts++;
        
        // Get config values
        double blockEntityThreshold = Config.BLOCK_ENTITY_THROTTLE_THRESHOLD_MS.get();
        double chunkThreshold = Config.CHUNK_THROTTLE_THRESHOLD_MS.get();
        double chunkSlowThreshold = Config.CHUNK_SLOW_THRESHOLD_MS.get();
        double chunkVerySlowThreshold = Config.CHUNK_VERY_SLOW_THRESHOLD_MS.get();
        int throttleIntervalSlow = Config.THROTTLE_INTERVAL_SLOW_CHUNK.get();
        int throttleIntervalVerySlow = Config.THROTTLE_INTERVAL_VERY_SLOW_CHUNK.get();
        int throttleIntervalExtremelySlow = Config.THROTTLE_INTERVAL_EXTREMELY_SLOW_CHUNK.get();
        
        // Check if this block entity should be throttled
        boolean shouldThrottleBlockEntity = world_balance$avgTickTime > blockEntityThreshold;
        
        // Check if the chunk is slow
        boolean shouldThrottleChunk = false;
        double chunkAvgTime = 0.0;
        
        BlockPos pos = blockEntity.getBlockPos();
        if (pos != null) {
            try {
                LevelChunk chunk = level.getChunkAt(pos);
                if (chunk instanceof ChunkTickDataAccessor chunkAccessor) {
                    chunkAvgTime = chunkAccessor.world_balance$getAvgTickTime();
                    shouldThrottleChunk = chunkAvgTime > chunkThreshold;
                }
            } catch (Exception e) {
                System.out.println("[WorldBalance] Exception caught: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        if (shouldThrottleBlockEntity) {
            world_balance$shouldThrottle = true;
            if (chunkAvgTime > chunkVerySlowThreshold && world_balance$tickAttempts % throttleIntervalExtremelySlow != 0) {
                ci.cancel();
                return;
            } else if (chunkAvgTime > chunkSlowThreshold && world_balance$tickAttempts % throttleIntervalVerySlow != 0) {
                ci.cancel();
                return;
            } else if (chunkAvgTime <= chunkSlowThreshold && world_balance$tickAttempts % throttleIntervalSlow != 0) {
                ci.cancel();
                return;
            }
        } else if(shouldThrottleChunk && world_balance$tickAttempts % 2 != 0) {
            world_balance$shouldThrottle = true;
            ci.cancel();
            return;
        } else {
            world_balance$shouldThrottle = false;
        }
        
        world_balance$tickStartTime = System.nanoTime();
    }
    
    @Inject(method = "tick", at = @At("RETURN"), remap = false)
    private void afterTick(CallbackInfo ci) {
        if (blockEntity != null && world_balance$tickStartTime > 0) {
            Level level = blockEntity.getLevel();
            if (level != null && !level.isClientSide) {
                long endTime = System.nanoTime();
                double tickTimeMs = (endTime - world_balance$tickStartTime) / 1_000_000.0;
                
                // Update block entity's running average
                long count = world_balance$tickCount;
                double currentAverage = world_balance$avgTickTime;
                double newAverage = (currentAverage * count + tickTimeMs) / (count + 1);
                world_balance$avgTickTime = newAverage;
                world_balance$tickCount = count + 1;
                
                // Accumulate to chunk
                BlockPos pos = blockEntity.getBlockPos();
                if (pos != null) {
                    try {
                        LevelChunk chunk = level.getChunkAt(pos);
                        if (chunk instanceof ChunkTickDataAccessor chunkAccessor) {
                            chunkAccessor.world_balance$accumulateTickTime(tickTimeMs);
                        }
                    } catch (Exception e) {
                        // Silently ignore errors to prevent crashes
                    }
                }
                
                world_balance$tickStartTime = 0L;
            }
        }
    }
    
    @Override
    public double world_balance$getAvgTickTime() {
        return world_balance$avgTickTime;
    }
    
    @Override
    public void world_balance$setAvgTickTime(double time) {
        world_balance$avgTickTime = time;
    }
    
    @Override
    public long world_balance$getTickCount() {
        return world_balance$tickCount;
    }
    
    @Override
    public void world_balance$setTickCount(long count) {
        world_balance$tickCount = count;
    }
    
    @Override
    public long world_balance$getTickAttempts() {
        return world_balance$tickAttempts;
    }
    
    @Override
    public void world_balance$setTickAttempts(long attempts) {
        world_balance$tickAttempts = attempts;
    }
    
    @Override
    public boolean world_balance$shouldThrottle() {
        return world_balance$shouldThrottle;
    }
    
    @Override
    public void world_balance$setShouldThrottle(boolean throttle) {
        world_balance$shouldThrottle = throttle;
    }
}