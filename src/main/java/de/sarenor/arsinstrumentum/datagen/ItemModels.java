package de.sarenor.arsinstrumentum.datagen;

import com.google.common.base.Preconditions;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.setup.Registration;
import lombok.extern.log4j.Log4j2;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

@Log4j2
public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), ArsInstrumentum.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        log.info("ArsInstrumentum: RegisterItemModels started");
        for (RegistryObject<Item> item : Registration.ITEMS.getEntries()) {
            if (item != Registration.ARCANE_APPLICATOR_ITEM) {
                try {
                    getBuilder(getRegistryName(item.get()).getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", itemTexture(item.get()));
                } catch (Exception e) {
                    System.out.println("No texture for " + item.toString());
                }
            }
        }
        log.info("ArsInstrumentum: RegisterItemModels ended");
    }

    private ResourceLocation itemTexture(final Item item) {
        final ResourceLocation name = registryName(item);
        return new ResourceLocation(name.getNamespace(), "item" + "/" + name.getPath());
    }

    private ResourceLocation registryName(final Item item) {
        return Preconditions.checkNotNull(getRegistryName(item), "Item %s has a null registry name", item);
    }

}
