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
import de.sarenor.arsinstrumentum.items.curios.armarium.WizardsArmarium;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;


public class Registration {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArsInstrumentum.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArsInstrumentum.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ArsInstrumentum.MODID);

    public static final String FAKE_WILDEN_TRIBUTE_ID = "fake_wilden_tribute";
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();

    public static final RegistryObject<Item> WIZARDS_ARMARIUM;
    public static final RegistryObject<Item> SCROLL_OF_SAVE_STARBUNCLE;
    public static final RegistryObject<Item> RUNIC_STORAGE_STONE;
    public static final RegistryObject<Item> COPY_PASTE_SPELL_SCROLL;
    public static final RegistryObject<Item> FAKE_WILDEN_TRIBUTE;
    public static final RegistryObject<Item> NUMERIC_CHARM;
    public static final RegistryObject<Block> ARCANE_APPLICATOR;
    public static final RegistryObject<Item> ARCANE_APPLICATOR_ITEM;
    public static final RegistryObject<BlockEntityType<ArcaneApplicatorTile>> ARCANE_APPLICATOR_TILE;


    static {
        ARCANE_APPLICATOR = BLOCKS.register(ArcaneApplicator.ARCANE_APPLICATOR_ID, ArcaneApplicator::new);
    }

    static {
        ARCANE_APPLICATOR_TILE = BLOCK_ENTITY_TYPES.register(ArcaneApplicatorTile.ARCANE_APPLICATOR_TILE_ID, () -> BlockEntityType.Builder.of(ArcaneApplicatorTile::new, ARCANE_APPLICATOR.get()).build(null));
    }

    static {
        WIZARDS_ARMARIUM = ITEMS.register(WizardsArmarium.WIZARDS_ARMARIUM_ID, WizardsArmarium::new);
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
    }

    public static <B extends Block> RegistryObject<Item> fromRendererBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new RendererBlockItem(block.get(), ITEM_PROPERTIES) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return ArcaneApplicatorRenderer::getISTER;
            }
        });
    }

}
