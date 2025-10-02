package igentuman.world_balance.api;

/**
 * Accessor interface for chunk tick timing data.
 * Implemented by LevelChunkMixin to provide access to injected fields.
 */
public interface ChunkTickDataAccessor {
    
    /**
     * Get the average tick time for this chunk in milliseconds
     */
    double world_balance$getAvgTickTime();
    
    /**
     * Set the average tick time for this chunk in milliseconds
     */
    void world_balance$setAvgTickTime(double time);
    
    /**
     * Get the total number of ticks recorded for this chunk
     */
    long world_balance$getTickCount();
    
    /**
     * Set the total number of ticks recorded for this chunk
     */
    void world_balance$setTickCount(long count);
    
    /**
     * Get the total number of tick attempts (including throttled ones)
     */
    long world_balance$getTickAttempts();
    
    /**
     * Set the total number of tick attempts (including throttled ones)
     */
    void world_balance$setTickAttempts(long attempts);
    
    /**
     * Accumulate tick time from block entities in this chunk
     */
    void world_balance$accumulateTickTime(double timeMs);
    
    /**
     * Get the accumulated tick time for the current tick cycle
     */
    double world_balance$getAccumulatedTickTime();
    
    /**
     * Reset the accumulated tick time (called at the start of each chunk tick)
     */
    void world_balance$resetAccumulatedTickTime();
}