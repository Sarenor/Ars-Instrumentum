package de.sarenor.arsinstrumentum.client.keybindings;

import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.items.curios.armarium.WizardsArmarium;
import de.sarenor.arsinstrumentum.network.Networking;
import de.sarenor.arsinstrumentum.network.WizardsArmariumSwitchMessage;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import static de.sarenor.arsinstrumentum.setup.Registration.WIZARDS_ARMARIUM;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsInstrumentum.MODID)
@Log4j2
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
                    Networking.INSTANCE.sendToServer(new WizardsArmariumSwitchMessage());
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
