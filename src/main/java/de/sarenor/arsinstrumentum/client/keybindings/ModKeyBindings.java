package de.sarenor.arsinstrumentum.client.keybindings;

import com.mojang.blaze3d.platform.InputConstants;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;


@EventBusSubscriber(modid = ArsInstrumentum.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {

    public static final String SWITCH_ARMARIUM_SLOT_ID = "key.ars_nouveau.switch_armarium_slot";
    public static final String CHOOSE_ARMARIUM_SLOT_ID = "key.ars_nouveau.choose_armarium_slot";
    private static final String CATEGORY = "key.category.ars_nouveau.general";
    public static final KeyMapping SWITCH_ARMARIUM_SLOT = new KeyMapping(SWITCH_ARMARIUM_SLOT_ID, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
    public static final KeyMapping CHOOSE_ARMARIUM_SLOT = new KeyMapping(CHOOSE_ARMARIUM_SLOT_ID, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY);

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(SWITCH_ARMARIUM_SLOT);
        event.register(CHOOSE_ARMARIUM_SLOT);
    }
}