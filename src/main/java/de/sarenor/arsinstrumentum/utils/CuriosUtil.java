package de.sarenor.arsinstrumentum.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CuriosUtil {
    private static final String SPELLFOCUS_CURIO_ID = "an_focus";

    public static Optional<IDynamicStackHandler> getSpellfocusStackHandler(ServerPlayer player) {
        return CuriosApi.getCuriosHelper().getCuriosHandler(player)
                .map(iCuriosItemHandler -> iCuriosItemHandler)
                .flatMap(map -> map.getStacksHandler(SPELLFOCUS_CURIO_ID))
                .map(ICurioStacksHandler::getStacks);
    }

    public static List<ItemStack> getSpellfoci(ServerPlayer player) {
        Optional<IDynamicStackHandler> dynamicSpellfocusStackHandler = CuriosUtil.getSpellfocusStackHandler(player);
        return dynamicSpellfocusStackHandler
                .map(CuriosUtil::getAllItemsInSlots)
                .orElse(Collections.emptyList());
    }

    public static void setSpellfoci(ServerPlayer player, List<ItemStack> spellfoci) {
        CuriosUtil.getSpellfocusStackHandler(player)
                .ifPresent(dynamicStackHandler -> CuriosUtil.setAllSpellfoci(dynamicStackHandler, spellfoci));
    }

    private static List<ItemStack> getAllItemsInSlots(IDynamicStackHandler dynamicStackHandler) {
        List<ItemStack> equippedCuriosInSlots = new ArrayList<>();
        for (int i = 0; i < dynamicStackHandler.getSlots(); i++) {
            equippedCuriosInSlots.add(dynamicStackHandler.getStackInSlot(i));
        }
        return equippedCuriosInSlots;
    }

    private static void setAllSpellfoci(IDynamicStackHandler dynamicStackHandler, List<ItemStack> spellfoci) {
        int maxLength = Math.max(dynamicStackHandler.getSlots(), spellfoci.size());
        for (int i = 0; i < maxLength; i++) {
            if (i >= dynamicStackHandler.getSlots()) {
                break;
            }
            if (i < spellfoci.size()) {
                dynamicStackHandler.setStackInSlot(i, spellfoci.get(i));
            } else {
                dynamicStackHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }
}
