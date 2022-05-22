package de.sarenor.arsinstrumentum.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.items.curios.NumericCharm;
import de.sarenor.arsinstrumentum.setup.ArsInstrumentumConfig.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @implNote Credits to Moonwolf287 for original implementation
 * @link <a href="https://github.com/Moonwolf287/ArsEnderStorage/blob/1.16.5/src/main/java/io/github/moonwolf287/ars_enderstorage/ManaTextGUI.java">Original implementation in 1.16.5</a>
 */
@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsInstrumentum.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NumericManaHUD extends GuiComponent {
    private static final Minecraft minecraft = Minecraft.getInstance();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderSpellHUD(final RenderGameOverlayEvent.Post event) {
        Player player = minecraft.player;
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || player == null) {
            return;
        }
        if (NumericCharm.hasCharm(player) || Boolean.TRUE.equals(Client.SHOW_MANA_NUM.get())) {
            drawHUD(event.getMatrixStack(), player);
        }
    }

    public static boolean shouldDisplayBar() {
        if (minecraft.player == null) return false;
        ItemStack mainHand = minecraft.player.getMainHandItem();
        ItemStack offHand = minecraft.player.getOffhandItem();
        return mainHand.getItem() instanceof IDisplayMana item && item.shouldDisplay(mainHand) || offHand.getItem() instanceof IDisplayMana item2 && item2.shouldDisplay(offHand);
    }

    private static void drawHUD(PoseStack ms, Player player) {

        IManaCap mana = CapabilityRegistry.getMana(player).orElse(null);

        if (!shouldDisplayBar() || mana == null) {
            return;
        }

        boolean renderOnTop = Client.SHOW_MANA_ON_TOP.get();

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

        drawString(ms, minecraft.font, text, offsetLeft, height, 0xFFFFFF);
        if (!renderOnTop) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_border" + ".png"));
            blit(ms, 10, height - 8, 0, 18, 108, 20, 256, 256);
        }

    }

}
