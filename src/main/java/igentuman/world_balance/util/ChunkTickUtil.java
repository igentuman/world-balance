package igentuman.world_balance.util;

import igentuman.world_balance.api.ChunkTickDataAccessor;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Utility class for accessing chunk tick timing data.
 */
public class ChunkTickUtil {
    
    /**
     * Get the average tick time for a chunk in milliseconds
     * 
     * @param chunk The chunk to query
     * @return Average tick time in milliseconds, or 0.0 if not yet measured
     */
    public static double getAvgTickTime(LevelChunk chunk) {
        return ((ChunkTickDataAccessor) chunk).world_balance$getAvgTickTime();
    }
    
    /**
     * Get the total number of ticks recorded for a chunk
     * 
     * @param chunk The chunk to query
     * @return Total tick count
     */
    public static long getTickCount(LevelChunk chunk) {
        return ((ChunkTickDataAccessor) chunk).world_balance$getTickCount();
    }
    
    /**
     * Get the total number of tick attempts (including throttled ones) for a chunk
     * 
     * @param chunk The chunk to query
     * @return Total tick attempts
     */
    public static long getTickAttempts(LevelChunk chunk) {
        return ((ChunkTickDataAccessor) chunk).world_balance$getTickAttempts();
    }
    
    /**
     * Check if a chunk is currently being throttled
     * 
     * @param chunk The chunk to check
     * @return true if the chunk's average tick time exceeds the throttle threshold
     */
    public static boolean isThrottled(LevelChunk chunk) {
        return getAvgTickTime(chunk) > 20.0; // 20ms threshold
    }
    
    /**
     * Get the throttle ratio for a chunk (how many ticks are being skipped)
     * 
     * @param chunk The chunk to query
     * @return Ratio of actual ticks to attempted ticks, or 1.0 if not throttled
     */
    public static double getThrottleRatio(LevelChunk chunk) {
        long attempts = getTickAttempts(chunk);
        long actualTicks = getTickCount(chunk);
        
        if (attempts == 0) {
            return 1.0;
        }
        
        return (double) actualTicks / attempts;
    }
}