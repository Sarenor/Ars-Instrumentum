package de.sarenor.arsinstrumentum.blocks.tiles;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import de.sarenor.arsinstrumentum.items.CopyPasteSpellScroll;
import de.sarenor.arsinstrumentum.items.RunicStorageStone;
import de.sarenor.arsinstrumentum.items.ScrollOfSaveStarbuncle;
import de.sarenor.arsinstrumentum.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArcaneApplicatorTile extends ModdedTile implements ITickable, Container, GeoBlockEntity {
    public static final String ARCANE_APPLICATOR_TILE_ID = "arcane_applicator_tile";
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
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
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        this.stack = compound.contains("itemStack") ? ItemStack.of(compound.getCompound("itemStack")) : ItemStack.EMPTY;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (getStack() != null) {
            CompoundTag reagentTag = new CompoundTag();
            getStack().save(reagentTag);
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
        return stack == null || stack.isEmpty() && (itemToPlace instanceof ScrollOfSaveStarbuncle || itemToPlace instanceof RunicStorageStone || itemToPlace instanceof CopyPasteSpellScroll);
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


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        itemHandler.invalidate();
        super.invalidateCaps();
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
