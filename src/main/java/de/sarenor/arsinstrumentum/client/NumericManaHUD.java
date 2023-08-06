package de.sarenor.arsinstrumentum.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.items.curios.NumericCharm;
import de.sarenor.arsinstrumentum.setup.ArsInstrumentumConfig.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hollingsworth.arsnouveau.client.gui.GuiManaHUD.shouldDisplayBar;

/**
 * @implNote Credits to Moonwolf287 for original implementation
 * @link <a href="https://github.com/Moonwolf287/ArsEnderStorage/blob/1.16.5/src/main/java/io/github/moonwolf287/ars_enderstorage/ManaTextGUI.java">Original implementation in 1.16.5</a>
 */
@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsInstrumentum.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NumericManaHUD {
    private static final Minecraft minecraft = Minecraft.getInstance();

    private static final ResourceLocation hudLoc = new ResourceLocation(ArsNouveau.MODID, "mana_hud");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderSpellHUD(final RenderGuiOverlayEvent.Post event) {
        Player player = minecraft.player;
        if (player == null || !event.getOverlay().id().equals(hudLoc)) {
            return;
        }
        if (NumericCharm.hasCharm(player))
            if (Client.SHOW_MANA_ON_TOP.get()) {
                drawTopHUD(event.getGuiGraphics(), player);
            }else {
                ArsNouveauAPI.ENABLE_DEBUG_NUMBERS = true;
            }
    }

    private static void drawTopHUD(GuiGraphics gg, Player player) {

        IManaCap mana = CapabilityRegistry.getMana(player).orElse(null);

        if (!shouldDisplayBar() || mana == null) {
            return;
        }

        ArsNouveauAPI.ENABLE_DEBUG_NUMBERS = false; //to hide on-bar numbers
        final boolean renderOnTop = true; //we always get here with true

        int offsetLeft = 10;
        int height = minecraft.getWindow().getGuiScaledHeight() - 15;
        int max = mana.getMaxMana();
        int current = (int) mana.getCurrentMana();
        String delimiter = renderOnTop ? "/" : "   /   ";

        String textMax = max + delimiter + max;
        String text = current + delimiter + max;

        int maxWidth = minecraft.font.width(textMax);
        if (renderOnTop) {
            height -= 25;
        } else {
            offsetLeft = 67 - maxWidth / 2;
        }
        offsetLeft += maxWidth - minecraft.font.width(text);

        gg.drawString(minecraft.font, text, offsetLeft, height, 0xFFFFFF, false);
        if (!renderOnTop) {
            gg.blit(new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_border" + ".png"), 10, height - 8, 0, 18, 108, 20, 256, 256);
        }

    }

    @SubscribeEvent //to enable numbers on spellcraft
    public static void drawTopGui(ScreenEvent event){
        if (event.getScreen() instanceof GuiSpellBook && Client.SHOW_MANA_ON_TOP.get()){
            if (minecraft.player != null && NumericCharm.hasCharm(minecraft.player)) {
                ArsNouveauAPI.ENABLE_DEBUG_NUMBERS = true;
            }
        }
    }

}
