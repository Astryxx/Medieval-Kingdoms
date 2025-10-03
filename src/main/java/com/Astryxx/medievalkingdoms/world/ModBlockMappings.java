package com.Astryxx.medievalkingdoms.world;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Defines the replacement mappings for vanilla blocks based on the assigned kingdom theme.
 * This map is used by the KingdomStructureProcessor.
 */
public class ModBlockMappings {

    // --- Kingdom Themes ---
    public static final String NORDIC = "Nordic";
    public static final String WESSEX = "Wessex";
    public static final String BYZANTINE = "Byzantine";
    public static final String KIEVAN = "Kievan"; // RE-ADDED
    public static final String MOORISH = "Moorish"; // NEW THEME

    // Set of all themes for validation
    @SuppressWarnings("unused")
    public static final Set<String> ALL_THEMES = Set.of(NORDIC, WESSEX, BYZANTINE, KIEVAN, MOORISH); // UPDATED

    // Master map: Theme -> (Original Block -> Replacement Block)
    private static final Map<String, Map<Block, Block>> THEME_MAPS = new HashMap<>();

    static {
        // --- NORDIC (Viking/Mountain Aesthetic) --- (UNCHANGED)
        Map<Block, Block> nordicMap = new HashMap<>();
        // ... (Nordic map content remains as originally provided)
        nordicMap.put(Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS);
        nordicMap.put(Blocks.OAK_LOG, Blocks.SPRUCE_LOG);
        nordicMap.put(Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE);
        nordicMap.put(Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR);
        nordicMap.put(Blocks.OAK_STAIRS, Blocks.SPRUCE_STAIRS);
        nordicMap.put(Blocks.OAK_SLAB, Blocks.SPRUCE_SLAB);
        nordicMap.put(Blocks.SPRUCE_PLANKS, Blocks.DARK_OAK_PLANKS);
        nordicMap.put(Blocks.SPRUCE_LOG, Blocks.DARK_OAK_LOG);
        nordicMap.put(Blocks.BIRCH_PLANKS, Blocks.SPRUCE_PLANKS);
        nordicMap.put(Blocks.COBBLESTONE, Blocks.STONE_BRICKS);
        nordicMap.put(Blocks.COBBLESTONE_WALL, Blocks.STONE_BRICK_WALL);
        nordicMap.put(Blocks.STONE, Blocks.COBBLED_DEEPSLATE);
        nordicMap.put(Blocks.GRAVEL, Blocks.COBBLED_DEEPSLATE);
        nordicMap.put(Blocks.DIRT_PATH, Blocks.TUFF);
        nordicMap.put(Blocks.ANDESITE, Blocks.TUFF);
        THEME_MAPS.put(NORDIC, nordicMap);

        // --- WESSEX (Anglo-Saxon/Southern English Aesthetic) --- (UNCHANGED)
        Map<Block, Block> wessexMap = new HashMap<>();
        // ... (Wessex map content remains as originally provided)
        wessexMap.put(Blocks.OAK_PLANKS, Blocks.STRIPPED_OAK_LOG);
        wessexMap.put(Blocks.OAK_LOG, Blocks.OAK_PLANKS);
        wessexMap.put(Blocks.BIRCH_PLANKS, Blocks.OAK_PLANKS);
        wessexMap.put(Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS);
        wessexMap.put(Blocks.COBBLESTONE, Blocks.SMOOTH_STONE);
        wessexMap.put(Blocks.COBBLESTONE_WALL, Blocks.STONE_BRICK_WALL);
        wessexMap.put(Blocks.STONE, Blocks.CALCITE);
        wessexMap.put(Blocks.GRAVEL, Blocks.COARSE_DIRT);
        wessexMap.put(Blocks.DIRT_PATH, Blocks.GRAVEL);
        wessexMap.put(Blocks.ANDESITE, Blocks.DIORITE);
        wessexMap.put(Blocks.BRICKS, Blocks.DEEPSLATE_TILES);
        wessexMap.put(Blocks.BRICK_STAIRS, Blocks.DEEPSLATE_TILE_STAIRS);
        THEME_MAPS.put(WESSEX, wessexMap);

        // --- BYZANTINE (Orthodox/Mediterranean Aesthetic) --- (UNCHANGED)
        Map<Block, Block> byzantineMap = new HashMap<>();
        // ... (Byzantine map content remains as originally provided)
        byzantineMap.put(Blocks.OAK_PLANKS, Blocks.BIRCH_PLANKS);
        byzantineMap.put(Blocks.OAK_LOG, Blocks.JUNGLE_LOG);
        byzantineMap.put(Blocks.BIRCH_PLANKS, Blocks.BIRCH_PLANKS);
        byzantineMap.put(Blocks.SPRUCE_PLANKS, Blocks.JUNGLE_PLANKS);
        byzantineMap.put(Blocks.COBBLESTONE, Blocks.SMOOTH_STONE_SLAB);
        byzantineMap.put(Blocks.COBBLESTONE_WALL, Blocks.ANDESITE_WALL);
        byzantineMap.put(Blocks.STONE, Blocks.QUARTZ_BLOCK);
        byzantineMap.put(Blocks.GRAVEL, Blocks.TERRACOTTA);
        byzantineMap.put(Blocks.DIRT_PATH, Blocks.RED_SANDSTONE);
        byzantineMap.put(Blocks.ANDESITE, Blocks.POLISHED_DIORITE);
        byzantineMap.put(Blocks.BRICKS, Blocks.ORANGE_TERRACOTTA);
        byzantineMap.put(Blocks.BRICK_STAIRS, Blocks.ORANGE_TERRACOTTA);
        byzantineMap.put(Blocks.GOLD_BLOCK, Blocks.WAXED_COPPER_BLOCK);
        byzantineMap.put(Blocks.WHITE_WOOL, Blocks.CYAN_CONCRETE);
        byzantineMap.put(Blocks.RED_WOOL, Blocks.BLUE_CONCRETE);
        THEME_MAPS.put(BYZANTINE, byzantineMap);

        // --- KIEVAN (Eastern European/Slavic Aesthetic) --- (RE-ADDED)
        Map<Block, Block> kievanMap = new HashMap<>();
        // ... (Kievan map content remains as originally provided)
        kievanMap.put(Blocks.OAK_PLANKS, Blocks.DARK_OAK_PLANKS);
        kievanMap.put(Blocks.OAK_LOG, Blocks.SPRUCE_LOG);
        kievanMap.put(Blocks.BIRCH_PLANKS, Blocks.SPRUCE_PLANKS);
        kievanMap.put(Blocks.SPRUCE_PLANKS, Blocks.STRIPPED_SPRUCE_LOG);
        kievanMap.put(Blocks.SPRUCE_LOG, Blocks.DARK_OAK_LOG);
        kievanMap.put(Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE);
        kievanMap.put(Blocks.COBBLESTONE_WALL, Blocks.COBBLED_DEEPSLATE_WALL);
        kievanMap.put(Blocks.STONE, Blocks.BLACKSTONE);
        kievanMap.put(Blocks.GRAVEL, Blocks.COARSE_DIRT);
        kievanMap.put(Blocks.DIRT_PATH, Blocks.GRAVEL);
        kievanMap.put(Blocks.ANDESITE, Blocks.COBBLED_DEEPSLATE);
        kievanMap.put(Blocks.BRICKS, Blocks.RED_NETHER_BRICKS);
        kievanMap.put(Blocks.BRICK_STAIRS, Blocks.RED_NETHER_BRICK_STAIRS);
        kievanMap.put(Blocks.WHITE_WOOL, Blocks.RED_CONCRETE);
        kievanMap.put(Blocks.OAK_FENCE, Blocks.NETHER_BRICK_FENCE);
        kievanMap.put(Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_STONE_BRICKS);
        THEME_MAPS.put(KIEVAN, kievanMap);

        // --- MOORISH (North African/Mediterranean Aesthetic) --- (NEW)
        Map<Block, Block> moorishMap = new HashMap<>();
        moorishMap.put(Blocks.OAK_PLANKS, Blocks.BIRCH_PLANKS);
        moorishMap.put(Blocks.OAK_LOG, Blocks.OAK_LOG);
        moorishMap.put(Blocks.BIRCH_PLANKS, Blocks.SANDSTONE);
        moorishMap.put(Blocks.SPRUCE_PLANKS, Blocks.CUT_SANDSTONE);
        moorishMap.put(Blocks.COBBLESTONE, Blocks.SMOOTH_SANDSTONE);
        moorishMap.put(Blocks.COBBLESTONE_WALL, Blocks.SANDSTONE_WALL);
        moorishMap.put(Blocks.STONE, Blocks.SMOOTH_STONE);
        moorishMap.put(Blocks.GRAVEL, Blocks.SAND);
        moorishMap.put(Blocks.DIRT_PATH, Blocks.RED_SANDSTONE);
        moorishMap.put(Blocks.ANDESITE, Blocks.SMOOTH_QUARTZ);
        moorishMap.put(Blocks.BRICKS, Blocks.ORANGE_TERRACOTTA);
        moorishMap.put(Blocks.BRICK_STAIRS, Blocks.ORANGE_TERRACOTTA);
        moorishMap.put(Blocks.WHITE_WOOL, Blocks.LIGHT_BLUE_CONCRETE);
        moorishMap.put(Blocks.RED_WOOL, Blocks.ORANGE_CONCRETE);
        THEME_MAPS.put(MOORISH, moorishMap);
    }

    /**
     * Gets the replacement block for a given original block based on the kingdom theme.
     * @param theme The kingdom theme (e.g., "Nordic").
     * @param original The block being swapped.
     * @return The replacement block, or the original block if no swap is defined.
     */
    public static Block getReplacement(String theme, Block original) {
        return THEME_MAPS.getOrDefault(theme, Map.of()).getOrDefault(original, original);
    }
}