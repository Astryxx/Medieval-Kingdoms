package com.Astryxx.medievalkingdoms.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "deprecation"})
public class LawBoardBlock extends HorizontalDirectionalBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Half> HALF = EnumProperty.create("half", Half.class);

    // VoxelShape for the BOTTOM half (Base and lower post, up to Y=16)
    protected static final VoxelShape LAW_BOARD_BOTTOM_SHAPE_NORTH = Shapes.or(
            Block.box(5.0D, 0.0D, 8.0D, 11.0D, 1.0D, 14.0D), // Base (0 to 1)
            Block.box(7.0D, 1.0D, 10.0D, 9.0D, 16.0D, 12.0D) // Post (1 to 16)
    );

    // VoxelShape for the TOP half (Upper post and board, Y=16 to Y=32)
    protected static final VoxelShape LAW_BOARD_TOP_SHAPE_NORTH = Shapes.or(
            Block.box(7.0D, 0.0D, 10.0D, 9.0D, 16.0D, 12.0D), // Post continuation (16 to 32)
            Block.box(1.0D, 0.0D, 9.0D, 15.0D, 16.0D, 10.0D), // Board support back (16 to 32)
            Block.box(2.0D, 1.0D, 8.0D, 14.0D, 15.0D, 9.0D)   // Board Display (17 to 31)
    );

    public LawBoardBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockPos origin = context.getClickedPos();
        // Only allow placement if there is an empty space directly above
        if (canPlaceBoard(context.getLevel(), origin)) {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HALF, Half.BOTTOM);
        }
        return null;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos topPos = pos.above();

            // Place the top half
            level.setBlock(topPos, this.defaultBlockState().setValue(FACING, facing).setValue(HALF, Half.TOP), 3);
        }
    }

    private boolean canPlaceBoard(Level level, BlockPos origin) {
        BlockPos topPos = origin.above();
        // Check if the space above is replaceable (usually air)
        return level.getBlockState(topPos).canBeReplaced();
    }

    @Override
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction facing = state.getValue(FACING);
        VoxelShape baseShape = (state.getValue(HALF) == Half.BOTTOM) ?
                LAW_BOARD_BOTTOM_SHAPE_NORTH :
                LAW_BOARD_TOP_SHAPE_NORTH;

        return rotateY(baseShape, facing);
    }

    @Override
    @NotNull
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        // Use the exact same VoxelShape for collision
        return this.getShape(state, level, pos, context);
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide) {
            Half half = state.getValue(HALF);
            BlockPos otherPos = (half == Half.BOTTOM) ? pos.above() : pos.below();

            if (level.getBlockState(otherPos).is(this)) {
                // Creative players destroy the other part without dropping items
                if (player.isCreative()) {
                    level.setBlock(otherPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 35);
                } else {
                    // Survival players destroy the other part without dropping items (only the bottom block drops the item)
                    level.destroyBlock(otherPos, false);
                }
            }
        }
        // Call super to ensure this block is destroyed, and item drops if it's the BOTTOM half (via super logic + loot table)
        super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * Performs VoxelShape rotation around the Y-axis.
     */
    protected static VoxelShape rotateY(VoxelShape shape, Direction direction) {
        if (direction == Direction.NORTH) return shape;

        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = direction.get2DDataValue();

        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.or(buffer[1],
                            Block.box(16 - maxZ, minY, minX, 16 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }
}