package de.sarenor.arsinstrumentum.items.curios.armarium;

import de.sarenor.arsinstrumentum.ArsInstrumentum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmariumStorage {

    public static final String ARMARIUM_STORAGE_TAG_ID = ArsInstrumentum.MODID + "_armarium_storage_tag";
    private static final String CURRENT_SLOT = "current_slot";
    private static final String FLAVORTEXT = "flavortext"; //TODO: Actually write Flavortext
    private static final String IS_HOTBAR_SWITCH = "is_hotbar_switch";
    private static final TextComponent WILL_SWITCH_HOTBAR = new TextComponent("Wizards Armarium will switch your Hotbar Items");
    private static final TextComponent WONT_SWITCH_HOTBAR = new TextComponent("Wizards Armarium won't switch your Hotbar Items");
    private static final TextComponent HOTBAR_WARNING = new TextComponent("Remove Hotbar Items from Armarium before switching mode!");
    private static final Style HOTBAR_WARNING_STYLE = Style.EMPTY.withColor(TextColor.fromRgb((int) Long.parseLong("AA0000", 16))).withUnderlined(true);

    private final Map<Slots, ArmariumSlot> armariumSlots = new HashMap<>();
    private Slots currentSlot;
    private String flavorText = "";
    private ItemStack armarium;
    private boolean isHotbarSwitch = true;

    public ArmariumStorage(ItemStack armarium) {
        this(armarium.getOrCreateTag());
        this.armarium = armarium;
    }

    private ArmariumStorage(CompoundTag itemTag) {
        CompoundTag tag = itemTag.getCompound(ARMARIUM_STORAGE_TAG_ID);

        this.currentSlot = tag.contains(CURRENT_SLOT) ? Slots.valueOf(tag.getString(CURRENT_SLOT)) : Slots.SLOT_ONE;
        this.flavorText = tag.getString(FLAVORTEXT);
        for (Slots slot : Slots.values()) {
            if (tag.contains(slot.name())) {
                armariumSlots.put(slot, ArmariumSlot.deserialize(tag.getCompound(slot.name())));
            }
        }
        if (tag.contains(IS_HOTBAR_SWITCH)) {
            this.isHotbarSwitch = tag.getBoolean(IS_HOTBAR_SWITCH);
        } else {
            this.isHotbarSwitch = true;
        }
    }

    public void switchIsHotbarSwitch() {
        isHotbarSwitch = !isHotbarSwitch;
        for (ArmariumSlot slot : armariumSlots.values()) {
            slot.setHotbar(Collections.emptyList());
        }
        writeArmariumStorageToArmariumItem();
    }

    public boolean isHotbarSwitch() {
        return isHotbarSwitch;
    }

    public ArmariumSlot storeAndGet(List<ItemStack> armorItems, List<ItemStack> hotbarItems, List<ItemStack> spellfocus, Slots slotsToGet) {
        ArmariumSlot armariumSlot = armariumSlots.getOrDefault(currentSlot, new ArmariumSlot());
        armariumSlot.setArmor(armorItems);
        if (isHotbarSwitch) {
            armariumSlot.setHotbar(hotbarItems);
        }
        armariumSlot.setSpellfoci(spellfocus);
        armariumSlots.put(currentSlot, armariumSlot);

        currentSlot = slotsToGet != null ? slotsToGet : Slots.getNextSlot(currentSlot);

        writeArmariumStorageToArmariumItem();
        return armariumSlots.getOrDefault(currentSlot, new ArmariumSlot());
    }

    public List<TextComponent> getTooltip() {
        HOTBAR_WARNING.setStyle(HOTBAR_WARNING_STYLE);
        if (!armariumSlots.isEmpty()) {
            return List.of(HOTBAR_WARNING,
                    isHotbarSwitch ? WILL_SWITCH_HOTBAR : WONT_SWITCH_HOTBAR,
                    new TextComponent("Next slots armor is: " + armariumSlots.get(Slots.getNextSlot(currentSlot)).listArmor()));
        } else {
            return List.of(HOTBAR_WARNING, isHotbarSwitch ? WILL_SWITCH_HOTBAR : WONT_SWITCH_HOTBAR);
        }
    }

    public Map<Slots, ArmariumSlot> getArmariumSlots() {
        return this.armariumSlots;
    }

    private void writeArmariumStorageToArmariumItem() {
        if (armarium == null || armarium.isEmpty()) {
            return;
        }
        CompoundTag tag = armarium.getOrCreateTag();
        tag.put(ARMARIUM_STORAGE_TAG_ID, writeTag()); // Nest our tags so we dont cause conflicts
        armarium.setTag(tag);
    }

    private CompoundTag writeTag() {
        CompoundTag armariumTag = new CompoundTag();
        armariumTag.putString(CURRENT_SLOT, currentSlot.name());
        armariumTag.putString(FLAVORTEXT, flavorText);
        armariumTag.putBoolean(IS_HOTBAR_SWITCH, isHotbarSwitch);

        for (Slots slot : Slots.values()) {
            armariumTag.put(slot.name(), armariumSlots.getOrDefault(slot, new ArmariumSlot()).serialize());
        }
        return armariumTag;
    }
}
