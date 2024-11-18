package de.sarenor.arsinstrumentum.items.curios.armarium;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.sarenor.arsinstrumentum.items.curios.armarium.ArmariumSlot.ArmariumSlotMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ArmariumStorage implements ITooltipProvider {

    public static final MutableComponent WILL_SWITCH_HOTBAR = Component.translatable(WizardsArmarium.SWITCHED_TO_HOTBAR);
    public static final MutableComponent WONT_SWITCH_HOTBAR = Component.translatable(WizardsArmarium.SWITCHED_TO_NO_HOTBAR);

    public static final MapCodec<ArmariumStorage> CODEC = createCodec(ArmariumStorage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ArmariumStorage> STREAM_CODEC = createStream(ArmariumStorage::new);

    public static <T extends ArmariumStorage> MapCodec<T> createCodec(Function4<Integer, String, Boolean, ArmariumSlotMap, T> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.optionalFieldOf("current_slot", 0).forGetter(ArmariumStorage::getCurrentSlot),
                Codec.STRING.optionalFieldOf("flavor_text", "").forGetter(ArmariumStorage::getFlavorText),
                Codec.BOOL.optionalFieldOf("is_hotbar_switch", false).forGetter(ArmariumStorage::isHotbarSwitch),
                ArmariumSlotMap.CODEC.optionalFieldOf("slots", new ArmariumSlotMap(ImmutableMap.of())).forGetter(ArmariumStorage::getArmariumSlots)
        ).apply(instance, constructor));
    }

    public static <T extends ArmariumStorage> StreamCodec<RegistryFriendlyByteBuf, T> createStream(Function4<Integer, String, Boolean, ArmariumSlotMap, T> constructor) {
        return StreamCodec.composite(ByteBufCodecs.INT, ArmariumStorage::getCurrentSlot, ByteBufCodecs.STRING_UTF8, ArmariumStorage::getFlavorText,
                ByteBufCodecs.BOOL, ArmariumStorage::isHotbarSwitch, ArmariumSlotMap.STREAM_CODEC, ArmariumStorage::getArmariumSlots,
                constructor);
    }

    public ArmariumStorage switchHotbarMode() {
        return new ArmariumStorage(currentSlot, flavorText, !isHotbarSwitch, getArmariumSlots());
    }

    static Map<Slots, ArmariumSlot> emptyMap() {
        Map<Slots, ArmariumSlot> map = new HashMap<>();
        for (Slots slot : Slots.values()) {
            map.put(slot, new ArmariumSlot());
        }
        return ImmutableMap.copyOf(map);
    }

    public ArmariumStorage() {
        this(Slots.getSlotForInt(0), "", false, new ArmariumSlotMap(emptyMap()));
    }
    public ArmariumStorage(Integer currentSlot, String flavorText, Boolean isHotbarSwitch, ArmariumSlotMap slotMap) {
        this(Slots.getSlotForInt(currentSlot), flavorText, isHotbarSwitch, slotMap);
    }
    public ArmariumStorage(Slots currentSlot, String flavorText, Boolean isHotbarSwitch, ArmariumSlotMap slotMap) {
        this.currentSlot = currentSlot;
        this.flavorText = flavorText;
        this.isHotbarSwitch = isHotbarSwitch;
        this.armariumSlots.putAll(slotMap.slots());
    }

    private final Map<Slots, ArmariumSlot> armariumSlots = new HashMap<>();
    private final String flavorText;
    private final Slots currentSlot;
    private final boolean isHotbarSwitch;

    public int getCurrentSlot() {
        return currentSlot.ordinal();
    }

    public String getFlavorText() {
        return flavorText;
    }

    public boolean isHotbarSwitch() {
        return isHotbarSwitch;
    }

    public ArmariumSlotMap getArmariumSlots() {
        return new ArmariumSlotMap(armariumSlots);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(isHotbarSwitch ? WILL_SWITCH_HOTBAR : WONT_SWITCH_HOTBAR);
        if (!armariumSlots.isEmpty()) {
            tooltip.add(Component.literal("Next slots armor is: " + armariumSlots.get(Slots.getNextSlot(currentSlot)).listArmor()));
        }
    }

    public ArmariumSlot storeAndGet(List<ItemStack> itemStacks, List<ItemStack> itemStacks1, List<ItemStack> spellfoci, ResourceLocation familiarId, Slots slot) {
        // get current ArmariumSlot, create the new one, and store it in the map
        ArmariumSlot armariumSlot = armariumSlots.get(slot);
        armariumSlots.put(slot, new ArmariumSlot(itemStacks, itemStacks1, spellfoci, familiarId));
        return armariumSlot;
    }

    //    public void switchIsHotbarSwitch(Player player) {
//        isHotbarSwitch = !isHotbarSwitch;
//        List<ItemStack> itemStacksToDrop = new ArrayList<>();
//        armariumSlots.forEach((slots, armariumSlot) -> {
//            if (!slots.equals(currentSlot)) {
//                itemStacksToDrop.addAll(armariumSlot.hotbar());
//            }
//            armariumSlot.hotbar(Collections.emptyList());
//        });
//
//        itemStacksToDrop.forEach(itemStack -> spawnItem(player, itemStack));
//
//        writeArmariumStorageToArmariumItem();
//    }

//
//    private void spawnItem(Player player, ItemStack itemStack) {
//        Random rand = new Random();
//        float rx = rand.nextFloat() * 0.8F + 0.1F;
//        float ry = rand.nextFloat() * 0.8F + 0.1F;
//        float rz = rand.nextFloat() * 0.8F + 0.1F;
//
//        ItemEntity entityItem = new ItemEntity(player.level(),
//                player.position().x + rx, player.position().y + ry, player.position().z + rz,
//                itemStack.copy());
//
//        if (itemStack.hasTag()) {
//            entityItem.getItem().setTag(itemStack.getOrCreateTag().copy());
//        }
//
//        player.level().addFreshEntity(entityItem);
//        itemStack.setCount(0);
//    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArmariumStorage that = (ArmariumStorage) o;
        return isHotbarSwitch() == that.isHotbarSwitch() && Objects.equals(getArmariumSlots(), that.getArmariumSlots()) && Objects.equals(getFlavorText(), that.getFlavorText()) && getCurrentSlot() == that.getCurrentSlot();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getArmariumSlots(), getFlavorText(), getCurrentSlot(), isHotbarSwitch());
    }
}
