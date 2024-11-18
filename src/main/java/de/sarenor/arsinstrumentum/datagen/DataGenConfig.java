package de.sarenor.arsinstrumentum.datagen;

import de.sarenor.arsinstrumentum.ArsInstrumentum;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ArsInstrumentum.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenConfig {

    public static CompletableFuture<HolderLookup.Provider> provider;
    public static PackOutput output;

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        provider = event.getLookupProvider();
        output = generator.getPackOutput();
//        log.info("Ars Instrumentum: Data Generation started.");
        generator.addProvider(event.includeServer(), new ApparatusRecipes(generator));
        generator.addProvider(event.includeServer(), new Recipes(generator,provider));
        generator.addProvider(event.includeClient(), new LanguageProvider(generator, "en_us"));
        generator.addProvider(event.includeClient(), new ItemModels(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ImbuementRecipes(generator));
        generator.addProvider(event.includeServer(), new DefaultTableProvider(generator));
        generator.addProvider(event.includeServer(), new BlockstateProvider(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new BlockTagProvider(generator, event.getLookupProvider(), event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new AICurioProvider(output, existingFileHelper, provider));
        //        log.info("Ars Instrumentum: Data Generation ended.");
    }
}
