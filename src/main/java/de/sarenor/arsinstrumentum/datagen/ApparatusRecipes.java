package de.sarenor.arsinstrumentum.datagen;

import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeBuilder;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ApparatusRecipes extends ApparatusRecipeProvider {

    private final DataGenerator generator;

    public ApparatusRecipes(DataGenerator generatorIn) {
        super(generatorIn);
        this.generator = generatorIn;
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_instrumentum/recipe/apparatus/" + str + ".json");
    }

    @Override
    public void collectJsons(CachedOutput cache) {
        //log.info("ArsInstrumentum: Recipe-Generation started");
        addEntries();
        //log.info("ArsInstrumentum: Recipe-Generation ended");
    }

    public void addEntries() {
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(Registration.WIZARDS_ARMARIUM.get())
                .withReagent(ItemsRegistry.MUNDANE_BELT)
                .withPedestalItem(2, ItemsRegistry.MAGE_FIBER)
                .withPedestalItem(2, Items.BLAZE_ROD)
                .withPedestalItem(2, Items.DIAMOND)
                .withPedestalItem(Items.ENDER_CHEST)
                .withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                .build());

        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.BATTLEMAGE_HOOD)
                .withReagent(ItemsRegistry.ARCANIST_HOOD)
                .withPedestalItem(1, Items.DIAMOND_HELMET)
                .keepNbtOfReagent(true)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "battlemage_hood_from_arcanist"))
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.BATTLEMAGE_ROBES)
                .withReagent(ItemsRegistry.ARCANIST_ROBES)
                .withPedestalItem(1, Items.DIAMOND_CHESTPLATE)
                .keepNbtOfReagent(true)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "battlemage_robes_from_arcanist"))
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.BATTLEMAGE_LEGGINGS)
                .withReagent(ItemsRegistry.ARCANIST_LEGGINGS)
                .withPedestalItem(1, Items.DIAMOND_LEGGINGS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "battlemage_leggings_from_arcanist"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.BATTLEMAGE_BOOTS)
                .withReagent(ItemsRegistry.ARCANIST_BOOTS)
                .withPedestalItem(1, Items.DIAMOND_BOOTS)
                .keepNbtOfReagent(true)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "battlemage_boots_from_arcanist"))
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.BATTLEMAGE_HOOD)
                .withReagent(ItemsRegistry.SORCERER_HOOD)
                .withPedestalItem(1, Items.DIAMOND_HELMET)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "battlemage_hood_from_sorcerer"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.BATTLEMAGE_ROBES)
                .withReagent(ItemsRegistry.SORCERER_ROBES)
                .withPedestalItem(1, Items.DIAMOND_CHESTPLATE)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "battlemage_robes_from_sorcerer"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.BATTLEMAGE_LEGGINGS)
                .withReagent(ItemsRegistry.SORCERER_LEGGINGS)
                .withPedestalItem(1, Items.DIAMOND_LEGGINGS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "battlemage_leggings_from_sorcerer"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.BATTLEMAGE_BOOTS)
                .withReagent(ItemsRegistry.SORCERER_BOOTS)
                .withPedestalItem(1, Items.DIAMOND_BOOTS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "battlemage_boots_from_sorcerer"))
                .keepNbtOfReagent(true)
                .build());


        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.ARCANIST_HOOD)
                .withReagent(ItemsRegistry.SORCERER_HOOD)
                .withPedestalItem(1, Items.IRON_HELMET)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "arcanist_hood_from_sorcerer"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.ARCANIST_ROBES)
                .withReagent(ItemsRegistry.SORCERER_ROBES)
                .withPedestalItem(1, Items.IRON_CHESTPLATE)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "arcanist_robes_from_sorcerer"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.ARCANIST_LEGGINGS)
                .withReagent(ItemsRegistry.SORCERER_LEGGINGS)
                .withPedestalItem(1, Items.IRON_LEGGINGS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "arcanist_leggings_from_sorcerer"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.ARCANIST_BOOTS)
                .withReagent(ItemsRegistry.SORCERER_BOOTS)
                .withPedestalItem(1, Items.IRON_BOOTS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "arcanist_boots_from_sorcerer"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.ARCANIST_HOOD)
                .withReagent(ItemsRegistry.BATTLEMAGE_HOOD)
                .withPedestalItem(1, Items.IRON_HELMET)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "arcanist_hood_from_battlemage"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.ARCANIST_ROBES)
                .withReagent(ItemsRegistry.BATTLEMAGE_ROBES)
                .withPedestalItem(1, Items.IRON_CHESTPLATE)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "arcanist_robes_from_battlemage"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.ARCANIST_LEGGINGS)
                .withReagent(ItemsRegistry.BATTLEMAGE_LEGGINGS)
                .withPedestalItem(1, Items.IRON_LEGGINGS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "arcanist_leggings_from_battlemage"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.ARCANIST_BOOTS)
                .withReagent(ItemsRegistry.BATTLEMAGE_BOOTS)
                .withPedestalItem(1, Items.IRON_BOOTS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "arcanist_boots_from_battlemage"))
                .keepNbtOfReagent(true)
                .build());

        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.SORCERER_HOOD)
                .withReagent(ItemsRegistry.ARCANIST_HOOD)
                .withPedestalItem(1, Items.GOLDEN_HELMET)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "sorcerer_hood_from_arcanist"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.SORCERER_ROBES)
                .withReagent(ItemsRegistry.ARCANIST_ROBES)
                .withPedestalItem(1, Items.GOLDEN_CHESTPLATE)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "sorcerer_robes_from_arcanist"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.SORCERER_LEGGINGS)
                .withReagent(ItemsRegistry.ARCANIST_LEGGINGS)
                .withPedestalItem(1, Items.GOLDEN_LEGGINGS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "sorcerer_leggings_from_arcanist"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.SORCERER_BOOTS)
                .withReagent(ItemsRegistry.ARCANIST_BOOTS)
                .withPedestalItem(1, Items.GOLDEN_BOOTS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "sorcerer_boots_from_arcanist"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.SORCERER_HOOD)
                .withReagent(ItemsRegistry.BATTLEMAGE_HOOD)
                .withPedestalItem(1, Items.GOLDEN_HELMET)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "sorcerer_hood_from_battlemage"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.SORCERER_ROBES)
                .withReagent(ItemsRegistry.BATTLEMAGE_ROBES)
                .withPedestalItem(1, Items.GOLDEN_CHESTPLATE)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "sorcerer_robes_from_battlemage"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.SORCERER_LEGGINGS)
                .withReagent(ItemsRegistry.BATTLEMAGE_LEGGINGS)
                .withPedestalItem(1, Items.GOLDEN_LEGGINGS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "sorcerer_leggings_from_battlemage"))
                .keepNbtOfReagent(true)
                .build());
        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(ItemsRegistry.SORCERER_BOOTS)
                .withReagent(ItemsRegistry.BATTLEMAGE_BOOTS)
                .withPedestalItem(1, Items.GOLDEN_BOOTS)
                .withId(ResourceLocation.fromNamespaceAndPath(ArsInstrumentum.MODID, "sorcerer_boots_from_battlemage"))
                .keepNbtOfReagent(true)
                .build());

        this.recipes.add(ApparatusRecipeBuilder.builder()
                .withResult(Registration.NUMERIC_CHARM.get())
                .withReagent(ItemsRegistry.DULL_TRINKET)
                .withPedestalItem(2, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .withPedestalItem(1, Items.DIAMOND)
                .withPedestalItem(Items.INK_SAC)
                .withPedestalItem(Items.PAPER)
                .build());
    }

    @Override
    public @NotNull String getName() {
        return "Ars Instrumentum Apparatus";
    }
}
