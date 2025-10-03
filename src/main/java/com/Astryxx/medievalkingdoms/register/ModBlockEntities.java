package com.Astryxx.medievalkingdoms.register;

import com.Astryxx.medievalkingdoms.MedievalKingdoms;
import com.Astryxx.medievalkingdoms.world.block.entity.ThroneBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers all custom Block Entities (Tile Entities) for the mod.
 */
public class ModBlockEntities {
    // DeferredRegister for BlockEntityType
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MedievalKingdoms.MOD_ID);

    // 1. Register the ThroneBlockEntity
    @SuppressWarnings("DataFlowIssue") // Suppresses the warning about passing 'null' to @NotNull parameter in the build method.
    public static final RegistryObject<BlockEntityType<ThroneBlockEntity>> THRONE_BE =
            BLOCK_ENTITIES.register("throne_be", () ->
                    BlockEntityType.Builder.of(
                            ThroneBlockEntity::new,
                            ModBlocks.ROYAL_THRONE.get()
                    ).build(null));
}