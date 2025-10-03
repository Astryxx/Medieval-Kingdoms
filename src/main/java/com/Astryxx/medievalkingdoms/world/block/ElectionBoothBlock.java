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

public class ElectionBoothBlock extends HorizontalDirectionalBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Half> HALF = EnumProperty.create("half", Half.class);

    // VoxelShape for the bottom half (legs and counter). Defined assuming FACING=NORTH.
    protected static final VoxelShape BOOTH_BOTTOM_SHAPE_NORTH = Shapes.or(
            Block.box(0, 15, 0, 16, 16, 16), // Counter top
            Block.box(0, 0, 0, 2, 16, 2),   // Leg 1
            Block.box(14, 0, 0, 16, 16, 2),  // Leg 2
            Block.box(0, 0, 14, 2, 16, 16),  // Leg 3
            Block.box(14, 0, 14, 16, 16, 16) // Leg 4
    );
    // VoxelShape for the top half (full block collision for stability)
    protected static final VoxelShape BOOTH_TOP_SHAPE = Shapes.block();

    public ElectionBoothBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    @SuppressWarnings("deprecation")
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (state.getValue(HALF) == Half.BOTTOM) {
            Direction facing = state.getValue(FACING);
            return rotateY(BOOTH_BOTTOM_SHAPE_NORTH, facing);
        }
        return BOOTH_TOP_SHAPE;
    }

    /**
     * Performs VoxelShape rotation around the Y-axis using the supported 2D axis value.
     */
    protected static VoxelShape rotateY(VoxelShape pShape, Direction direction) {
        if (direction == Direction.NORTH) return pShape;

        VoxelShape[] buffer = new VoxelShape[]{pShape, Shapes.empty()};

        // get2DDataValue returns: NORTH=0, EAST=1, SOUTH=2, WEST=3. We rotate N times.
        int times = direction.get2DDataValue();

        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.or(buffer[1], Block.box(16 - maxZ, minY, minX, 16 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos origin = context.getClickedPos();

        if (canPlaceBooth(context.getLevel(), origin)) {
            return this.defaultBlockState().setValue(FACING, facing).setValue(HALF, Half.BOTTOM);
        }
        return null;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos topPos = pos.above();

            level.setBlock(topPos, this.defaultBlockState().setValue(FACING, facing).setValue(HALF, Half.TOP), 3);
        }
    }

    private boolean canPlaceBooth(Level level, BlockPos origin) {
        BlockPos topPos = origin.above();
        return level.getBlockState(topPos).canBeReplaced();
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide) {
            Half half = state.getValue(HALF);
            BlockPos otherPos = (half == Half.BOTTOM) ? pos.above() : pos.below();

            if (level.getBlockState(otherPos).is(this)) {

                boolean dropItem = half == Half.BOTTOM && !player.isCreative();

                if (player.isCreative()) {
                    level.setBlock(otherPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 35);
                } else {
                    level.destroyBlock(otherPos, false);
                    level.destroyBlock(pos, dropItem);
                    return;
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }
}
