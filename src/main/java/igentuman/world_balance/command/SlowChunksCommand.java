package igentuman.world_balance.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import igentuman.world_balance.util.ChunkTickUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Command to display the 10 slowest chunks on the server
 */
public class SlowChunksCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("wb")
                .requires(source -> source.hasPermission(2)) // Requires OP level 2
                .then(Commands.literal("slow_chunks")
                    .executes(SlowChunksCommand::execute)
                )
        );
    }
    
    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        // Collect all chunks from all dimensions
        List<ChunkData> allChunks = new ArrayList<>();
        
        for (ServerLevel level : source.getServer().getAllLevels()) {
            level.getChunkSource().chunkMap.getChunks().forEach(chunkHolder -> {
                LevelChunk chunk = chunkHolder.getTickingChunk();
                if (chunk != null) {
                    double avgTickTime = ChunkTickUtil.getAvgTickTime(chunk);
                    long tickCount = ChunkTickUtil.getTickCount(chunk);
                    
                    // Only include chunks that have been ticked at least once
                    if (tickCount > 0) {
                        ChunkPos pos = chunk.getPos();
                        allChunks.add(new ChunkData(
                            level.dimension().location().toString(),
                            pos.x,
                            pos.z,
                            avgTickTime,
                            tickCount,
                            ChunkTickUtil.isThrottled(chunk),
                            ChunkTickUtil.getThrottleRatio(chunk)
                        ));
                    }
                }
            });
        }
        
        // Sort by average tick time (descending)
        allChunks.sort(Comparator.comparingDouble(ChunkData::avgTickTime).reversed());
        
        // Take top 10
        List<ChunkData> slowest = allChunks.stream().limit(10).toList();
        
        if (slowest.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No chunk data available yet.").withStyle(ChatFormatting.YELLOW), false);
            return 0;
        }
        
        // Send header
        source.sendSuccess(() -> Component.literal("=== Top 10 Slowest Chunks ===").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        source.sendSuccess(() -> Component.literal(""), false);
        
        // Send each chunk's data
        int rank = 1;
        for (ChunkData data : slowest) {
            final int currentRank = rank;
            
            // Format: #1 [dimension] (x, z) - 25.3ms (1234 ticks) [THROTTLED 50%]
            source.sendSuccess(() -> {
                MutableComponent message = Component.literal("#" + currentRank + " ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("[" + data.dimension + "] ")
                        .withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("(" + data.chunkX + ", " + data.chunkZ + ") ")
                        .withStyle(ChatFormatting.WHITE))
                    .append(Component.literal("- " + String.format("%.2f", data.avgTickTime) + "ms ")
                        .withStyle(data.throttled ? ChatFormatting.RED : ChatFormatting.GREEN))
                    .append(Component.literal("(" + data.tickCount + " ticks)")
                        .withStyle(ChatFormatting.GRAY));
                
                if (data.throttled) {
                    message = message.append(Component.literal(" [THROTTLED " + String.format("%.0f", data.throttleRatio * 100) + "%]")
                        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                }
                
                return message;
            }, false);
            
            rank++;
        }
        
        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("Total chunks analyzed: " + allChunks.size()).withStyle(ChatFormatting.GRAY), false);
        
        return 1;
    }
    
    /**
     * Data class to hold chunk information
     */
    private record ChunkData(
        String dimension,
        int chunkX,
        int chunkZ,
        double avgTickTime,
        long tickCount,
        boolean throttled,
        double throttleRatio
    ) {}
}