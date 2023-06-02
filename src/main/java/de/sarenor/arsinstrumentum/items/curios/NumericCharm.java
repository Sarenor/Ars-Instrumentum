package de.sarenor.arsinstrumentum.items.curios;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import de.sarenor.arsinstrumentum.setup.ArsInstrumentumConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public class NumericCharm extends ArsNouveauCurio {
    public NumericCharm(Properties pProperties) {
        super(pProperties);
    }

    public static final String TOOLTIP_MESSAGE = "ars_instrumentum.tooltip.mana_cost";


    @SuppressWarnings("ConstantConditions")
    public static boolean hasCharm(Player player) {
        if (ArsInstrumentumConfig.Client.SHOW_MANA_NUM.get()) return true;
        IItemHandlerModifiable items = CuriosApi.getCuriosHelper().getEquippedCurios(player).orElse(null);
        //Blame Bailey for this @NotNull infringement
        if (items != null) {
            for (int i = 0; i < items.getSlots(); ++i) {
                Item item = items.getStackInSlot(i).getItem();
                if (item instanceof NumericCharm) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        ArsNouveauAPI.ENABLE_DEBUG_NUMBERS = false; //reset the switch. will be set to true when the overlay check succeeds
    }
}
