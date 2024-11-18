package de.sarenor.arsinstrumentum.datagen;

import com.google.common.base.Preconditions;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), ArsInstrumentum.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //log.info("ArsInstrumentum: RegisterItemModels started");
        for (DeferredHolder<Item, ? extends Item> item : Registration.ITEMS.getEntries()) {
            if (item != Registration.ARCANE_APPLICATOR_ITEM) {
                try {
                    getBuilder(getRegistryName(item.get()).getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(item.get()));
                } catch (Exception e) {
                    System.out.println("No texture for " + item.toString());
                }
            }
        }
        //log.info("ArsInstrumentum: RegisterItemModels ended");
    }

    private ResourceLocation itemTexture(final Item item) {
        final ResourceLocation name = registryName(item);
        return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), "item" + "/" + name.getPath());
    }

    private ResourceLocation registryName(final Item item) {
        return Preconditions.checkNotNull(getRegistryName(item), "Item %s has a null registry name", item);
    }

    @Override
    public String getName() {
        return "Ars Instrumentum Item Models";
    }
}
