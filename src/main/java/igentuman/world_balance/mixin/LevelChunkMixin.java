package igentuman.world_balance.mixin;

import igentuman.world_balance.api.ChunkTickDataAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelChunk.class)
public class LevelChunkMixin implements ChunkTickDataAccessor {
    
    @Unique
    private double world_balance$avgTickTime = 0.0;
    
    @Unique
    private long world_balance$tickCount = 0L;
    
    @Unique
    private long world_balance$tickAttempts = 0L;
    
    @Unique
    private volatile double world_balance$accumulatedTickTime = 0.0;
    
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
    public synchronized void world_balance$accumulateTickTime(double timeMs) {
        world_balance$accumulatedTickTime += timeMs;
        
        // Update the running average immediately
        long count = world_balance$tickCount;
        double currentAverage = world_balance$avgTickTime;
        
        // Running average formula: new_avg = (old_avg * count + new_value) / (count + 1)
        double newAverage = (currentAverage * count + timeMs) / (count + 1);
        
        world_balance$avgTickTime = newAverage;
        world_balance$tickCount = count + 1;
    }
    
    @Override
    public double world_balance$getAccumulatedTickTime() {
        return world_balance$accumulatedTickTime;
    }
    
    @Override
    public void world_balance$resetAccumulatedTickTime() {
        world_balance$accumulatedTickTime = 0.0;
    }
}