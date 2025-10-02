package igentuman.world_balance.block;

import igentuman.world_balance.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SlowBlockEntity extends BlockEntity {
    
    public SlowBlockEntity(BlockPos pos, BlockState state) {
        super(Main.SLOW_BLOCK_ENTITY.get(), pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        // Simulate slow chunk by scanning 50x50x50 blocks
        int radius = 50; // 50/2 = 25 blocks in each direction
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos scanPos = pos.offset(x, y, z);
                    // Get the blockstate at this position
                    // This operation is intentionally slow to simulate chunk lag
                    BlockState scannedState = level.getBlockState(scanPos);
                    
                    // You can add additional operations here if needed
                    // For example: scannedState.getBlock(), scannedState.isAir(), etc.
                }
            }
        }
    }
}
