package de.sarenor.arsinstrumentum.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.items.CopyPasteSpellScroll;
import de.sarenor.arsinstrumentum.items.RunicStorageStone;
import de.sarenor.arsinstrumentum.items.ScrollOfSaveStarbuncle;
import de.sarenor.arsinstrumentum.items.curios.NumericCharm;
import de.sarenor.arsinstrumentum.items.curios.armarium.WizardsArmarium;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class Registration {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArsInstrumentum.MODID);

    public static final String FAKE_WILDEN_TRIBUTE_ID = "fake_wilden_tribute";

    public static final RegistryObject<Item> WIZARDS_ARMARIUM = ITEMS.register(WizardsArmarium.WIZARDS_ARMARIUM_ID, WizardsArmarium::new);
    public static final RegistryObject<Item> SCROLL_OF_SAVE_STARBUNCLE = ITEMS.register(ScrollOfSaveStarbuncle.SCROLL_OF_SAVE_STARBUNCLE_ID, ScrollOfSaveStarbuncle::new);
    public static final RegistryObject<Item> RUNIC_STORAGE_STONE = ITEMS.register(RunicStorageStone.RUNIC_STORAGE_STONE_ID, RunicStorageStone::new);
    public static final RegistryObject<Item> COPY_PASTE_SPELL_SCROLL = ITEMS.register(CopyPasteSpellScroll.COPY_PASTE_SPELL_SCROLL, CopyPasteSpellScroll::new);
    public static final RegistryObject<Item> FAKE_WILDEN_TRIBUTE = ITEMS.register(FAKE_WILDEN_TRIBUTE_ID, () -> new Item(new Item.Properties().tab(ArsNouveau.itemGroup)));

    public static final RegistryObject<Item> NUMERIC_CHARM; //choose one of the two styles, I usually divide to keep lines shorter

    static {
        NUMERIC_CHARM = ITEMS.register("numeric_mana_charm", () -> new NumericCharm(new Item.Properties().stacksTo(1).tab(ArsNouveau.itemGroup)));
    }

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
    }

}
