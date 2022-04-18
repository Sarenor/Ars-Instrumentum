package de.sarenor.arsinstrumentum.setup;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber
public class ArsInstrumentumConfig {

    public static final Common COMMON;
    public static final Client CLIENT;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
        final Pair<Client,ForgeConfigSpec> specClientPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specClientPair.getRight();
        CLIENT = specClientPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
    }

    public static class Common{
        public Common(ForgeConfigSpec.Builder builder){

        }

    }

    public static class Client{

        public static ForgeConfigSpec.BooleanValue SHOW_MANA_NUM;
        public static ForgeConfigSpec.BooleanValue SHOW_MANA_ON_TOP;

        public Client(ForgeConfigSpec.Builder builder) {

            builder.push("Display mana amount numerical");
            SHOW_MANA_NUM = builder.comment("Display numbers").define("showManaNumerical", true);
            SHOW_MANA_ON_TOP = builder.comment("Display numbers above the bar instead of on it").define("displayAboveBar", false);
            builder.pop();

        }
    }

}
