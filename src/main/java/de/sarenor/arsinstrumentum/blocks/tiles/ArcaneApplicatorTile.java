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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArcaneApplicatorTile extends ModdedTile implements ITickable, Container, IAnimatable {
    public static final String ARCANE_APPLICATOR_TILE_ID = "arcane_applicator_tile";
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    private final AnimationFactory factory = new AnimationFactory(this);
    public float frames;
    public ItemEntity entity;
    private ItemStack stack = ItemStack.EMPTY;


    public ArcaneApplicatorTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public ArcaneApplicatorTile(BlockPos blockPos, BlockState blockState) {
        super(Registration.ARCANE_APPLICATOR_TILE.get(), blockPos, blockState);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.stack = compound.contains("itemStack") ? ItemStack.of((CompoundTag) compound.get("itemStack")) : ItemStack.EMPTY;
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
    public ItemStack getItem(int slot) {
        return getStack() == null ? ItemStack.EMPTY : getStack();
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack toReturn = getItem(0).copy().split(count);
        getStack().shrink(1);
        updateBlock();
        return toReturn;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return getStack();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack itemStackToPlace) {
        Item itemToPlace = itemStackToPlace.getItem();
        return stack == null || stack.isEmpty() && (itemToPlace instanceof ScrollOfSaveStarbuncle || itemToPlace instanceof RunicStorageStone || itemToPlace instanceof CopyPasteSpellScroll);
    }

    @Override
    public void setItem(int index, ItemStack s) {
        setStack(s);
        updateBlock();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }


    @Override
    public void clearContent() {
        this.setStack(ItemStack.EMPTY);
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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

    @SuppressWarnings("unchecked")
    private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().transitionLengthTicks = 0;
        return PlayState.CONTINUE;
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
