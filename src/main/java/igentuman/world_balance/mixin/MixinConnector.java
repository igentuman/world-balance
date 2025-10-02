package igentuman.world_balance.mixin;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector  implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfiguration("world_balance.mixins.json");
    }
}