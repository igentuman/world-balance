package igentuman.world_balance;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Main.MODID)
public class Main
{
    public static final String MODID = "world_balance";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Main(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}
