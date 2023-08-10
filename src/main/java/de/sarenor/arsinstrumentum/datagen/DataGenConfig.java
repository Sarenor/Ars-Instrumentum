package de.sarenor.arsinstrumentum.datagen;

import de.sarenor.arsinstrumentum.ArsInstrumentum;
import lombok.extern.log4j.Log4j2;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsInstrumentum.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Log4j2
public class DataGenConfig {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        log.info("Ars Instrumentum: Data Generation started.");
        generator.addProvider(event.includeServer(), new ApparatusRecipes(generator));
        generator.addProvider(event.includeServer(), new Recipes(generator));
        generator.addProvider(event.includeClient(), new LanguageProvider(generator, "en_us"));
        generator.addProvider(event.includeClient(), new ItemModels(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ImbuementRecipes(generator));
        generator.addProvider(event.includeServer(), new DefaultTableProvider(generator));
        generator.addProvider(event.includeServer(), new BlockstateProvider(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new BlockTagProvider(generator, event.getLookupProvider(), event.getExistingFileHelper()));
        log.info("Ars Instrumentum: Data Generation ended.");
    }
}
