package de.sarenor.arsinstrumentum.items.curios.armarium;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.sarenor.arsinstrumentum.utils.SerializationUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.sarenor.arsinstrumentum.utils.SerializationUtils.deserializeItemList;
import static net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC;

public record ArmariumSlot(List<ItemStack> armor, List<ItemStack> hotbar, List<ItemStack> spellfoci,
                           ResourceLocation familiarId) {

    public ArmariumSlot() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null);
    }

    public static final MapCodec<ArmariumSlot> CODEC = createCodec(ArmariumSlot::new);

    public ArmariumSlot(List<ItemStack> armor, List<ItemStack> hotbar, List<ItemStack> spellfoci, ResourceLocation familiarId) {
        this.armor = armor;
        this.hotbar = hotbar;
        this.spellfoci = spellfoci;
        this.familiarId = familiarId;
    }

    private static <T extends ArmariumSlot> MapCodec<T> createCodec(Function4<List<ItemStack>, List<ItemStack>, List<ItemStack>, ResourceLocation, T> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.list(ItemStack.CODEC).fieldOf("armor").forGetter(ArmariumSlot::armor),
                Codec.list(ItemStack.CODEC).fieldOf("hotbar").forGetter(ArmariumSlot::hotbar),
                Codec.list(ItemStack.CODEC).fieldOf("spellfoci").forGetter(ArmariumSlot::spellfoci),
                ResourceLocation.CODEC.optionalFieldOf("familiarId", ResourceLocation.withDefaultNamespace("none")).forGetter(ArmariumSlot::familiarId)
        ).apply(instance, constructor));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmariumSlot> STREAM = StreamCodec.ofMember(ArmariumSlot::serialize, ArmariumSlot::deserialize
    );

    public static ArmariumSlot deserialize(RegistryFriendlyByteBuf buf) {
        List<ItemStack> armor = deserializeItemList(buf);
        List<ItemStack> hotbar = deserializeItemList(buf);
        List<ItemStack> foci = deserializeItemList(buf);
        ResourceLocation familiarId = null;
        if (buf.readableBytes() > 0) {
            familiarId = buf.readResourceLocation();
        }
        return new ArmariumSlot(armor, hotbar, foci, familiarId);
    }


    public static void serialize(ArmariumSlot slot, RegistryFriendlyByteBuf buf) {

        buf.writeInt(slot.armor.size());
        for (ItemStack stack : slot.armor) {
            OPTIONAL_STREAM_CODEC.encode(buf, stack);
        }
        buf.writeInt(slot.hotbar.size());
        for (ItemStack stack : slot.hotbar) {
            OPTIONAL_STREAM_CODEC.encode(buf, stack);
        }
        buf.writeInt(slot.spellfoci.size());
        for (ItemStack stack : slot.spellfoci) {
            OPTIONAL_STREAM_CODEC.encode(buf, stack);
        }

        if (slot.familiarId != null) {
            buf.writeResourceLocation(slot.familiarId);
        }

    }

    public String listArmor() {
        return armor.stream()
                .map(ItemStack::getDisplayName)
                .map(Component::getString)
                .collect(Collectors.joining(", "));
    }

    public record ArmariumSlotMap(Map<Slots, ArmariumSlot> slots) {
        public static final Codec<ArmariumSlotMap> CODEC = SerializationUtils.slotMap(ArmariumSlot.CODEC.codec(), ArmariumSlotMap::new, ArmariumSlotMap::slots);
        public static final StreamCodec<RegistryFriendlyByteBuf, ArmariumSlotMap> STREAM_CODEC =
                StreamCodec.ofMember((val, buf) -> {
                    var entries = val.slots.entrySet();
                    buf.writeInt(entries.size());

                    for (var entry : entries) {
                        buf.writeInt(entry.getKey().ordinal());
                        ArmariumSlot.STREAM.encode(buf, entry.getValue());
                    }
                }, (buf) -> {
                    int size = buf.readInt();
                    var immutableMap = ImmutableMap.<Slots, ArmariumSlot>builder();
                    for (int i = 0; i < size; i++) {
                        int key = buf.readInt();
                        ArmariumSlot value = ArmariumSlot.STREAM.decode(buf);
                        immutableMap.put(Slots.getSlotForInt(key), value);
                    }
                    return new ArmariumSlotMap(immutableMap.build());
                });
    }
}
