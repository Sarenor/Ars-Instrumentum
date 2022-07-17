package de.sarenor.arsinstrumentum.items.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.minecraft.world.item.Item.Properties;

public class NumericCharm extends Item implements ICurioItem {
    public NumericCharm(Properties pProperties) {
        super(pProperties);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean hasCharm(Player player) {

        IItemHandlerModifiable items = CuriosApi.getCuriosHelper().getEquippedCurios(player).orElse(null);
        //Blame Bailey for this @NotNull infringement
        if (items != null) {
            for(int i = 0; i < items.getSlots(); ++i) {
                Item item = items.getStackInSlot(i).getItem();
                if (item instanceof NumericCharm) {
                    return true;
                }
            }
        }
        return false;
    }

}
