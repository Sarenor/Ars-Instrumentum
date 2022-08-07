package de.sarenor.arsinstrumentum.datagen;

import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.items.RunicStorageStone;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        Block SOURCESTONE = BlockRegistry.getBlock(LibBlockNames.SOURCESTONE);

        ShapelessRecipeBuilder.shapeless(Registration.SCROLL_OF_SAVE_STARBUNCLE.get(), 2).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .requires(ItemsRegistry.BLANK_PARCHMENT)
                .requires(Ingredient.of(Tags.Items.NUGGETS_GOLD), 1)
                .requires(Items.INK_SAC)
                .requires(Items.FEATHER)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(Registration.RUNIC_STORAGE_STONE.get()).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .requires(ItemsRegistry.SOURCE_GEM)
                .requires(Items.LAPIS_LAZULI)
                .requires(Items.FLINT)
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(Registration.RUNIC_STORAGE_STONE.get()).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .requires(Registration.RUNIC_STORAGE_STONE.get())
                .save(consumer, ArsInstrumentum.MODID + ":" + RunicStorageStone.RUNIC_STORAGE_STONE_ALTERNATE_RECIPE_ID);

        ShapelessRecipeBuilder.shapeless(Registration.COPY_PASTE_SPELL_SCROLL.get()).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .requires(ItemsRegistry.BLANK_PARCHMENT)
                .requires(ItemsRegistry.SOURCE_GEM)
                .requires(Items.INK_SAC)
                .requires(Items.FEATHER)
                .save(consumer);


        ShapedRecipeBuilder.shaped(Registration.ARCANE_APPLICATOR_ITEM.get()).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK))
                .define('S', SOURCESTONE)
                .define('A', BlockRegistry.ARCHWOOD_SLABS)
                .define('G', Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .define('R', Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .pattern("GAG")
                .pattern(" S ")
                .pattern("SRS")
                .save(consumer);
    }
}
