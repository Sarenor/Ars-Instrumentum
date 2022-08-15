package de.sarenor.arsinstrumentum.datagen;

import de.sarenor.arsinstrumentum.ArsInstrumentum;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockstateProvider extends BlockStateProvider {


    public BlockstateProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, ArsInstrumentum.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        
    }
}
