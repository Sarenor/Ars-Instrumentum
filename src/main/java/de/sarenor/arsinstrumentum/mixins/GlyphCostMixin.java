package de.sarenor.arsinstrumentum.mixins;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import de.sarenor.arsinstrumentum.items.curios.NumericCharm;
import de.sarenor.arsinstrumentum.setup.ArsInstrumentumConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ItemStack.class)
public class GlyphCostMixin {

    @OnlyIn(Dist.CLIENT)
    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    public void appendHoverText(Item instance, ItemStack stack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        instance.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);
        if (ArsNouveau.proxy.getMinecraft() == null) return;
        Player player = ArsNouveau.proxy.getPlayer();
        if (player == null) return;
        if (NumericCharm.hasCharm(player) || ArsInstrumentumConfig.Client.SHOW_MANA_NUM.get()) {
            int cost;
            if (instance instanceof Glyph glyph) cost = glyph.spellPart.getCastingCost();
            else if (instance instanceof ICasterTool casterTool) {
                var casterData = casterTool.getSpellCaster(stack);
                Spell spell = casterData.getSpell(casterData.getCurrentSlot());
                if (spell.isEmpty()) return;
                cost = spell.getDiscountedCost() - ManaUtil.getPlayerDiscounts(player, spell);
            } else if (stack.getEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()) > 0) {
                Spell casterData = new ReactiveCaster(stack).getSpell();
                if (casterData.isEmpty()) return;
                cost = casterData.getDiscountedCost() - ManaUtil.getPlayerDiscounts(player, casterData);
            } else return;

            pTooltipComponents.add(Component.translatable(NumericCharm.TOOLTIP_MESSAGE, cost).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE)).append(String.valueOf(cost)));
        }
    }
}
