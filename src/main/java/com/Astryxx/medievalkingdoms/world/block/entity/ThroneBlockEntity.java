package com.Astryxx.medievalkingdoms.world.block.entity;

import com.Astryxx.medievalkingdoms.register.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block Entity for the Royal Throne. Manages its state and will be used
 * for future GUI access.
 */
public class ThroneBlockEntity extends BlockEntity {

    public ThroneBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.THRONE_BE.get(), pPos, pBlockState);
    }

    // Add necessary synchronization methods (like load/save) later when adding kingdom data.
}