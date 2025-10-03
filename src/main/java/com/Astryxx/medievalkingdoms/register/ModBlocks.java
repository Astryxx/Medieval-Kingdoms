package com.Astryxx.medievalkingdoms.register;

import com.Astryxx.medievalkingdoms.MedievalKingdoms;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.Astryxx.medievalkingdoms.world.block.RoyalThroneBlock;
import com.Astryxx.medievalkingdoms.world.block.BankerDeskBlock; // NEW IMPORT placeholder
import com.Astryxx.medievalkingdoms.world.block.ElectionBoothBlock; // NEW IMPORT placeholder
import com.Astryxx.medievalkingdoms.world.block.LawBoardBlock; // ADDED IMPORT
import com.Astryxx.medievalkingdoms.world.block.ForgeryBoardBlock; // ADDED IMPORT

/**
 * Registers all custom blocks for the Medieval Kingdoms mod.
 */
public class ModBlocks {
    // DeferredRegister for Blocks
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MedievalKingdoms.MOD_ID);

    // 1. ROYAL THRONE (Single-block with 3x2x2 validation)
    public static final RegistryObject<Block> ROYAL_THRONE = BLOCKS.register("royal_throne",
            () -> new RoyalThroneBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(3.5F)
                    .sound(SoundType.LODESTONE)));

    // 2. LAW BOARD (Two-block high structure)
    public static final RegistryObject<Block> LAW_BOARD = BLOCKS.register("law_board",
            () -> new LawBoardBlock(BlockBehaviour.Properties.of() // FIXED: Using LawBoardBlock
                    .mapColor(MapColor.WOOD)
                    .strength(3.0F)
                    .noOcclusion() // Added for VoxelShape rendering
                    .sound(SoundType.HANGING_SIGN)));

    // 3. BANKER DESK (Single-block with 3x1x1 validation)
    public static final RegistryObject<Block> BANKER_DESK = BLOCKS.register("banker_desk",
            () -> new BankerDeskBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)));

    // 4. ELECTION BOOTH (Single-block with 1x1x2 validation)
    public static final RegistryObject<Block> ELECTION_BOOTH = BLOCKS.register("election_booth",
            () -> new ElectionBoothBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)));

    // 5. FORGERY TABLE (Single block - using custom block)
    public static final RegistryObject<Block> FORGERY_TABLE = BLOCKS.register("forgery_table",
            () -> new ForgeryBoardBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(2.0F)
                    .noOcclusion() // 👈 CRITICAL FIX: Add this property
                    .sound(SoundType.WOOD)));
}