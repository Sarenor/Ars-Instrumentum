package de.sarenor.arsinstrumentum.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultTableProvider extends LootTableProvider {
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables = ImmutableList.of(
            Pair.of(BlockLootTable::new, LootContextParamSets.BLOCK)
    );

    public DefaultTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return tables;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach((p_218436_2_, p_218436_3_) -> {
            LootTables.validate(validationtracker, p_218436_2_, p_218436_3_);
        });
    }

    public static class BlockLootTable extends BlockLoot {
        public List<Block> list = new ArrayList<>();

        @Override
        protected void addTables() {
            registerDropSelf(Registration.ARCANE_APPLICATOR.get());
        }

        public void registerDropSelf(Block block) {
            list.add(block);
            dropSelf(block);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return list;
        }

    }

}
