package de.sarenor.arsinstrumentum.items.curios.armarium;

import de.sarenor.arsinstrumentum.ArsInstrumentum;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.sarenor.arsinstrumentum.utils.SerializationUtiils.deserializeItemList;
import static de.sarenor.arsinstrumentum.utils.SerializationUtiils.serializeItemList;

@Getter
@Setter
public class ArmariumSlot {
    private static final String ARMARIUM_ARMOR_TAG = ArsInstrumentum.MODID + "_armarium_armor_tag";
    private static final String ARMARIUM_HOTBAR_TAG = ArsInstrumentum.MODID + "_armarium_hotbar_tag";
    private static final String ARMARIUM_SPELLFOCUS_TAG = ArsInstrumentum.MODID + "_armarium_spellfocus_tag";
    private static final String ARMARIUM_FAMILIAR_TAG = ArsInstrumentum.MODID + "_armarium_familiar_tag";

    private List<ItemStack> armor = new ArrayList<>();
    private List<ItemStack> hotbar = new ArrayList<>();
    private List<ItemStack> spellfoci = new ArrayList<>();
    private String familiarId = null;

    public static ArmariumSlot deserialize(CompoundTag compoundTag) {
        ArmariumSlot armariumSlot = new ArmariumSlot();
        armariumSlot.setArmor(deserializeItemList(compoundTag, ARMARIUM_ARMOR_TAG));
        armariumSlot.setHotbar(deserializeItemList(compoundTag, ARMARIUM_HOTBAR_TAG));
        armariumSlot.setSpellfoci(deserializeItemList(compoundTag, ARMARIUM_SPELLFOCUS_TAG));
        if (compoundTag.contains(ARMARIUM_FAMILIAR_TAG)) {
            armariumSlot.setFamiliarId(compoundTag.getString(ARMARIUM_FAMILIAR_TAG));
        }
        return armariumSlot;
    }

    public CompoundTag serialize() {
        CompoundTag serialized = new CompoundTag();
        serialized.put(ARMARIUM_ARMOR_TAG, serializeItemList(armor));
        serialized.put(ARMARIUM_HOTBAR_TAG, serializeItemList(hotbar));
        serialized.put(ARMARIUM_SPELLFOCUS_TAG, serializeItemList(spellfoci));
        if (familiarId != null) {
            serialized.putString(ARMARIUM_FAMILIAR_TAG, familiarId);
        }
        return serialized;
    }

    public String listArmor() {
        return armor.stream()
                .map(ItemStack::getDisplayName)
                .map(Component::getString)
                .collect(Collectors.joining(", "));
    }
}
