package de.sarenor.arsinstrumentum.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RelaySplitterTile;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import de.sarenor.arsinstrumentum.utils.BlockPosUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

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

    private static final String FROM_CONFIGURATION = "from_configuration";
    private static final String TO_CONFIGURATION = "to_configuration";
    private static final String RELAY_TYPE = "relay_type";
    private static final String TOOLTIP = "tooltip";
    private static final String DIRECTION_FROM = "from_";
    private static final String DIRECTION_TO = "to_";
    private static final String RUNIC_STORAGE_STONE_TAG_ID = "scroll_of_save_starbuncle_tag";

    public RunicStorageStone() {
        super((new Properties()).stacksTo(1).tab(ArsNouveau.itemGroup));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
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
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag p_77624_4_) {
        CompoundTag scrollTag = stack.getOrCreateTag();
        if (scrollTag.contains(RUNIC_STORAGE_STONE_TAG_ID)) {
            tooltip.add(Component.literal(scrollTag.getCompound(RUNIC_STORAGE_STONE_TAG_ID).getString(TOOLTIP)));
        }
    }

    private void apply(ItemStack stone, RelayTile relayTile, Player player) {
        CompoundTag stoneTag = stone.getOrCreateTag();
        if (stoneTag.contains(RUNIC_STORAGE_STONE_TAG_ID)) {
            CompoundTag configTag = stoneTag.getCompound(RUNIC_STORAGE_STONE_TAG_ID);
            if (RelayType.valueOf(configTag.getString(RELAY_TYPE)) == RelayType.SPLITTER) {
                if (relayTile instanceof RelaySplitterTile relaySplitterTile) {
                    applySplitterRelay(configTag, relaySplitterTile, player);
                } else {
                    PortUtil.sendMessage(player, Component.literal(CANT_APPLY_SPLITTER_CONFIGURATION_TO_ARCANE));
                }
            } else {
                if (!(relayTile instanceof RelaySplitterTile)) {
                    applyArcaneRelay(configTag, relayTile, player);
                } else {
                    PortUtil.sendMessage(player, Component.literal(CANT_APPLY_ARCANE_CONFIGURATION_TO_SPLITTER));
                }
            }
        } else {
            PortUtil.sendMessage(player, Component.literal(CANT_APPLY_EMPTY_CONFIGURATION));
        }
    }

    private void applySplitterRelay(CompoundTag configTag, RelaySplitterTile relaySplitterTile, Player player) {
        List<BlockPos> toList = deserializeBlockPosList(configTag, TO_CONFIGURATION);
        List<BlockPos> fromList = deserializeBlockPosList(configTag, FROM_CONFIGURATION);

        for (BlockPos to : toList) {
            if (relaySplitterTile.closeEnough(to)) {
                relaySplitterTile.setSendTo(to);
            } else {
                PortUtil.sendMessage(player, Component.literal("Block " + BlockPosUtils.toString(to) + " is to far"
                        + " away from the Relay and could not be added to Deposit-Locations"));
            }
        }
        for (BlockPos from : fromList) {
            if (relaySplitterTile.closeEnough(from)) {
                relaySplitterTile.setTakeFrom(from);
            } else {
                PortUtil.sendMessage(player, Component.literal("Block " + BlockPosUtils.toString(from) + " is to far"
                        + " away from the Relay and could not be added to Take-Locations"));
            }
        }
        PortUtil.sendMessage(player, Component.literal(APPLIED_CONFIGURATION));
    }

    private void applyArcaneRelay(CompoundTag configTag, RelayTile relayTile, Player player) {
        BlockPos to = deserializeBlockPos(configTag.getCompound(TO_CONFIGURATION));
        BlockPos from = deserializeBlockPos(configTag.getCompound(FROM_CONFIGURATION));
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

    private void store(ItemStack stone, RelayTile relayTile, Player player) {
        if (relayTile instanceof RelaySplitterTile relaySplitterTile) {
            storeSplitterRelay(stone, relaySplitterTile);
        } else {
            storeArcaneRelay(stone, relayTile);
        }
        PortUtil.sendMessage(player, Component.literal(SAVED_CONFIGURATION));
    }

    private void storeSplitterRelay(ItemStack stone, RelaySplitterTile relaySplitterTile) {
        CompoundTag stoneTag = stone.getOrCreateTag();
        CompoundTag configTag = new CompoundTag();
        CompoundTag tileData = new CompoundTag();
        relaySplitterTile.saveAdditional(tileData);
        List<BlockPos> fromList = deserializeFakeBlockList(tileData, DIRECTION_FROM);
        List<BlockPos> toList = deserializeFakeBlockList(tileData, DIRECTION_TO);

        configTag.putString(RELAY_TYPE, RelayType.SPLITTER.name());
        configTag.put(FROM_CONFIGURATION, serializeBlockPosList(fromList));
        configTag.put(TO_CONFIGURATION, serializeBlockPosList(toList));
        configTag.putString(TOOLTIP, "Stored Splitter Config with " + fromList.size() + " Take-Positions and "
                + toList.size() + " Deposit-Positions");
        stoneTag.put(RUNIC_STORAGE_STONE_TAG_ID, configTag);
    }

    private void storeArcaneRelay(ItemStack stone, RelayTile relayTile) {
        CompoundTag stoneTag = stone.getOrCreateTag();
        CompoundTag configTag = new CompoundTag();
        configTag.put(TO_CONFIGURATION, serializeBlockPos(relayTile.getToPos()));
        configTag.put(FROM_CONFIGURATION, serializeBlockPos(relayTile.getFromPos()));
        configTag.putString(RELAY_TYPE, RelayType.STANDARD.name());
        configTag.putString(TOOLTIP, "Stored Non-Splitter Config with Take-Position " + BlockPosUtils.toString(relayTile.getFromPos())
                + " and Deposit-Position " + BlockPosUtils.toString(relayTile.getToPos()));
        stoneTag.put(RUNIC_STORAGE_STONE_TAG_ID, configTag);
        stone.setTag(stoneTag);
    }

    private List<BlockPos> deserializeFakeBlockList(CompoundTag tag, String direction) {
        List<BlockPos> blockPosList = new ArrayList<>();
        int counter = 0;
        while (NBTUtil.hasBlockPos(tag, direction + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(tag, direction + counter);
            if (!blockPosList.contains(pos))
                blockPosList.add(pos);
            counter++;
        }
        return blockPosList;
    }

    enum RelayType {
        STANDARD, SPLITTER
    }
}
