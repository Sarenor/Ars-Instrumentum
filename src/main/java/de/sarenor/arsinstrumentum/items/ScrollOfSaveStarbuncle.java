package de.sarenor.arsinstrumentum.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ScrollOfSaveStarbuncle extends ModItem {

    public static final String SCROLL_OF_SAVE_STARBUNCLE_ID = "scroll_of_save_starbuncle";
    public static final String SAVED_CONFIGURATION = "Saved Starbuncle configuration";
    public static final String APPLIED_CONFIGURATION = "Applied Starbuncle configuration";
    public static final String CLEARED_CONFIGURATION = "Cleared savedStarbuncle configuration";

    private static final String DATA_TAG = "data_tag";
    private static final String TOOLTIP = "tooltip";
    private static final String SCROLL_OF_SAVE_TAG_ID = "scroll_of_save_starbuncle_tag";

    public ScrollOfSaveStarbuncle() {
        super((new Properties()).stacksTo(1).tab(ArsNouveau.itemGroup));
    }

    public static void apply(ItemStack scroll, Starbuncle starbuncle, Player player) {
        CompoundTag scrollTag = scroll.getOrCreateTag();
        if (scrollTag.contains(SCROLL_OF_SAVE_TAG_ID)) {
            CompoundTag configTag = scrollTag.getCompound(SCROLL_OF_SAVE_TAG_ID);
            if (configTag.contains(DATA_TAG)) {
                starbuncle.data = new Starbuncle.StarbuncleData(configTag.getCompound(DATA_TAG));
            }
            // consider cherry-picking the data we want to restore
            starbuncle.restoreFromTag();
            if (player != null) {
                PortUtil.sendMessage(player, Component.literal(APPLIED_CONFIGURATION));
            }
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack doNotUseStack, Player playerEntity, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (playerEntity.level.isClientSide || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ItemStack heldScroll = playerEntity.getItemInHand(hand);
        if (target instanceof Starbuncle starbuncle) {
            if (playerEntity.isShiftKeyDown()) {
                apply(heldScroll, starbuncle, playerEntity);
            } else {
                store(heldScroll, starbuncle, playerEntity);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player playerEntity, @NotNull InteractionHand hand) {
        if (playerEntity.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            clear(playerEntity.getItemInHand(hand), playerEntity);
        }
        return InteractionResultHolder.pass(playerEntity.getItemInHand(hand));
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag p_77624_4_) {
        CompoundTag scrollTag = stack.getOrCreateTag();
        if (scrollTag.contains(SCROLL_OF_SAVE_TAG_ID)) {
            tooltip.add(Component.literal(scrollTag.getCompound(SCROLL_OF_SAVE_TAG_ID).getString(TOOLTIP)));
        }
    }

    private void store(ItemStack scroll, Starbuncle starbuncle, Player player) {
        CompoundTag scrollTag = scroll.getOrCreateTag();
        CompoundTag configTag = new CompoundTag();
        Starbuncle.StarbuncleData data = starbuncle.data;
        CompoundTag starbyBehavior = data.toTag(starbuncle, new CompoundTag());
        //don't save cosmetic data
        starbyBehavior.remove("cosmetic");
        configTag.put(DATA_TAG, starbyBehavior);
        scrollTag.put(SCROLL_OF_SAVE_TAG_ID, configTag);
        scroll.setTag(scrollTag);
        PortUtil.sendMessage(player, Component.literal(SAVED_CONFIGURATION));
    }

    private void clear(ItemStack scroll, Player player) {
        CompoundTag scrollTag = scroll.getOrCreateTag();
        scrollTag.remove(SCROLL_OF_SAVE_TAG_ID);
        scroll.setTag(scrollTag);
        PortUtil.sendMessage(player, Component.literal(CLEARED_CONFIGURATION));
    }


}
