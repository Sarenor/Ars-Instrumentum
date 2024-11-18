package de.sarenor.arsinstrumentum.setup;

import com.hollingsworth.arsnouveau.common.items.RendererBlockItem;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.blocks.ArcaneApplicator;
import de.sarenor.arsinstrumentum.blocks.tiles.ArcaneApplicatorTile;
import de.sarenor.arsinstrumentum.client.renderer.tile.ArcaneApplicatorRenderer;
import de.sarenor.arsinstrumentum.items.CopyPasteSpellScroll;
import de.sarenor.arsinstrumentum.items.RunicStorageStone;
import de.sarenor.arsinstrumentum.items.ScrollOfSaveStarbuncle;
import de.sarenor.arsinstrumentum.items.curios.NumericCharm;
import de.sarenor.arsinstrumentum.items.curios.armarium.ArmariumStorage;
import de.sarenor.arsinstrumentum.items.curios.armarium.WizardsArmarium;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class Registration {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(ArsInstrumentum.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(ArsInstrumentum.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ArsInstrumentum.MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ArsInstrumentum.MODID);
    public static final String FAKE_WILDEN_TRIBUTE_ID = "fake_wilden_tribute";
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();

    public static final DeferredHolder<Item, Item> WIZARDS_ARMARIUM;
    public static final DeferredHolder<Item, Item> SCROLL_OF_SAVE_STARBUNCLE ;
    public static final DeferredHolder<Item, Item> RUNIC_STORAGE_STONE;
    public static final DeferredHolder<Item, Item> COPY_PASTE_SPELL_SCROLL;
    public static final DeferredHolder<Item, Item> FAKE_WILDEN_TRIBUTE;
    public static final DeferredHolder<Item, Item> NUMERIC_CHARM;
    public static final DeferredHolder<Block, Block> ARCANE_APPLICATOR;
    public static final DeferredHolder<Item, Item> ARCANE_APPLICATOR_ITEM;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcaneApplicatorTile>> ARCANE_APPLICATOR_TILE;

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ArmariumStorage>> ARMARIUM_STORAGE;

    // DATA COMPONENTS
    static {
        ARMARIUM_STORAGE = DATA_COMPONENTS.register("armarium_storage", () -> DataComponentType.<ArmariumStorage>builder().persistent(ArmariumStorage.CODEC.codec()).networkSynchronized(ArmariumStorage.STREAM_CODEC).build());
    }

    // BLOCKS
    static {
        ARCANE_APPLICATOR = BLOCKS.register(ArcaneApplicator.ARCANE_APPLICATOR_ID, ArcaneApplicator::new);
    }

    // BLOCK ENTITY TYPES
    static {
        ARCANE_APPLICATOR_TILE = BLOCK_ENTITY_TYPES.register(ArcaneApplicatorTile.ARCANE_APPLICATOR_TILE_ID, () -> BlockEntityType.Builder.of(ArcaneApplicatorTile::new, ARCANE_APPLICATOR.get()).build(null));
    }

    // ITEMS
    static {
        WIZARDS_ARMARIUM = ITEMS.register(WizardsArmarium.WIZARDS_ARMARIUM_ID, () -> new WizardsArmarium(ITEM_PROPERTIES.stacksTo(1)));
        SCROLL_OF_SAVE_STARBUNCLE = ITEMS.register(ScrollOfSaveStarbuncle.SCROLL_OF_SAVE_STARBUNCLE_ID, ScrollOfSaveStarbuncle::new);
        RUNIC_STORAGE_STONE = ITEMS.register(RunicStorageStone.RUNIC_STORAGE_STONE_ID, RunicStorageStone::new);
        COPY_PASTE_SPELL_SCROLL = ITEMS.register(CopyPasteSpellScroll.COPY_PASTE_SPELL_SCROLL, CopyPasteSpellScroll::new);
        FAKE_WILDEN_TRIBUTE = ITEMS.register(FAKE_WILDEN_TRIBUTE_ID, () -> new Item(ITEM_PROPERTIES));
        NUMERIC_CHARM = ITEMS.register("numeric_mana_charm", () -> new NumericCharm(ITEM_PROPERTIES.stacksTo(1)));
        ARCANE_APPLICATOR_ITEM = fromRendererBlock(ARCANE_APPLICATOR);
    }


    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
        DATA_COMPONENTS.register(bus);
        bus.addListener(Registration::attachCapabilities);
    }

    public static <B extends Block> DeferredHolder<Item, Item> fromRendererBlock(DeferredHolder<Block, B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new RendererBlockItem(block.get(), ITEM_PROPERTIES) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return ArcaneApplicatorRenderer::getISTER;
            }
        });
    }

    //register capabilities
    public static void attachCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ARCANE_APPLICATOR_TILE.get(), (applicatorTile, direction) -> new InvWrapper(applicatorTile));
    }

}
