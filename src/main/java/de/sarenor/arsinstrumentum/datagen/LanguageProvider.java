package de.sarenor.arsinstrumentum.datagen;

import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.setup.Registration;
import lombok.extern.log4j.Log4j2;
import net.minecraft.data.DataGenerator;

import static de.sarenor.arsinstrumentum.client.keybindings.ModKeyBindings.CHOOSE_ARMARIUM_SLOT_ID;
import static de.sarenor.arsinstrumentum.client.keybindings.ModKeyBindings.SWITCH_ARMARIUM_SLOT_ID;

@Log4j2
public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {


    public LanguageProvider(DataGenerator gen, String locale) {
        super(gen, ArsInstrumentum.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        log.info("ArsInstrumentum: AddTranslation started");
        add(Registration.WIZARDS_ARMARIUM.get(), "Wizards Armarium");
        add(Registration.SCROLL_OF_SAVE_STARBUNCLE.get(), "Scroll of Save Starbuncle");
        add(Registration.RUNIC_STORAGE_STONE.get(), "Runic Stone of Storage");
        add(Registration.FAKE_WILDEN_TRIBUTE.get(), "Essence of Vanquished Foes");
        add(Registration.COPY_PASTE_SPELL_SCROLL.get(), "Scroll of Spelltransfer");
        add(SWITCH_ARMARIUM_SLOT_ID, "Switch Wizards Armarium");
        add(CHOOSE_ARMARIUM_SLOT_ID, "(Wizards Armarium) Toggle Selection HUD");
        log.info("ArsInstrumentum: AddTranslation ended");
    }
}
