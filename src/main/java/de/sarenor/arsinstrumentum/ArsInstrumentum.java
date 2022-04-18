package de.sarenor.arsinstrumentum;

import de.sarenor.arsinstrumentum.network.Networking;
import de.sarenor.arsinstrumentum.setup.ArsInstrumentumConfig;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ArsInstrumentum.MODID)
public class ArsInstrumentum {
    public static final String MODID = "ars_instrumentum";
    public static ForgeConfigSpec SERVER_CONFIG;

    public ArsInstrumentum() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ArsInstrumentumConfig.CLIENT_SPEC);
        Registration.init(bus);
        bus.addListener(this::setup);
        bus.addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        Networking.registerMessages();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }
}
