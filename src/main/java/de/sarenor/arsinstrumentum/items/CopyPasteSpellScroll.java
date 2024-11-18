package de.sarenor.arsinstrumentum.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CopyPasteSpellScroll extends ModItem implements ICasterTool {

    public static final String COPY_PASTE_SPELL_SCROLL = "copy_paste_spell_scroll";
    public static final String APPLIED_CONFIGURATION = "Applied Spell";

    public CopyPasteSpellScroll() {
        super((new Properties()).stacksTo(1).component(DataComponentRegistry.SPELL_CASTER.get(), new SpellCaster()));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player player, @NotNull InteractionHand handIn) {
        ItemStack usedCopyPasteScroll = player.getItemInHand(handIn);
        if (!worldIn.isClientSide() && player.isShiftKeyDown()) {
            ItemStack offhand = player.getOffhandItem();
            if (offhand.getItem() instanceof ICasterTool offhandCasterTool) {
                AbstractCaster<? extends AbstractCaster<?>> copyPasteSpellcaster = this.getSpellCaster(usedCopyPasteScroll);
                AbstractCaster<? extends AbstractCaster<?>> offhandSpellcaster = offhandCasterTool.getSpellCaster(offhand);
                if (copyPasteSpellcaster == null || offhandSpellcaster == null) {
                    return new InteractionResultHolder<>(InteractionResult.PASS, usedCopyPasteScroll);
                }
                offhandSpellcaster.setSpell(copyPasteSpellcaster.getSpell()).setColor(copyPasteSpellcaster.getColor()).setSpellName(copyPasteSpellcaster.getSpellName()).saveToStack(offhand);
                PortUtil.sendMessage(player, Component.literal(APPLIED_CONFIGURATION));
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, usedCopyPasteScroll);
            } else {
                return new InteractionResultHolder<>(InteractionResult.PASS, usedCopyPasteScroll);
            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, usedCopyPasteScroll);
    }

    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack stack) {
        ItemStack heldStack = player.getItemInHand(handIn);
        AbstractCaster<? extends AbstractCaster<?>> thisCaster = SpellCasterRegistry.from(stack);
        if (thisCaster == null || !(heldStack.getItem() instanceof SpellBook) && !(heldStack.getItem() instanceof SpellParchment)) {
            return false;
        } else {
            Spell spell = new Spell();
            if (heldStack.getItem() instanceof ICasterTool) {
                AbstractCaster<? extends AbstractCaster<?>> heldCaster = SpellCasterRegistry.from(heldStack);
                if (heldCaster == null) {
                    return false;
                }
                thisCaster.setSpell(heldCaster.getSpell()).setColor(heldCaster.getColor()).setFlavorText(heldCaster.getFlavorText()).setSpellName(heldCaster.getSpellName()).saveToStack(stack);
            }

            if (this.isScribedSpellValid(thisCaster, player, handIn, stack, spell)) {
                thisCaster.setSpell(spell, thisCaster.getCurrentSlot());
                this.sendSetMessage(player);
                return true;
            } else {
                this.sendInvalidMessage(player);
            }

            return false;
        }
    }

    @Override
    public boolean doesSneakBypassUse(@NotNull ItemStack stack, @NotNull LevelReader world, @NotNull BlockPos pos, @NotNull Player player) {
        return true;
    }

    @Override
    public boolean shouldDisplay(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        stack.addToTooltip(DataComponentRegistry.SPELL_CASTER, context, tooltip2::add, flagIn);
        super.appendHoverText(stack, context, tooltip2, flagIn);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        if (caster != null && Config.GLYPH_TOOLTIPS.get() && !Screen.hasShiftDown() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }
}
