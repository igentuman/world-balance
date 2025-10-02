package igentuman.world_balance.api;

/**
 * Accessor interface for block entity tick timing data.
 * Implemented by TickingBlockEntityMixin to provide access to injected fields.
 */
public interface BlockEntityTickDataAccessor {
    
    /**
     * Get the average tick time for this block entity in milliseconds
     */
    double world_balance$getAvgTickTime();
    
    /**
     * Set the average tick time for this block entity in milliseconds
     */
    void world_balance$setAvgTickTime(double time);
    
    /**
     * Get the total number of ticks recorded for this block entity
     */
    long world_balance$getTickCount();
    
    /**
     * Set the total number of ticks recorded for this block entity
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
     * Check if this block entity should be throttled
     */
    boolean world_balance$shouldThrottle();
    
    /**
     * Set whether this block entity should be throttled
     */
    void world_balance$setShouldThrottle(boolean throttle);
}