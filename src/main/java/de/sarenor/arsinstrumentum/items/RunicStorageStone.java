package de.sarenor.arsinstrumentum.items;

import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RelaySplitterTile;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.sarenor.arsinstrumentum.setup.Registration;
import de.sarenor.arsinstrumentum.utils.BlockPosUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static de.sarenor.arsinstrumentum.utils.SerializationUtils.*;

public class RunicStorageStone extends ModItem {
    public static final String RUNIC_STORAGE_STONE_ID = "runic_storage_stone";
    public static final String RUNIC_STORAGE_STONE_ALTERNATE_RECIPE_ID = "runic_storage_stone_alternate";
    public static final String SAVED_CONFIGURATION = "Saved Relay configuration";
    public static final String APPLIED_CONFIGURATION = "Applied Relay configuration";
    public static final String CANT_APPLY_EMPTY_CONFIGURATION = "Cannot apply empty configuration to Relay";
    public static final String CANT_APPLY_SPLITTER_CONFIGURATION_TO_ARCANE = "Cannot apply Splitter configuration to Arcane Relay";
    public static final String CANT_APPLY_ARCANE_CONFIGURATION_TO_SPLITTER = "Cannot apply Arcane configuration to Splitter Relay";

    public RunicStorageStone() {
        super((new Properties()).stacksTo(1));
    }

    public static void apply(ItemStack stone, RelayTile relayTile, Player player) {
        var data = stone.get(Registration.RUNIC_STORAGE_STONE_DATA);
        if (data != null) {
            if (data.relayType == RelayType.SPLITTER) {
                if (relayTile instanceof RelaySplitterTile relaySplitterTile) {
                    applySplitterRelay(data, relaySplitterTile, player);
                } else {
                    if (player != null) {
                        PortUtil.sendMessage(player, Component.literal(CANT_APPLY_SPLITTER_CONFIGURATION_TO_ARCANE));
                    }
                }
            } else {
                if (!(relayTile instanceof RelaySplitterTile)) {
                    applyArcaneRelay(data, relayTile, player);
                } else {
                    if (player != null) {
                        PortUtil.sendMessage(player, Component.literal(CANT_APPLY_ARCANE_CONFIGURATION_TO_SPLITTER));
                    }
                }
            }
        } else {
            if (player != null) {
                PortUtil.sendMessage(player, Component.literal(CANT_APPLY_EMPTY_CONFIGURATION));
            }
        }
    }

    private static void applySplitterRelay(Data data, RelaySplitterTile relaySplitterTile, Player player) {
        for (BlockPos to : data.to) {
            if (relaySplitterTile.closeEnough(to)) {
                relaySplitterTile.setSendTo(to);
            } else {
                PortUtil.sendMessage(player, Component.literal("Block " + BlockPosUtils.toString(to) + " is to far"
                                                               + " away from the Relay and could not be added to Deposit-Locations"));
            }
        }
        for (BlockPos from : data.from) {
            if (relaySplitterTile.closeEnough(from)) {
                relaySplitterTile.setTakeFrom(from);
            } else {
                PortUtil.sendMessage(player, Component.literal("Block " + BlockPosUtils.toString(from) + " is to far"
                                                               + " away from the Relay and could not be added to Take-Locations"));
            }
        }
        PortUtil.sendMessage(player, Component.literal(APPLIED_CONFIGURATION));
    }

    private static void applyArcaneRelay(Data data, RelayTile relayTile, Player player) {
        if (data.to.isEmpty()) {
            PortUtil.sendMessage(player, Component.literal("No Deposit-Location."
                                                           + " No configuration has been applied"));
            return;
        }

        if (data.from.isEmpty()) {
            PortUtil.sendMessage(player, Component.literal("No Take-Location."
                                                           + " No configuration has been applied"));
            return;
        }

        BlockPos to = data.to.getFirst();
        BlockPos from = data.from.getFirst();

        if (!relayTile.closeEnough(to)) {
            PortUtil.sendMessage(player, Component.literal("Deposit-Location " + BlockPosUtils.toString(to) + " is to far away."
                                                           + " No configuration has been applied"));
            return;
        }
        if (!relayTile.closeEnough(from)) {
            PortUtil.sendMessage(player, Component.literal("Take-Location " + BlockPosUtils.toString(from) + " is to far away."
                                                           + " No configuration has been applied"));
            return;
        }
        relayTile.setSendTo(to);
        relayTile.setTakeFrom(from);
        PortUtil.sendMessage(player, Component.literal(APPLIED_CONFIGURATION));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide || context.getPlayer() == null) {
            return super.useOn(context);
        }
        BlockPos pos = context.getClickedPos();
        Player playerEntity = context.getPlayer();
        ItemStack heldStone = context.getItemInHand();
        Level world = context.getLevel();


        if (world.getBlockEntity(pos) instanceof RelayTile relayTile) {
            if (playerEntity.isShiftKeyDown()) {
                apply(heldStone, relayTile, playerEntity);
            } else {
                store(heldStone, relayTile, playerEntity);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        var data = stack.get(Registration.RUNIC_STORAGE_STONE_DATA);
        if (data != null) {
            tooltip.add(Component.literal(data.tooltip));
        }
    }

    private void store(ItemStack stone, RelayTile relayTile, Player player) {
        if (relayTile instanceof RelaySplitterTile relaySplitterTile) {
            storeSplitterRelay(stone, relaySplitterTile);
        } else {
            storeArcaneRelay(stone, relayTile);
        }
        PortUtil.sendMessage(player, Component.literal(SAVED_CONFIGURATION));
    }

    private void storeSplitterRelay(ItemStack stone, RelaySplitterTile relaySplitterTile) {
        var toList = relaySplitterTile.getToList();
        var fromList = relaySplitterTile.getFromList();

        stone.set(
                Registration.RUNIC_STORAGE_STONE_DATA,
                new Data(
                        toList,
                        fromList,
                        RelayType.SPLITTER,
                        "Stored Splitter Config with "
                        + fromList.size() + " Take-Positions and "
                        + toList.size() + " Deposit-Positions"));
    }

    private void storeArcaneRelay(ItemStack stone, RelayTile relayTile) {
        BlockPos to = relayTile.getToPos();
        BlockPos from = relayTile.getFromPos();

        stone.set(
                Registration.RUNIC_STORAGE_STONE_DATA,
                new Data(
                        to == null ? List.of() : List.of(to),
                        from == null ? List.of() : List.of(from),
                        RelayType.STANDARD,
                        "Stored Non-Splitter Config with Take-Position "
                        + BlockPosUtils.toString(relayTile.getFromPos())
                        + " and Deposit-Position "
                        + BlockPosUtils.toString(relayTile.getToPos())));
    }

    private List<BlockPos> deserializeFakeBlockList(CompoundTag tag, String direction) {
        List<BlockPos> blockPosList = new ArrayList<>();
        int counter = 0;
        while (NBTUtil.hasBlockPos(tag, direction + counter)) {
            BlockPos pos = NBTUtil.getNullablePos(tag, direction + counter);
            if (!blockPosList.contains(pos))
                blockPosList.add(pos);
            counter++;
        }
        return blockPosList;
    }

    enum RelayType {
        STANDARD, SPLITTER
    }

    public record Data(@NotNull List<BlockPos> to, @NotNull List<BlockPos> from, @NotNull RelayType relayType,
                       @NotNull String tooltip) {
        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockPos.CODEC.listOf().fieldOf("to").forGetter(Data::to),
                BlockPos.CODEC.listOf().fieldOf("from").forGetter(Data::from),
                Codec.INT.fieldOf("relayType").xmap(i -> RelayType.values()[i], Enum::ordinal).forGetter(Data::relayType),
                Codec.STRING.fieldOf("tooltip").forGetter(Data::tooltip)
        ).apply(instance, Data::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), Data::to,
                BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), Data::from,
                ByteBufCodecs.VAR_INT.map(i -> RelayType.values()[i], RelayType::ordinal), Data::relayType,
                ByteBufCodecs.STRING_UTF8, Data::tooltip,
                Data::new
        );
    }
}
