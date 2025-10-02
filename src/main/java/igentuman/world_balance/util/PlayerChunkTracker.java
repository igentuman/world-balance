package igentuman.world_balance.util;

import igentuman.world_balance.api.ChunkTickDataAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks player chunk positions and notifies them when entering slow chunks
 */
public class PlayerChunkTracker {
    
    private static final Map<UUID, ChunkPos> playerLastChunk = new HashMap<>();
    private static final Map<UUID, Boolean> playerInSlowChunk = new HashMap<>();
    
    // Threshold for considering a chunk "slow" (in milliseconds)
    private static final double SLOW_CHUNK_THRESHOLD = 5.0;
    
    /**
     * Update player chunk position and notify if entering/leaving slow chunk
     */
    public static void updatePlayerChunk(ServerPlayer player) {
        if (player == null || player.level() == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        ChunkPos currentChunk = player.chunkPosition();
        ChunkPos lastChunk = playerLastChunk.get(playerId);
        
        // Check if player changed chunks
        if (lastChunk == null || !lastChunk.equals(currentChunk)) {
            playerLastChunk.put(playerId, currentChunk);
            
            // Check if the new chunk is slow
            try {
                LevelChunk chunk = player.level().getChunk(currentChunk.x, currentChunk.z);
                if (chunk instanceof ChunkTickDataAccessor accessor) {
                    double avgTickTime = accessor.world_balance$getAvgTickTime();
                    boolean isSlowChunk = avgTickTime > SLOW_CHUNK_THRESHOLD;
                    Boolean wasInSlowChunk = playerInSlowChunk.getOrDefault(playerId, false);
                    
                    if (isSlowChunk && !wasInSlowChunk) {
                        // Player entered a slow chunk
                        notifyPlayerSlowChunk(player, avgTickTime);
                        playerInSlowChunk.put(playerId, true);
                    } else if (!isSlowChunk && wasInSlowChunk) {
                        // Player left a slow chunk - no notification needed
                        playerInSlowChunk.put(playerId, false);
                    }
                }
            } catch (Exception e) {
                // Silently ignore errors
            }
        }
    }
    
    /**
     * Notify player they entered a slow chunk
     */
    private static void notifyPlayerSlowChunk(ServerPlayer player, double avgTickTime) {
        Component message = Component.literal(String.format("§c⚠ Slow Chunk (%.2fms avg)", avgTickTime));
        player.displayClientMessage(message, true); // true = action bar
    }
    
    /**
     * Remove player from tracking when they disconnect
     */
    public static void removePlayer(UUID playerId) {
        playerLastChunk.remove(playerId);
        playerInSlowChunk.remove(playerId);
    }
    
    /**
     * Clear all tracking data
     */
    public static void clear() {
        playerLastChunk.clear();
        playerInSlowChunk.clear();
    }
}