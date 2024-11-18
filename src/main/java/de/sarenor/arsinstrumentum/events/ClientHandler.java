package de.sarenor.arsinstrumentum.events;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.GuiEntityInfoHUD;
import com.hollingsworth.arsnouveau.client.gui.GuiManaHUD;
import com.hollingsworth.arsnouveau.client.gui.GuiSpellHUD;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.client.NumericManaHUD;
import de.sarenor.arsinstrumentum.client.renderer.tile.ArcaneApplicatorRenderer;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT, modid = ArsInstrumentum.MODID, bus = EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.ARCANE_APPLICATOR_TILE.get(), p_i226006_1_ -> new ArcaneApplicatorRenderer());
    }

    @SubscribeEvent
    public static void registerOverlays(final RegisterGuiLayersEvent event) {
        event.registerAbove(ArsNouveau.prefix("mana_hud"), ArsNouveau.prefix("mana_numbers"), NumericManaHUD.OVERLAY);
    }

}
