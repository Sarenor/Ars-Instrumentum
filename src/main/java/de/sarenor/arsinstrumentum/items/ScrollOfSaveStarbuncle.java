package de.sarenor.arsinstrumentum.items;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.items.data.StarbuncleCharmData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.sarenor.arsinstrumentum.items.curios.armarium.ArmariumSlot;
import de.sarenor.arsinstrumentum.items.curios.armarium.ArmariumStorage;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ScrollOfSaveStarbuncle extends ModItem {

    public static final String SCROLL_OF_SAVE_STARBUNCLE_ID = "scroll_of_save_starbuncle";
    public static final String SAVED_CONFIGURATION = "Saved Starbuncle configuration";
    public static final String APPLIED_CONFIGURATION = "Applied Starbuncle configuration";
    public static final String CLEARED_CONFIGURATION = "Cleared savedStarbuncle configuration";

    public ScrollOfSaveStarbuncle() {
        super((new Properties()).stacksTo(1));
    }

    public static void apply(ItemStack scroll, Starbuncle starbuncle, Player player) {
        var data = scroll.get(Registration.SCROLL_OF_SAVE_STARBUNCLE_DATA);
        if (data != null) {
            starbuncle.data = data.data.mutable();
            // consider cherry-picking the data we want to restore
            starbuncle.restoreFromTag();
            if (player != null) {
                PortUtil.sendMessage(player, Component.literal(APPLIED_CONFIGURATION));
            }
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack doNotUseStack, Player playerEntity, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (playerEntity.level().isClientSide || hand != InteractionHand.MAIN_HAND) {
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
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        var data = stack.get(Registration.SCROLL_OF_SAVE_STARBUNCLE_DATA);
        if (data != null && data.tooltip.isPresent()) {
            tooltip.add(Component.literal(data.tooltip.get()));
        }
    }

    private void store(ItemStack scroll, Starbuncle starbuncle, Player player) {
        StarbuncleCharmData.Mutable data = starbuncle.data.immutable().mutable();
        data.cosmetic = null;
        scroll.set(Registration.SCROLL_OF_SAVE_STARBUNCLE_DATA, new Data(data.immutable(), Optional.empty()));
        PortUtil.sendMessage(player, Component.literal(SAVED_CONFIGURATION));
    }

    private void clear(ItemStack scroll, Player player) {
        scroll.remove(Registration.SCROLL_OF_SAVE_STARBUNCLE_DATA);
        PortUtil.sendMessage(player, Component.literal(CLEARED_CONFIGURATION));
    }

    public record Data(@NotNull StarbuncleCharmData data, @NotNull Optional<String> tooltip) {
        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StarbuncleCharmData.CODEC.fieldOf("data").forGetter(Data::data),
                Codec.STRING.optionalFieldOf("tooltip").forGetter(Data::tooltip)
        ).apply(instance, Data::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                StarbuncleCharmData.STREAM_CODEC, Data::data,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Data::tooltip,
                Data::new
        );
    }
}
