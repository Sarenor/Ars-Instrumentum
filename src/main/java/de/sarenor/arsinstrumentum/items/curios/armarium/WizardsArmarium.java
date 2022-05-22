package de.sarenor.arsinstrumentum.items.curios.armarium;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.client.gui.RadialMenu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.RadialMenu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.RadialMenu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.RadialMenu.SecondaryIconPosition;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import com.hollingsworth.arsnouveau.common.network.PacketSummonFamiliar;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import de.sarenor.arsinstrumentum.network.Networking;
import de.sarenor.arsinstrumentum.network.WizardsArmariumChoiceMessage;
import de.sarenor.arsinstrumentum.utils.CuriosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hollingsworth.arsnouveau.common.event.FamiliarEvents.getFamiliars;
import static de.sarenor.arsinstrumentum.setup.Registration.WIZARDS_ARMARIUM;
import static de.sarenor.arsinstrumentum.utils.IterableUtils.iterableToList;

public class WizardsArmarium extends ArsNouveauCurio {
    public static final String WIZARDS_ARMARIUM_ID = "wizards_armarium";
    private static final int HOTBAR_SIZE = 9;
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final String SWITCHED_TO_NO_HOTBAR = "Wizards Armarium will not switch Hotbar Items";
    private static final String SWITCHED_TO_HOTBAR = "Wizards Armarium will switch Hotbar Items";

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
                player.getInventory().items.subList(0, 9), CuriosUtil.getSpellfoci(player), getFamiliarId(player), Slots.getSlotForInt(choosenSlot));

        setArmor(player, armariumSlot.getArmor());
        if (armariumStorage.isHotbarSwitch()) {
            setHotbar(player, armariumSlot.getHotbar());
        }
        setFamiliar(player, armariumSlot.getFamiliarId());
        CuriosUtil.setSpellfoci(player, armariumSlot.getSpellfoci());
    }

    public static void handleArmariumSwitch(ServerPlayer player) {
        ArmariumStorage armariumStorage = new ArmariumStorage(
                CuriosApi.getCuriosHelper().findEquippedCurio(WIZARDS_ARMARIUM.get(), player).get().getRight());
        ArmariumSlot armariumSlot = armariumStorage.storeAndGet(iterableToList(player.getArmorSlots()),
                player.getInventory().items.subList(0, 9), CuriosUtil.getSpellfoci(player), getFamiliarId(player), null);

        setArmor(player, armariumSlot.getArmor());
        if (armariumStorage.isHotbarSwitch()) {
            setHotbar(player, armariumSlot.getHotbar());
        }
        setFamiliar(player, armariumSlot.getFamiliarId());
        CuriosUtil.setSpellfoci(player, armariumSlot.getSpellfoci());
    }

    public static void handleModeSwitch(ItemStack itemStack, Player player) {
        ArmariumStorage storage = new ArmariumStorage(itemStack);
        storage.switchIsHotbarSwitch();
        if (storage.isHotbarSwitch()) {
            PortUtil.sendMessage(player, new TextComponent(SWITCHED_TO_HOTBAR));
        } else {
            PortUtil.sendMessage(player, new TextComponent(SWITCHED_TO_NO_HOTBAR));
        }
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

    private static void setFamiliar(ServerPlayer player, String familiarHolderId) {
        com.hollingsworth.arsnouveau.common.network.Networking.INSTANCE.sendToServer(new PacketSummonFamiliar(familiarHolderId, player.getId()));
    }

    private static String getFamiliarId(ServerPlayer player) {
        return getFamiliars(familiarEntity -> familiarEntity.getOwner() != null && familiarEntity.getOwner().equals(player))
                .stream().map(FamiliarEntity::getHolderID).findFirst().orElse(null);
    }

    private static RadialMenu<Item> getRadialMenuProvider(ArmariumStorage armariumStorage) {
        return new RadialMenu<>((int slot) -> Networking.INSTANCE.sendToServer(new WizardsArmariumChoiceMessage(slot)),
                getRadialMenuSlots(armariumStorage),
                SecondaryIconPosition.EAST,
                WizardsArmarium::renderItemAsNonTransparentIcon,
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

    public static void renderItemAsNonTransparentIcon(Item providedItem, PoseStack poseStack, int positionX, int positionY, int size, boolean renderTransparent) {
        RenderUtils.drawItemAsIcon(providedItem, poseStack, positionX, positionY, size, false);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand handIn) {
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

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag p_77624_4_) {
        ArmariumStorage armariumStorage = new ArmariumStorage(stack);
        tooltip.addAll(armariumStorage.getTooltip());
    }

    @Override
    public void wearableTick(LivingEntity livingEntity) {
        // No op
    }
}