package de.sarenor.arsinstrumentum.setup;

import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.client.renderer.tile.ArcaneApplicatorRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ArsInstrumentum.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientListener {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.ARCANE_APPLICATOR_TILE.get(), ArcaneApplicatorRenderer::new);
    }

    @SubscribeEvent
    public static void registerRenderers(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(Registration.ARCANE_APPLICATOR.get(), RenderType.cutout());
    }
}
