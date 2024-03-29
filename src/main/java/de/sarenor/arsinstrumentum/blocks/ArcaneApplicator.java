package de.sarenor.arsinstrumentum.blocks;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import de.sarenor.arsinstrumentum.blocks.tiles.ArcaneApplicatorTile;
import de.sarenor.arsinstrumentum.items.CopyPasteSpellScroll;
import de.sarenor.arsinstrumentum.items.RunicStorageStone;
import de.sarenor.arsinstrumentum.items.ScrollOfSaveStarbuncle;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static de.sarenor.arsinstrumentum.utils.BlockPosUtils.getNeighbours;
import static de.sarenor.arsinstrumentum.utils.BlockPosUtils.isNeighbour;

public class ArcaneApplicator extends TickableModBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final String ARCANE_APPLICATOR_ID = "arcane_applicator";
    public static final VoxelShape SHAPE_POST = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 14.0D, 12.0D);
    public static final VoxelShape SHAPE_TOP_PLATE = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    public static final VoxelShape SHAPE_COLLISION = Shapes.or(SHAPE_POST, SHAPE_TOP_PLATE);
    public static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.box(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.box(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), SHAPE_POST);
    public static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.box(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.box(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), SHAPE_POST);
    public static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(10.666667D, 10.0D, 0.0D, 15.0D, 14.0D, 16.0D), Block.box(6.333333D, 12.0D, 0.0D, 10.666667D, 16.0D, 16.0D), Block.box(2.0D, 14.0D, 0.0D, 6.333333D, 18.0D, 16.0D), SHAPE_POST);
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(0.0D, 10.0D, 10.666667D, 16.0D, 14.0D, 15.0D), Block.box(0.0D, 12.0D, 6.333333D, 16.0D, 16.0D, 10.666667D), Block.box(0.0D, 14.0D, 2.0D, 16.0D, 18.0D, 6.333333D), SHAPE_POST);

    public ArcaneApplicator() {
        super(defaultProperties().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(TRIGGERED, Boolean.FALSE));
    }

    public static Block.Properties defaultProperties() {
        return Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0f, 6.0f);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_POST;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COLLISION;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
        return true;
    }

    private void handleApplicationSignal(ItemStack itemStack, ServerLevel serverLevel, BlockPos blockPos) {
        if (itemStack.getItem() instanceof ScrollOfSaveStarbuncle) {
            handleStarbuncleApply(itemStack, serverLevel, blockPos);
        } else if (itemStack.getItem() instanceof RunicStorageStone) {
            handleRelayApply(itemStack, serverLevel, blockPos);
        } else if (itemStack.getItem() instanceof CopyPasteSpellScroll || itemStack.getItem() instanceof SpellParchment) {
            handleSpellturretApply(itemStack, serverLevel, blockPos);
        }
    }

    private void handleStarbuncleApply(ItemStack itemStack, ServerLevel serverLevel, BlockPos blockPos) {
        // Gets all Starbuncles with a bound bed that is adjacent to the Arcane Applicator
        serverLevel.getEntitiesOfClass(Starbuncle.class, new AABB(blockPos.north(10).west(10).below(6), blockPos.south(10).east(10).above(6))).stream()
                .filter(starbuncle -> isNeighbour(blockPos, starbuncle.data.bedPos))
                .forEach(starbuncle -> ScrollOfSaveStarbuncle.apply(itemStack, starbuncle, null));
    }

    private void handleRelayApply(ItemStack itemStack, ServerLevel serverLevel, BlockPos blockPos) {
        StreamSupport.stream(getNeighbours(blockPos).spliterator(), false)
                .map(serverLevel::getBlockEntity)
                .filter(blockEntity -> blockEntity instanceof RelayTile)
                .forEach(blockEntity -> RunicStorageStone.apply(itemStack, (RelayTile) blockEntity, null));
    }

    private void handleSpellturretApply(ItemStack itemStack, ServerLevel serverLevel, BlockPos blockPos) {
        ISpellCaster copyPasteSpellcaster = new SpellCaster(itemStack);
        StreamSupport.stream(getNeighbours(blockPos).spliterator(), false)
                .map(serverLevel::getBlockEntity)
                .filter(blockEntity -> blockEntity instanceof BasicSpellTurretTile)
                .forEach(blockEntity -> {
                    ((BasicSpellTurretTile) blockEntity).spellCaster.setSpell(copyPasteSpellcaster.getSpell());
                    ((BasicSpellTurretTile) blockEntity).spellCaster.setColor(copyPasteSpellcaster.getColor());
                    ((BasicSpellTurretTile) blockEntity).spellCaster.setSpellName(copyPasteSpellcaster.getSpellName());
                });
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean neighborSignal = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(pos.above());
        boolean isTriggered = state.getValue(TRIGGERED);
        if (neighborSignal && !isTriggered) {
            worldIn.scheduleTick(pos, this, 4);
            worldIn.setBlock(pos, state.setValue(TRIGGERED, Boolean.TRUE), 4);
        } else if (!neighborSignal && isTriggered) {
            worldIn.setBlock(pos, state.setValue(TRIGGERED, Boolean.FALSE), 4);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;
        if (!world.isClientSide && world.getBlockEntity(pos) instanceof ArcaneApplicatorTile tile) {
            if (tile.getStack() != null && player.getItemInHand(handIn).isEmpty()) {
                if (world.getBlockState(pos.above()).getMaterial() != Material.AIR)
                    return InteractionResult.SUCCESS;
                ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getStack());
                world.addFreshEntity(item);
                tile.setStack(ItemStack.EMPTY);
            } else if (!player.getInventory().getSelected().isEmpty() && isHoldableItem(player.getInventory().getSelected())) {
                if (tile.getStack() != null) {
                    ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getStack());
                    world.addFreshEntity(item);
                }
                tile.setStack(player.getInventory().removeItem(player.getInventory().selected, 1));
            }
            world.sendBlockUpdated(pos, state, state, 2);
        }
        return InteractionResult.SUCCESS;
    }

    private boolean isHoldableItem(ItemStack itemStack) {
        Item placedItem = itemStack.getItem();
        return placedItem instanceof SpellParchment || placedItem instanceof CopyPasteSpellScroll
                || placedItem instanceof RunicStorageStone || placedItem instanceof ScrollOfSaveStarbuncle;
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (worldIn.getBlockEntity(pos) instanceof ArcaneApplicatorTile tile && tile.getStack() != null) {
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getStack()));
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        getApplicableStack(serverLevel, blockPos)
                .ifPresent(itemStack -> this.handleApplicationSignal(itemStack, serverLevel, blockPos));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ArcaneApplicatorTile(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.WATERLOGGED);
        builder.add(BlockStateProperties.TRIGGERED);
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }

    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(worldIn.getBlockEntity(pos));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    private Optional<ItemStack> getApplicableStack(ServerLevel serverLevel, BlockPos blockPos) {
        if (!serverLevel.isClientSide && serverLevel.getBlockEntity(blockPos) instanceof ArcaneApplicatorTile tile && tile.getStack() != null) {
            return Optional.of(tile.getStack());
        } else {
            return Optional.empty();
        }
    }
}
