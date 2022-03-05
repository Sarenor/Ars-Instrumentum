package de.sarenor.arsinstrumentum.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CopyPasteSpellScroll extends ModItem implements ICasterTool {

    public static final String COPY_PASTE_SPELL_SCROLL = "copy_paste_spell_scroll";
    public static final String APPLIED_CONFIGURATION = "Applied Spell";

    public CopyPasteSpellScroll() {
        super((new Properties()).stacksTo(1).tab(ArsNouveau.itemGroup));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        ItemStack usedCopyPasteScroll = player.getItemInHand(handIn);
        if (!worldIn.isClientSide() && player.isShiftKeyDown()) {
            ItemStack offhand = player.getOffhandItem();
            if (offhand.getItem() instanceof ICasterTool offhandCasterTool) {
                ISpellCaster copyPasteSpellcaster = this.getSpellCaster(usedCopyPasteScroll);
                ISpellCaster offhandSpellcaster = offhandCasterTool.getSpellCaster(offhand);
                offhandSpellcaster.setSpell(copyPasteSpellcaster.getSpell());
                offhandSpellcaster.setColor(copyPasteSpellcaster.getColor());
                offhandSpellcaster.setSpellName(copyPasteSpellcaster.getSpellName());
                PortUtil.sendMessage(player, new TextComponent(APPLIED_CONFIGURATION));
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, usedCopyPasteScroll);
            } else {
                return new InteractionResultHolder<>(InteractionResult.PASS, usedCopyPasteScroll);
            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, usedCopyPasteScroll);
    }

    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack stack) {
        ItemStack heldStack = player.getItemInHand(handIn);
        ISpellCaster thisCaster = CasterUtil.getCaster(stack);
        if (!(heldStack.getItem() instanceof SpellBook) && !(heldStack.getItem() instanceof SpellParchment)) {
            return false;
        } else {
            Spell spell = new Spell();
            if (heldStack.getItem() instanceof ICasterTool) {
                ISpellCaster heldCaster = CasterUtil.getCaster(heldStack);
                spell = heldCaster.getSpell();
                thisCaster.setColor(heldCaster.getColor());
                thisCaster.setFlavorText(heldCaster.getFlavorText());
                thisCaster.setSpellName(heldCaster.getSpellName());
            }

            if (this.isScribedSpellValid(thisCaster, player, handIn, stack, spell)) {
                boolean success = this.setSpell(thisCaster, player, handIn, stack, spell);
                if (success) {
                    this.sendSetMessage(player);
                    return true;
                }
            } else {
                this.sendInvalidMessage(player);
            }

            return false;
        }
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public boolean shouldDisplay(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        ISpellCaster copyPasteSpellcaster = this.getSpellCaster(stack);
        tooltip2.add(new TextComponent("Inscribed Spell: " + copyPasteSpellcaster.getSpellName()));
        tooltip2.add(new TextComponent(copyPasteSpellcaster.getSpell().getDisplayString()));
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
