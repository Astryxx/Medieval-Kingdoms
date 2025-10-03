package com.Astryxx.medievalkingdoms.world.block;

import com.Astryxx.medievalkingdoms.register.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"deprecation"})
public class RoyalThroneBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty WIDTH_PART = IntegerProperty.create("width_part", 0, 2);
    public static final IntegerProperty HEIGHT_PART = IntegerProperty.create("height_part", 0, 1);
    public static final IntegerProperty DEPTH_PART = IntegerProperty.create("depth_part", 0, 1);


    private static final int THRONE_HEIGHT = 2;
    private static final int THRONE_DEPTH = 2;
    private static final int THRONE_WIDTH = 3;
    private static final int SEAT_DURATION = 999999;

    // VoxelShape for the anchor block (Center, Bottom, Front). Defined assuming FACING=NORTH.
    protected static final VoxelShape THRONE_ANCHOR_SHAPE_NORTH = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), // Solid seat base (extended to z=0)
            Block.box(1.0D, 8.0D, 3.0D, 15.0D, 9.0D, 16.0D)   // Cushion
    );

    public RoyalThroneBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WIDTH_PART, 1) // Center of the 3 wide
                .setValue(HEIGHT_PART, 0) // Bottom half
                .setValue(DEPTH_PART, 0)); // Front half
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WIDTH_PART, HEIGHT_PART, DEPTH_PART);
    }

    @Override
    @SuppressWarnings("deprecation")
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        // Only return the custom shape for the designated anchor block (Center, Bottom, Front)
        if (state.getValue(WIDTH_PART) == 1 && state.getValue(HEIGHT_PART) == 0 && state.getValue(DEPTH_PART) == 0) {
            Direction facing = state.getValue(FACING);
            return rotateY(THRONE_ANCHOR_SHAPE_NORTH, facing);
        }

        // Non-anchor parts must return an empty shape to prevent collision issues
        return Shapes.empty();
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

        if (canPlaceThrone(context.getLevel(), origin, facing)) {
            return this.defaultBlockState().setValue(FACING, facing).setValue(WIDTH_PART, 1).setValue(HEIGHT_PART, 0).setValue(DEPTH_PART, 0);
        }
        return null;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            Direction widthDir = facing.getClockWise();

            for (int y = 0; y < THRONE_HEIGHT; y++) {
                for (int i = 0; i < THRONE_DEPTH; i++) {
                    for (int j = 0; j < THRONE_WIDTH; j++) {
                        BlockPos currentPos = pos.above(y)
                                .relative(facing.getOpposite(), i)
                                .relative(widthDir.getOpposite(), j - 1);

                        if (currentPos.equals(pos)) continue;

                        BlockState newState = this.defaultBlockState()
                                .setValue(FACING, facing)
                                .setValue(WIDTH_PART, j)
                                .setValue(HEIGHT_PART, y)
                                .setValue(DEPTH_PART, i);

                        level.setBlock(currentPos, newState, 3);
                    }
                }
            }
        }
    }

    private boolean canPlaceThrone(Level level, BlockPos origin, Direction facing) {
        Direction widthDir = facing.getClockWise();

        for (int y = 0; y < THRONE_HEIGHT; y++) {
            for (int i = 0; i < THRONE_DEPTH; i++) {
                for (int j = 0; j < THRONE_WIDTH; j++) {

                    BlockPos checkPos = origin.above(y)
                            .relative(facing.getOpposite(), i)
                            .relative(widthDir.getOpposite(), j - 1);

                    if (!level.getBlockState(checkPos).canBeReplaced()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            Direction widthDir = facing.getClockWise();
            int anchorX = state.getValue(WIDTH_PART);
            int anchorY = state.getValue(HEIGHT_PART);
            int anchorZ = state.getValue(DEPTH_PART);

            BlockPos mainAnchorPos = pos.below(anchorY)
                    .relative(facing, anchorZ)
                    .relative(widthDir, anchorX - 1);

            // Clean up the seat entity (AreaEffectCloud) regardless of creative mode
            level.getEntitiesOfClass(AreaEffectCloud.class,
                            new AABB(mainAnchorPos.getX(), mainAnchorPos.getY(), mainAnchorPos.getZ(),
                                    mainAnchorPos.getX() + 1, mainAnchorPos.getY() + 1, mainAnchorPos.getZ() + 1),
                            (Entity entity) -> entity instanceof AreaEffectCloud && ((AreaEffectCloud) entity).getDuration() == SEAT_DURATION)
                    .forEach(Entity::discard);

            if (player.isCreative()) {
                for (int y = 0; y < THRONE_HEIGHT; y++) {
                    for (int i = 0; i < THRONE_DEPTH; i++) {
                        for (int j = 0; j < THRONE_WIDTH; j++) {
                            BlockPos currentPos = mainAnchorPos.above(y)
                                    .relative(facing.getOpposite(), i)
                                    .relative(widthDir.getOpposite(), j - 1);

                            if (level.getBlockState(currentPos).is(this) && !currentPos.equals(pos)) {
                                level.setBlock(currentPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 35);
                            }
                        }
                    }
                }
            } else {
                for (int y = 0; y < THRONE_HEIGHT; y++) {
                    for (int i = 0; i < THRONE_DEPTH; i++) {
                        for (int j = 0; j < THRONE_WIDTH; j++) {
                            BlockPos currentPos = mainAnchorPos.above(y)
                                    .relative(facing.getOpposite(), i)
                                    .relative(widthDir.getOpposite(), j - 1);

                            if (level.getBlockState(currentPos).is(this)) {
                                level.destroyBlock(currentPos, currentPos.equals(mainAnchorPos));
                            }
                        }
                    }
                }
                return;
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return ModBlockEntities.THRONE_BE.get().create(pPos, pState);
    }

    @Override
    @NotNull
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                 @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {

        if (level.isClientSide || player.getVehicle() != null) {
            return InteractionResult.SUCCESS;
        }

        // Check if it's the seat part (Anchor block: Center, Bottom, Front)
        if (state.getValue(WIDTH_PART) == 1 && state.getValue(DEPTH_PART) == 0 && state.getValue(HEIGHT_PART) == 0) {
            if (level.getBlockEntity(pos) != null && player.getPassengers().isEmpty()) {
                // Extracted method to fix complexity warning
                spawnSeatEntity(level, pos, state.getValue(FACING), player);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * Spawns an invisible AreaEffectCloud entity to act as a seat and mounts the player to it.
     */
    private void spawnSeatEntity(Level level, BlockPos pos, Direction facing, Player player) {
        // Calculate seat position slightly above and forward/backward relative to facing
        double xOffset = 0.5;
        double zOffset = 0.5;

        // Adjust seat position based on the throne's facing direction
        if (facing == Direction.NORTH) zOffset = 0.8;
        else if (facing == Direction.SOUTH) zOffset = 0.2;
        else if (facing == Direction.EAST) xOffset = 0.2;
        else if (facing == Direction.WEST) xOffset = 0.8;

        // Create the invisible seat entity
        AreaEffectCloud seat = new AreaEffectCloud(level, pos.getX() + xOffset, pos.getY() + 0.05, pos.getZ() + zOffset);
        seat.setDuration(SEAT_DURATION);
        seat.setRadius(0.0F);
        seat.setInvisible(true);

        level.addFreshEntity(seat);
        player.startRiding(seat);
    }
}
