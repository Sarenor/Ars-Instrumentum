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

import javax.annotation.Nullable;
import java.util.List;

import static de.sarenor.arsinstrumentum.utils.SerializationUtiils.deserializeBlockPosList;
import static de.sarenor.arsinstrumentum.utils.SerializationUtiils.serializeBlockPosList;

public class ScrollOfSaveStarbuncle extends ModItem {

    public static final String SCROLL_OF_SAVE_STARBUNCLE_ID = "scroll_of_save_starbuncle";
    public static final String SAVED_CONFIGURATION = "Saved Starbuncle configuration";
    public static final String APPLIED_CONFIGURATION = "Applied Starbuncle configuration";
    public static final String CLEARED_CONFIGURATION = "Cleared savedStarbuncle configuration";

    private static final String FROM_LIST = "from_list";
    private static final String TO_LIST = "to_list";
    private static final String BEHAVIOR_TAG = "behavior_tag";
    private static final String TOOLTIP = "tooltip";
    private static final String SCROLL_OF_SAVE_TAG_ID = "scroll_of_save_starbuncle_tag";

    public ScrollOfSaveStarbuncle() {
        super((new Properties()).stacksTo(1).tab(ArsNouveau.itemGroup));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack doNotUseStack, Player playerEntity, LivingEntity target, InteractionHand hand) {
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
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player playerEntity, InteractionHand hand) {
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
        configTag.put(FROM_LIST, serializeBlockPosList(starbuncle.data.FROM_LIST));
        configTag.put(TO_LIST, serializeBlockPosList(starbuncle.data.TO_LIST));
        configTag.put(BEHAVIOR_TAG, starbuncle.data.behaviorTag);
        configTag.putString(TOOLTIP, "Stored Config with " + starbuncle.data.FROM_LIST.size() + " Take-Locations and "
                + starbuncle.data.TO_LIST.size() + " Deposit-Locations");
        scrollTag.put(SCROLL_OF_SAVE_TAG_ID, configTag);
        scroll.setTag(scrollTag);
        PortUtil.sendMessage(player, Component.literal(SAVED_CONFIGURATION));
    }

    private void apply(ItemStack scroll, Starbuncle starbuncle, Player player) {
        CompoundTag scrollTag = scroll.getOrCreateTag();
        if (scrollTag.contains(SCROLL_OF_SAVE_TAG_ID)) {
            CompoundTag configTag = scrollTag.getCompound(SCROLL_OF_SAVE_TAG_ID);
            starbuncle.data.FROM_LIST = deserializeBlockPosList(configTag, FROM_LIST);
            starbuncle.data.TO_LIST = deserializeBlockPosList(configTag, TO_LIST);
            if (configTag.contains(BEHAVIOR_TAG)) {
                starbuncle.data.behaviorTag = configTag.getCompound(BEHAVIOR_TAG);
            }
            PortUtil.sendMessage(player, Component.literal(APPLIED_CONFIGURATION));
        }
    }

    private void clear(ItemStack scroll, Player player) {
        CompoundTag scrollTag = scroll.getOrCreateTag();
        scrollTag.remove(SCROLL_OF_SAVE_TAG_ID);
        scroll.setTag(scrollTag);
        PortUtil.sendMessage(player, Component.literal(CLEARED_CONFIGURATION));
    }


}
