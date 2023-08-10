package de.sarenor.arsinstrumentum.datagen;

import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DefaultTableProvider extends LootTableProvider {
    public DefaultTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn.getPackOutput(), new HashSet<>(), List.of(new LootTableProvider.SubProviderEntry(BlockLootTable::new, LootContextParamSets.BLOCK)));
    }


    public static class BlockLootTable extends BlockLootSubProvider {
        public List<Block> list = new ArrayList<>();

        protected BlockLootTable() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), new HashMap<>());
        }

        @Override
        protected void generate() {
            registerDropSelf(Registration.ARCANE_APPLICATOR.get());
        }

        public void registerDropSelf(Block block) {
            list.add(block);
            dropSelf(block);
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return list;
        }

    }

}
