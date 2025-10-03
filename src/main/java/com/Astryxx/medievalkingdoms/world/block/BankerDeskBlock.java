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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BankerDeskBlock extends HorizontalDirectionalBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BankerDeskPart> PART = EnumProperty.create("part", BankerDeskPart.class);

    // Collision for the Center block (Grate + Top). Defined assuming FACING=NORTH.
    protected static final VoxelShape CENTER_SHAPE_NORTH = Shapes.or(
            Block.box(0, 15, 0, 16, 16, 16), // Top surface
            Block.box(0, 0, 1, 16, 15, 2)    // Grate/Screen (thin vertical part near the front)
    );
    // Collision for the Side blocks (Full block depth, based on common designs)
    protected static final VoxelShape SIDE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);


    public BankerDeskBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, BankerDeskPart.CENTER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    @SuppressWarnings("deprecation")
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (state.getValue(PART) == BankerDeskPart.CENTER) {
            Direction facing = state.getValue(FACING);
            return rotateY(CENTER_SHAPE_NORTH, facing);
        }
        return SIDE_SHAPE;
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

        if (canPlaceDesk(context.getLevel(), origin, facing)) {
            return this.defaultBlockState().setValue(FACING, facing).setValue(PART, BankerDeskPart.CENTER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            Direction widthDirection = facing.getClockWise();

            // Place LEFT block
            BlockPos leftPos = pos.relative(widthDirection, 1);
            level.setBlock(leftPos, this.defaultBlockState().setValue(FACING, facing).setValue(PART, BankerDeskPart.LEFT), 3);

            // Place RIGHT block
            BlockPos rightPos = pos.relative(widthDirection, -1);
            level.setBlock(rightPos, this.defaultBlockState().setValue(FACING, facing).setValue(PART, BankerDeskPart.RIGHT), 3);
        }
    }

    public enum BankerDeskPart implements net.minecraft.util.StringRepresentable {
        LEFT("left"),
        CENTER("center"),
        RIGHT("right");

        private final String name;

        BankerDeskPart(String name) {
            this.name = name;
        }

        @Override
        @NotNull
        public String getSerializedName() {
            return this.name;
        }
    }

    private boolean canPlaceDesk(Level level, BlockPos origin, Direction facing) {
        Direction widthDirection = facing.getClockWise();

        BlockPos leftPos = origin.relative(widthDirection, 1);
        BlockPos rightPos = origin.relative(widthDirection, -1);

        return level.getBlockState(leftPos).canBeReplaced() && level.getBlockState(rightPos).canBeReplaced();
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            Direction widthDirection = facing.getClockWise();
            BankerDeskPart part = state.getValue(PART);

            // Determine the position of the CENTER/Anchor block based on the part being broken
            BlockPos anchorPos = pos;
            if (part == BankerDeskPart.LEFT) {
                anchorPos = pos.relative(widthDirection, -1);
            } else if (part == BankerDeskPart.RIGHT) {
                anchorPos = pos.relative(widthDirection, 1);
            }

            if (player.isCreative()) {
                for (BankerDeskPart p : BankerDeskPart.values()) {
                    BlockPos currentPos = anchorPos;
                    if (p == BankerDeskPart.LEFT) {
                        currentPos = anchorPos.relative(widthDirection, 1);
                    } else if (p == BankerDeskPart.RIGHT) {
                        currentPos = anchorPos.relative(widthDirection, -1);
                    }

                    if (level.getBlockState(currentPos).is(this) && !currentPos.equals(pos)) {
                        level.destroyBlock(currentPos, false);
                    }
                }
            } else {
                for (BankerDeskPart p : BankerDeskPart.values()) {
                    BlockPos currentPos = anchorPos;
                    if (p == BankerDeskPart.LEFT) {
                        currentPos = anchorPos.relative(widthDirection, 1);
                    } else if (p == BankerDeskPart.RIGHT) {
                        currentPos = anchorPos.relative(widthDirection, -1);
                    }

                    if (level.getBlockState(currentPos).is(this)) {
                        level.destroyBlock(currentPos, currentPos.equals(anchorPos));
                    }
                }
                return;
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }
}
