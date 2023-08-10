package de.sarenor.arsinstrumentum.datagen;

import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {

    public BlockTagProvider(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper) {
        super(generatorIn.getPackOutput(), lookupProvider, ArsInstrumentum.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                Registration.ARCANE_APPLICATOR.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_AXE).add(
                Registration.ARCANE_APPLICATOR.get()
        );
    }
}
