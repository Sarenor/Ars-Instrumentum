package de.sarenor.arsinstrumentum.datagen;

import de.sarenor.arsinstrumentum.ArsInstrumentum;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockstateProvider extends BlockStateProvider {

    public BlockstateProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn.getPackOutput(), ArsInstrumentum.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        
    }
}
