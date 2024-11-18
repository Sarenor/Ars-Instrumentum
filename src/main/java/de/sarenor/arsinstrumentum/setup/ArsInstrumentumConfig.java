package de.sarenor.arsinstrumentum.setup;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ArsInstrumentumConfig {

    public static final Common COMMON;
    public static final Client CLIENT;
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
        final Pair<Client, ModConfigSpec> specClientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specClientPair.getRight();
        CLIENT = specClientPair.getLeft();
    }

    public static class Common {
        public Common(ModConfigSpec.Builder builder) {

        }

    }

    public static class Client {

        public static ModConfigSpec.BooleanValue SHOW_MANA_NUM;
        public static ModConfigSpec.BooleanValue SHOW_MANA_ON_TOP;

        public Client(ModConfigSpec.Builder builder) {

            builder.push("Display mana amount numerical");
            SHOW_MANA_NUM = builder.comment("Display numbers").define("showNumericalManaBar", false);
            SHOW_MANA_ON_TOP = builder.comment("Display numbers above the bar instead of on it").define("displayAboveBar", false);
            builder.pop();

        }
    }

}
