package de.sarenor.arsinstrumentum.datagen;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ImbuementRecipeProvider;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.io.IOException;
import java.nio.file.Path;

public class ImbuementRecipes extends ImbuementRecipeProvider {
    public ImbuementRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    private static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_instrumentum/recipes/imbuement/" + str + ".json");
    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        this.recipes.add((new ImbuementRecipe("fake_wilden_tribute", Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND), new ItemStack(Registration.FAKE_WILDEN_TRIBUTE.get()), 5000)).withPedestalItem(ItemsRegistry.ARCHMAGE_SPELLBOOK).withPedestalItem(Items.NETHER_STAR).withPedestalItem(Items.TOTEM_OF_UNDYING));

        Path output = this.generator.getOutputFolder();

        for (ImbuementRecipe g : this.recipes) {
            Path path = getRecipePath(output, g.getId().getPath());
            DataProvider.saveStable(cache, g.asRecipe(), path);
        }

    }

    public String getName() {
        return "Imbuement";
    }
}
