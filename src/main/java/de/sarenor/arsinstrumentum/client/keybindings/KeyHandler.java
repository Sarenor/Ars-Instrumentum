package de.sarenor.arsinstrumentum.client.keybindings;

import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.common.network.Networking;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.items.curios.armarium.WizardsArmarium;
import de.sarenor.arsinstrumentum.network.WizardsArmariumSwitchMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import top.theillusivec4.curios.api.CuriosApi;

import static de.sarenor.arsinstrumentum.setup.Registration.WIZARDS_ARMARIUM;

@EventBusSubscriber(value = Dist.CLIENT, modid = ArsInstrumentum.MODID)
public class KeyHandler {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    public static void checkKeysPressed(int key) {
        Player player = MINECRAFT.player;
        if (player != null) {
            if (key == ModKeyBindings.CHOOSE_ARMARIUM_SLOT.getKey().getValue()) {
                if (MINECRAFT.screen instanceof GuiRadialMenu) {
                    MINECRAFT.player.closeContainer();
                    return;
                }
            }
            if (key == ModKeyBindings.SWITCH_ARMARIUM_SLOT.getKey().getValue()) {
                if (CuriosApi.getCuriosHelper().findEquippedCurio(WIZARDS_ARMARIUM.get(), player).isPresent()) {
                    Networking.sendToServer(new WizardsArmariumSwitchMessage());
                }
            }
            if (key == ModKeyBindings.CHOOSE_ARMARIUM_SLOT.getKey().getValue()) {
                if (CuriosApi.getCuriosHelper().findEquippedCurio(WIZARDS_ARMARIUM.get(), player).isPresent()) {
                    WizardsArmarium.openSwitchRadialMenu(player);
                }
            }
        }

    }

    @SubscribeEvent
    public static void mouseEvent(final InputEvent.MouseButton.Post event) {
        if (MINECRAFT.player == null || MINECRAFT.screen != null || event.getAction() != 1) {
            return;
        }
        checkKeysPressed(event.getButton());
    }

    @SubscribeEvent
    public static void keyEvent(final InputEvent.Key event) {
        if (MINECRAFT.player == null || MINECRAFT.screen != null || event.getAction() != 1) {
            return;
        }
        checkKeysPressed(event.getKey());

    }

}
