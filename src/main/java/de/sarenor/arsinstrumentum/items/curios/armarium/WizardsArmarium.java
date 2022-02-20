package de.sarenor.arsinstrumentum.items.curios.armarium;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.client.gui.RadialMenu.*;
import de.sarenor.arsinstrumentum.network.Networking;
import de.sarenor.arsinstrumentum.network.WizardsArmariumChoiceMessage;
import de.sarenor.arsinstrumentum.utils.CuriosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.sarenor.arsinstrumentum.setup.Registration.WIZARDS_ARMARIUM;
import static de.sarenor.arsinstrumentum.utils.IterableUtils.iterableToList;

public class WizardsArmarium extends ArsNouveauCurio {
    public static final String WIZARDS_ARMARIUM_ID = "wizards_armarium";
    private static final int HOTBAR_SIZE = 9;
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @OnlyIn(Dist.CLIENT)
    public static void openSwitchRadialMenu(Player player) {
        ArmariumStorage armariumStorage = new ArmariumStorage(
                CuriosApi.getCuriosHelper().findEquippedCurio(WIZARDS_ARMARIUM.get(), player).get().getRight());
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(getRadialMenuProvider(armariumStorage)));
    }

    public static void handleArmariumChoice(ServerPlayer player, int choosenSlot) {
        ArmariumStorage armariumStorage = new ArmariumStorage(
                CuriosApi.getCuriosHelper().findEquippedCurio(WIZARDS_ARMARIUM.get(), player).get().getRight());
        ArmariumSlot armariumSlot = armariumStorage.storeAndGet(iterableToList(player.getArmorSlots()),
                player.getInventory().items.subList(0, 9), CuriosUtil.getSpellfoci(player), Slots.getSlotForInt(choosenSlot));

        setArmor(player, armariumSlot.getArmor());
        setHotbar(player, armariumSlot.getHotbar());
        CuriosUtil.setSpellfoci(player, armariumSlot.getSpellfoci());
    }

    public static void handleArmariumSwitch(ServerPlayer player) {
        ArmariumStorage armariumStorage = new ArmariumStorage(
                CuriosApi.getCuriosHelper().findEquippedCurio(WIZARDS_ARMARIUM.get(), player).get().getRight());
        ArmariumSlot armariumSlot = armariumStorage.storeAndGet(iterableToList(player.getArmorSlots()),
                player.getInventory().items.subList(0, 9), CuriosUtil.getSpellfoci(player), null);

        setArmor(player, armariumSlot.getArmor());
        setHotbar(player, armariumSlot.getHotbar());
        CuriosUtil.setSpellfoci(player, armariumSlot.getSpellfoci());
    }

    private static void setArmor(ServerPlayer player, List<ItemStack> armorItems) {
        for (EquipmentSlot equipmentSlot : ARMOR_SLOTS) {
            Optional<ItemStack> armorItem = armorItems.stream()
                    .filter(itemStack -> LivingEntity.getEquipmentSlotForItem(itemStack).equals(equipmentSlot))
                    .findFirst();
            player.setItemSlot(equipmentSlot, armorItem.orElse(ItemStack.EMPTY));
        }
    }

    private static void setHotbar(ServerPlayer player, List<ItemStack> hotbarItems) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            if (i < hotbarItems.size()) {
                inventory.setItem(i, hotbarItems.get(i));
            } else {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    private static RadialMenu<Item> getRadialMenuProvider(ArmariumStorage armariumStorage) {
        return new RadialMenu<>((int slot) -> Networking.INSTANCE.sendToServer(new WizardsArmariumChoiceMessage(slot)),
                getRadialMenuSlots(armariumStorage),
                SecondaryIconPosition.EAST,
                GuiRadialMenuUtils::drawItemAsIcon,
                0);
    }

    private static List<RadialMenuSlot<Item>> getRadialMenuSlots(ArmariumStorage armariumStorage) {
        List<RadialMenuSlot<Item>> radialMenuSlots = new ArrayList<>();
        radialMenuSlots.add(getRadialMenuSlot(armariumStorage.getArmariumSlots().getOrDefault(Slots.SLOT_ONE, new ArmariumSlot())));
        radialMenuSlots.add(getRadialMenuSlot(armariumStorage.getArmariumSlots().getOrDefault(Slots.SLOT_TWO, new ArmariumSlot())));
        radialMenuSlots.add(getRadialMenuSlot(armariumStorage.getArmariumSlots().getOrDefault(Slots.SLOT_THREE, new ArmariumSlot())));
        return radialMenuSlots;
    }

    private static RadialMenuSlot<Item> getRadialMenuSlot(ArmariumSlot armariumSlot) {
        Item primaryIcon = armariumSlot.getSpellfoci().stream().map(ItemStack::getItem).findFirst().orElse(null);
        List<Item> secondaryIcons = armariumSlot.getArmor().stream().map(ItemStack::getItem).collect(Collectors.toList());
        return new RadialMenuSlot<>("", primaryIcon, secondaryIcons);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag p_77624_4_) {
        ArmariumStorage armariumStorage = new ArmariumStorage(stack);
        tooltip.add(new TextComponent(armariumStorage.getTooltip()));
    }

    @Override
    public void wearableTick(LivingEntity livingEntity) {
        // No op
    }
}