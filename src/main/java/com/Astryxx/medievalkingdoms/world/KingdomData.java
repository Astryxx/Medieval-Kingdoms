package com.Astryxx.medievalkingdoms.world;

import com.Astryxx.medievalkingdoms.MedievalKingdoms;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Manages persistent world data for Kingdoms, including creation, saving, and loading.
 * This class determines which villages belong to which kingdom.
 */
@SuppressWarnings({"unused", "RedundantSuppression", "MismatchedQueryAndUpdateOfCollection"})
public class KingdomData extends SavedData {
    private static final String DATA_NAME = "medieval_kingdoms_data";
    private static final double KINGDOM_PROXIMITY_SQ = 500000.0; // Proximity threshold (500 blocks squared)

    // Map: Kingdom UUID -> Kingdom object
    private final Map<UUID, Kingdom> kingdoms = new HashMap<>();

    // Map: Village BlockPos -> Kingdom UUID (for quick lookup)
    private final Map<BlockPos, UUID> trackedVillages = new HashMap<>();

    // --- Data Management (Loading/Saving) ---
    public static KingdomData load(CompoundTag pTag) {
        KingdomData data = new KingdomData();
        ListTag kingdomsTag = pTag.getList("Kingdoms", Tag.TAG_COMPOUND);
        for (Tag tag : kingdomsTag) {
            CompoundTag kingdomCompound = (CompoundTag) tag;
            Kingdom kingdom = Kingdom.load(kingdomCompound);
            data.kingdoms.put(kingdom.getId(), kingdom);

            ListTag villagesTag = kingdomCompound.getList("Villages", Tag.TAG_LONG);
            for (Tag posTag : villagesTag) {
                if (posTag instanceof LongTag longTag) {
                    BlockPos pos = BlockPos.of(longTag.getAsLong());
                    data.trackedVillages.put(pos, kingdom.getId());
                }
            }
        }
        MedievalKingdoms.LOGGER.info("Loaded {} Kingdoms from world data.", data.kingdoms.size());
        return data;
    }

    @Override
    @NotNull
    public CompoundTag save(@NotNull CompoundTag pCompoundTag) {
        ListTag kingdomsTag = new ListTag();
        for (Kingdom kingdom : this.kingdoms.values()) {
            kingdomsTag.add(kingdom.save());
        }
        pCompoundTag.put("Kingdoms", kingdomsTag);
        return pCompoundTag;
    }

    // --- Access and Retrieval ---

    @NotNull
    public static KingdomData get(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(ServerLevel.OVERWORLD);
        if (overworld == null) {
            throw new IllegalStateException("Overworld level not found during KingdomData initialization.");
        }
        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(KingdomData::load, KingdomData::new, DATA_NAME);
    }

    public Map<UUID, Kingdom> getKingdoms() {
        return this.kingdoms;
    }

    // --- Kingdom Logic ---

    public boolean isVillageTracked(BlockPos villagePos) {
        return this.trackedVillages.containsKey(villagePos);
    }

    public void assignVillageToKingdom(BlockPos newVillagePos, String villageType) {
        if (isVillageTracked(newVillagePos)) {
            return;
        }

        // 1. Check for proximity to existing kingdoms
        for (Kingdom kingdom : this.kingdoms.values()) {
            double distanceSq = kingdom.getCenterPosition().distSqr(newVillagePos);

            if (distanceSq <= KINGDOM_PROXIMITY_SQ) {
                kingdom.addVillage(newVillagePos);
                this.trackedVillages.put(newVillagePos, kingdom.getId());
                MedievalKingdoms.LOGGER.info("Village at {} joined Kingdom {} (Distance: {} blocks)",
                        newVillagePos.toShortString(), kingdom.getName(), Math.sqrt(distanceSq));
                this.setDirty();
                return;
            }
        }

        // 2. If no nearby kingdom, create a new one, using the village type to set the theme
        createNewKingdom(newVillagePos, villageType);
        this.setDirty();
    }

    private void createNewKingdom(BlockPos centerPos, String villageType) {
        UUID newId = UUID.randomUUID();
        String name = KingdomNameGenerator.generateRandomName();
        String theme = getThemeForVillageType(villageType);
        String color = KingdomNameGenerator.pickRandomColor();

        Kingdom newKingdom = new Kingdom(newId, name, centerPos, theme, color);
        newKingdom.addVillage(centerPos);

        this.kingdoms.put(newId, newKingdom);
        this.trackedVillages.put(centerPos, newId);

        MedievalKingdoms.LOGGER.info("Created new Kingdom: {} at {} with theme {} (from village type {}) and color {}", name, centerPos.toShortString(), theme, villageType, color);
    }

    /**
     * Determines the kingdom theme based on the type of vanilla village (biome or structure type).
     */
    private String getThemeForVillageType(String villageType) {
        // Themes used for specific biomes
        return switch (villageType) {
            case "taiga", "snowy" -> ModBlockMappings.NORDIC; // Nordic for cold biomes
            case "desert" -> ModBlockMappings.BYZANTINE; // Byzantine for arid biomes
            case "savanna" -> ModBlockMappings.MOORISH; // Moorish for savanna biomes
            case "plains" -> ModBlockMappings.KIEVAN; // Plains now exclusively gets Kievan
            default -> {
                // If the type is unrecognized (modded village or unknown vanilla type),
                // assign a versatile theme randomly from the most neutral options.
                java.util.List<String> fallbackOptions = java.util.List.of(
                        ModBlockMappings.WESSEX, // Good general fallback
                        ModBlockMappings.MOORISH // Good diverse biome fallback
                );
                yield fallbackOptions.get(ThreadLocalRandom.current().nextInt(fallbackOptions.size()));
            }
        };
    }

    public String getThemeForVillage(BlockPos villagePos) {
        UUID kingdomId = this.trackedVillages.get(villagePos);
        if (kingdomId != null && this.kingdoms.containsKey(kingdomId)) {
            return this.kingdoms.get(kingdomId).getTheme();
        }

        MedievalKingdoms.LOGGER.warn("Could not find Kingdom for village at {}. Using default theme.", villagePos.toShortString());
        return Kingdom.DEFAULT_THEME;
    }

    public static String pickRandomThemeStatic() {
        Object[] themes = ModBlockMappings.ALL_THEMES.toArray();
        if (themes.length == 0) {
            return Kingdom.DEFAULT_THEME;
        }
        int index = new java.util.Random().nextInt(themes.length);
        return (String) themes[index];
    }
}
