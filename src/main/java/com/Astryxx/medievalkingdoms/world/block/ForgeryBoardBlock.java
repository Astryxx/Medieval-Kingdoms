package com.Astryxx.medievalkingdoms.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "deprecation"})
public class ForgeryBoardBlock extends HorizontalDirectionalBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    // VoxelShape for the table stand (North orientation)
    // Coordinates match the Blockbench model (in sixteenths of a block: 0.0D to 16.0D)
    protected static final VoxelShape FORGERY_BOARD_SHAPE_NORTH = Shapes.or(
            Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), // Base (Y: 0-1)
            Block.box(7.0D, 1.0D, 7.0D, 9.0D, 13.0D, 9.0D),  // Leg/Post (Y: 1-13)
            Block.box(6.0D, 13.0D, 6.0D, 10.0D, 15.0D, 10.0D), // Top Support (Y: 13-15)
            Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D) // Top Plate (Y: 15-16)
    );

    public ForgeryBoardBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        // Use the player's horizontal direction
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction facing = state.getValue(FACING);
        // Use the custom rotation method to align the VoxelShape with the model
        return rotateY(FORGERY_BOARD_SHAPE_NORTH, facing);
    }

    // Override getCollisionShape to use the exact same complex VoxelShape for the hitbox
    @Override
    @NotNull
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }


    /**
     * Performs VoxelShape rotation around the Y-axis (90 degrees clockwise).
     * The rotation logic is fixed here to correctly swap and invert the X and Z coordinates.
     */
    protected static VoxelShape rotateY(VoxelShape shape, Direction direction) {
        if (direction == Direction.NORTH) return shape;

        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = direction.get2DDataValue(); // 0=N, 1=E, 2=S, 3=W

        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.or(buffer[1],
                            // Correct 90-degree clockwise rotation (North -> East):
                            // new minX = minZ
                            // new maxX = maxZ
                            // new minZ = 16 - maxX
                            // new maxZ = 16 - minX
                            Block.box(minZ, minY, 16 - maxX, maxZ, maxY, 16 - minX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }
}
