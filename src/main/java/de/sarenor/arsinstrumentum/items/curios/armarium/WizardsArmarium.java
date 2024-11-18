package de.sarenor.arsinstrumentum.items.curios.armarium;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.SecondaryIconPosition;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSummonFamiliar;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import de.sarenor.arsinstrumentum.network.WizardsArmariumChoiceMessage;
import de.sarenor.arsinstrumentum.setup.Registration;
import de.sarenor.arsinstrumentum.utils.CuriosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hollingsworth.arsnouveau.common.event.FamiliarEvents.getFamiliars;
import static de.sarenor.arsinstrumentum.setup.Registration.ARMARIUM_STORAGE;
import static de.sarenor.arsinstrumentum.setup.Registration.WIZARDS_ARMARIUM;
import static de.sarenor.arsinstrumentum.utils.IterableUtils.iterableToList;

public class WizardsArmarium extends ArsNouveauCurio {
    public static final String WIZARDS_ARMARIUM_ID = "wizards_armarium";
    public static final String SWITCHED_TO_NO_HOTBAR = "instrumentum.armarium.hotbar_no_switch";
    public static final String SWITCHED_TO_HOTBAR = "instrumentum.armarium.hotbar_switch";
    private static final int HOTBAR_SIZE = 9;
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public WizardsArmarium(Properties properties) {
        super(properties.component(ARMARIUM_STORAGE, new ArmariumStorage()));
    }

    @OnlyIn(Dist.CLIENT)
    public static void openSwitchRadialMenu(Player player) {
        var armariumOptional = CuriosApi.getCuriosInventory(player).flatMap(i -> i.findFirstCurio(WIZARDS_ARMARIUM.get()));
        armariumOptional.ifPresent(slotResult -> {
            ArmariumStorage armariumStorage = slotResult.stack().get(ARMARIUM_STORAGE.get());
            if (armariumStorage == null) return;
            Minecraft.getInstance().setScreen(new GuiRadialMenu<>(getRadialMenuProvider(armariumStorage)));
        });
    }

    public static void handleArmariumChoice(ServerPlayer player, int choosenSlot) {
        var armariumOptional = CuriosApi.getCuriosInventory(player).flatMap(i -> i.findFirstCurio(WIZARDS_ARMARIUM.get()));
        armariumOptional.ifPresent(slotResult -> {
            ArmariumStorage armariumStorage = slotResult.stack().get(ARMARIUM_STORAGE.get());
            if (armariumStorage == null) return;
            ArmariumSlot armariumSlot = armariumStorage.storeAndGet(iterableToList(player.getArmorSlots()),
                    player.getInventory().items.subList(0, 9), CuriosUtil.getSpellfoci(player), getFamiliarId(player), Slots.getSlotForInt(choosenSlot));

            setArmor(player, armariumSlot.armor());
            if (armariumStorage.isHotbarSwitch()) {
                setHotbar(player, armariumSlot.hotbar());
            }
            setFamiliar(player, armariumSlot.familiarId());
            CuriosUtil.setSpellfoci(player, armariumSlot.spellfoci());
        });
    }

    public static void handleArmariumSwitch(ServerPlayer player) {
        var armariumOptional = CuriosApi.getCuriosInventory(player).flatMap(i -> i.findFirstCurio(WIZARDS_ARMARIUM.get()));
        armariumOptional.ifPresent(slotResult -> {
            ArmariumStorage armariumStorage = slotResult.stack().get(ARMARIUM_STORAGE.get());
            if (armariumStorage == null) return;

            ArmariumSlot armariumSlot = armariumStorage.storeAndGet(iterableToList(player.getArmorSlots()),
                    player.getInventory().items.subList(0, 9), CuriosUtil.getSpellfoci(player), getFamiliarId(player), null);

            setArmor(player, armariumSlot.armor());
            if (armariumStorage.isHotbarSwitch()) {
                setHotbar(player, armariumSlot.hotbar());
            }
            setFamiliar(player, armariumSlot.familiarId());
            CuriosUtil.setSpellfoci(player, armariumSlot.spellfoci());
        });
    }


    public static void handleModeSwitch(ItemStack itemStack, Player player) {
        ArmariumStorage storage = itemStack.getOrDefault(ARMARIUM_STORAGE.get(), new ArmariumStorage());
        if (storage == null) return;
        itemStack.set(Registration.ARMARIUM_STORAGE.get(), storage.switchHotbarMode());
        PortUtil.sendMessage(player, Component.translatable(
                storage.isHotbarSwitch() ? SWITCHED_TO_HOTBAR : SWITCHED_TO_NO_HOTBAR)
        );

    }

    private static void setArmor(ServerPlayer player, List<ItemStack> armorItems) {
        for (EquipmentSlot equipmentSlot : ARMOR_SLOTS) {
            Optional<ItemStack> armorItem = armorItems.stream()
                    .filter(itemStack -> player.getEquipmentSlotForItem(itemStack).equals(equipmentSlot))
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

    private static void setFamiliar(ServerPlayer player, ResourceLocation familiarHolderId) {
        try {
            if (familiarHolderId != null) {
                Networking.sendToServer(new PacketSummonFamiliar(familiarHolderId));
            } else {
                getFamiliars(familiarEntity -> familiarEntity.getOwner() != null && familiarEntity.getOwner().equals(player))
                        .stream().findFirst().ifPresent(familiarEntity -> familiarEntity.remove(Entity.RemovalReason.DISCARDED));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ResourceLocation getFamiliarId(ServerPlayer player) {
        return getFamiliars(familiarEntity -> familiarEntity.getOwner() != null && familiarEntity.getOwner().equals(player))
                .stream().map(FamiliarEntity::getHolderID).findFirst().orElse(null);
    }

    private static RadialMenu<ItemStack> getRadialMenuProvider(ArmariumStorage armariumStorage) {
        return new RadialMenu<>((int slot) -> Networking.sendToServer(new WizardsArmariumChoiceMessage(slot)),
                getRadialMenuSlots(armariumStorage),
                SecondaryIconPosition.EAST,
                WizardsArmarium::renderItemAsNonTransparentIcon,
                0);
    }

    private static List<RadialMenuSlot<ItemStack>> getRadialMenuSlots(ArmariumStorage armariumStorage) {
        List<RadialMenuSlot<ItemStack>> radialMenuSlots = new ArrayList<>();
        radialMenuSlots.add(getRadialMenuSlot(armariumStorage.getArmariumSlots().slots().getOrDefault(Slots.SLOT_ONE, new ArmariumSlot())));
        radialMenuSlots.add(getRadialMenuSlot(armariumStorage.getArmariumSlots().slots().getOrDefault(Slots.SLOT_TWO, new ArmariumSlot())));
        radialMenuSlots.add(getRadialMenuSlot(armariumStorage.getArmariumSlots().slots().getOrDefault(Slots.SLOT_THREE, new ArmariumSlot())));
        return radialMenuSlots;
    }

    private static RadialMenuSlot<ItemStack> getRadialMenuSlot(ArmariumSlot armariumSlot) {
        ItemStack primaryIcon = armariumSlot.spellfoci().stream().findFirst().orElse(null);
        List<ItemStack> secondaryIcons = new ArrayList<>(armariumSlot.armor());
        return new RadialMenuSlot<>("", primaryIcon, secondaryIcons);
    }

    public static void renderItemAsNonTransparentIcon(ItemStack providedItem, GuiGraphics poseStack, int positionX,
                                                      int positionY, int size, boolean renderTransparent) {
        RenderUtils.drawItemAsIcon(providedItem, poseStack, positionX, positionY, size, false);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, @NotNull Player
            player, @NotNull InteractionHand handIn) {
        if (world.isClientSide) {
            return super.use(world, player, handIn);
        }
        ItemStack heldArmarium = player.getItemInHand(handIn);

        if (player.isShiftKeyDown()) {
            handleModeSwitch(heldArmarium, player);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldArmarium);
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, heldArmarium);
    }


//    @Override
//    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
//        super.appendHoverText(stack, context, tooltip2, flagIn);
//        ArmariumStorage armariumStorage = new ArmariumStorage(stack);
//        tooltip.addAll(armariumStorage.getTooltip());
//    }

}
