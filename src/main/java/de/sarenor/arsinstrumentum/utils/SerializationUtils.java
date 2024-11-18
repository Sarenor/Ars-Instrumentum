package de.sarenor.arsinstrumentum.utils;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import de.sarenor.arsinstrumentum.items.curios.armarium.Slots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SerializationUtils {

    private static final int COMPOUND_TAG_TYPE = 10;
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";

    public static ListTag serializeItemList(List<ItemStack> items, RegistryAccess registryAccess) {
        ListTag itemList = new ListTag();
        items.forEach((itemstack) -> itemList.add(itemstack.saveOptional(registryAccess)));
        return itemList;
    }

    public static List<ItemStack> deserializeItemList(RegistryFriendlyByteBuf buf) {
        List<ItemStack> itemStacks = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            itemStacks.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
        }
        return itemStacks;
    }

    public static ListTag serializeBlockPosList(List<BlockPos> blockPositions) {
        ListTag serializedBlockPositions = new ListTag();
        blockPositions.forEach(blockPos -> serializedBlockPositions.add(serializeBlockPos(blockPos)));
        return serializedBlockPositions;
    }

    public static CompoundTag serializeBlockPos(BlockPos blockPos) {
        CompoundTag serializedBlockPos = new CompoundTag();
        serializedBlockPos.putInt(X, blockPos.getX());
        serializedBlockPos.putInt(Y, blockPos.getY());
        serializedBlockPos.putInt(Z, blockPos.getZ());
        return serializedBlockPos;
    }

    public static List<BlockPos> deserializeBlockPosList(CompoundTag tag, String key) {
        ListTag serializedBlockPositions = tag.getList(key, COMPOUND_TAG_TYPE);
        List<BlockPos> blockPositions = new ArrayList<>();
        for (int i = 0; i < serializedBlockPositions.size(); i++) {
            blockPositions.add(deserializeBlockPos(serializedBlockPositions.getCompound(i)));
        }
        return blockPositions;
    }

    public static BlockPos deserializeBlockPos(CompoundTag serializedBlockPosition) {
        return new BlockPos(serializedBlockPosition.getInt(X), serializedBlockPosition.getInt(Y), serializedBlockPosition.getInt(Z));
    }

    public static <MapVal, Obj> Codec<Obj> slotMap(Codec<MapVal> codec, Function<Map<Slots, MapVal>, Obj> constructor, Function<Obj, Map<Slots, MapVal>> intMap){
        return Codec.unboundedMap(Codec.STRING, codec).xmap((stringMap) ->{
            var builder = ImmutableMap.<Slots, MapVal>builder();
            stringMap.forEach((key, value) -> builder.put(Slots.getSlotForInt(Integer.parseInt(key)), value));
            return constructor.apply(builder.build());
        }, obj -> {
            var ints = intMap.apply(obj);
            var builder = ImmutableMap.<String, MapVal>builder();
            ints.forEach((key, value) -> builder.put(key.toString(), value));
            return builder.build();
        });
    }
}
