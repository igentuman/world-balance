package igentuman.world_balance.mixin;

import igentuman.world_balance.api.ChunkTickDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLevel.class, remap = false)
public class ServerLevelMixin {

    @Inject(method = "tickChunk", at = @At("HEAD"), remap = false)
    private void tickChunkBefore(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci) {
        ChunkTickDataAccessor accessor = (ChunkTickDataAccessor) pChunk;
        
        // Reset accumulated tick time at the start of each chunk tick
        accessor.world_balance$resetAccumulatedTickTime();
    }

    @Inject(method = "tickChunk", at = @At("TAIL"), remap = false)
    private void tickChunkAfter(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci) {
        ChunkTickDataAccessor accessor = (ChunkTickDataAccessor) pChunk;
        
        // Get the accumulated tick time from all block entities in this chunk
        double tickTime = accessor.world_balance$getAccumulatedTickTime();
        
        // Only update average if there was actual tick time recorded
        if (tickTime > 0) {
            // Calculate running average
            long count = accessor.world_balance$getTickCount();
            double currentAverage = accessor.world_balance$getAvgTickTime();
            
            // Running average formula: new_avg = (old_avg * count + new_value) / (count + 1)
            double newAverage = (currentAverage * count + tickTime) / (count + 1);
            
            accessor.world_balance$setAvgTickTime(newAverage);
            accessor.world_balance$setTickCount(count + 1);
        }
    }
}
