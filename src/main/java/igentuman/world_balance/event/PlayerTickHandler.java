package igentuman.world_balance.event;

import igentuman.world_balance.util.BlockEntityLookTracker;
import igentuman.world_balance.util.PlayerChunkTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles player tick events to track chunk changes and block entity looks
 */
@Mod.EventBusSubscriber(modid = "world_balance")
public class PlayerTickHandler {
    
    private static int chunkCheckCounter = 0;
    private static int lookCheckCounter = 0;
    private static final int CHUNK_CHECK_INTERVAL = 20; // Check every 20 ticks (1 second)
    private static final int LOOK_CHECK_INTERVAL = 20; // Check every 10 ticks (0.5 seconds)
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Only process on server side and at the end of the tick
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        
        if (event.player instanceof ServerPlayer serverPlayer) {
            // Check chunk changes every second
            chunkCheckCounter++;
            if (chunkCheckCounter >= CHUNK_CHECK_INTERVAL) {
                chunkCheckCounter = 0;
                PlayerChunkTracker.updatePlayerChunk(serverPlayer);
            }
            
            // Check what block entity player is looking at more frequently
            lookCheckCounter++;
            if (lookCheckCounter >= LOOK_CHECK_INTERVAL) {
                lookCheckCounter = 0;
                BlockEntityLookTracker.updatePlayerLook(serverPlayer);
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerChunkTracker.removePlayer(event.getEntity().getUUID());
        BlockEntityLookTracker.removePlayer(event.getEntity().getUUID());
    }
}