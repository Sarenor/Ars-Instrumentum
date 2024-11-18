package de.sarenor.arsinstrumentum.blocks.tiles;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import de.sarenor.arsinstrumentum.items.CopyPasteSpellScroll;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArcaneApplicatorTile extends ModdedTile implements ITickable, Container, GeoBlockEntity {
    public static final String ARCANE_APPLICATOR_TILE_ID = "arcane_applicator_tile";
    public ItemEntity entity;
    public float frames;
    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private ItemStack stack = ItemStack.EMPTY;

    public ArcaneApplicatorTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
    }

    public ArcaneApplicatorTile(BlockPos blockPos, BlockState blockState) {
        super(Registration.ARCANE_APPLICATOR_TILE.get(), blockPos, blockState);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.stack = ItemStack.parseOptional(registries, tag.getCompound("itemStack"));
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag,registries);
        if (getStack() != null) {
            CompoundTag reagentTag = new CompoundTag();
            getStack().save(registries, reagentTag);
            tag.put("itemStack", reagentTag);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getStack() == null || getStack().isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return getStack() == null ? ItemStack.EMPTY : getStack();
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        ItemStack toReturn = getItem(0).copy().split(count);
        getStack().shrink(1);
        updateBlock();
        return toReturn;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        return getStack();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack itemStackToPlace) {
        Item itemToPlace = itemStackToPlace.getItem();
        return false;
        //return stack == null || stack.isEmpty() && (itemToPlace instanceof ScrollOfSaveStarbuncle || itemToPlace instanceof RunicStorageStone || itemToPlace instanceof CopyPasteSpellScroll);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack s) {
        setStack(s);
        updateBlock();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }


    @Override
    public void clearContent() {
        this.setStack(ItemStack.EMPTY);
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        updateBlock();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
