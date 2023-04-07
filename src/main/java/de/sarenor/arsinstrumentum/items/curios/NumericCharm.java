package de.sarenor.arsinstrumentum.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

public class NumericCharm extends ArsNouveauCurio {
    public NumericCharm(Properties pProperties) {
        super(pProperties);
    }

    public static final String TOOLTIP_MESSAGE = "ars_instrumentum.tooltip.mana_cost";


    @SuppressWarnings("ConstantConditions")
    public static boolean hasCharm(Player player) {

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

}
