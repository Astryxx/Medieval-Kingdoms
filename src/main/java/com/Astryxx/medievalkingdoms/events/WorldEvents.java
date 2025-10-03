package com.Astryxx.medievalkingdoms.events;

import com.Astryxx.medievalkingdoms.MedievalKingdoms;
import com.Astryxx.medievalkingdoms.world.KingdomData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
// REMOVED: import net.minecraft.resources.ResourceLocation; (Unused)
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Handles world events, primarily detecting newly generated villages to assign them to kingdoms.
 * This class is updated to detect all vanilla AND modded Jigsaw villages.
 */
@Mod.EventBusSubscriber(modid = MedievalKingdoms.MOD_ID)
public class WorldEvents {

    /**
     * Extracts the village type (e.g., "plains" from "village_plains") from the structure's ResourceKey path.
     * This method is extracted to resolve the readability warning in onChunkLoad.
     */
    private static String getVillageTypeFromKey(ResourceKey<Structure> key) {
        String fullPath = key.location().getPath();

        // Try to find the substring after the last underscore (e.g., '_plains')
        int lastUnderscore = fullPath.lastIndexOf('_');
        if (lastUnderscore != -1 && lastUnderscore < fullPath.length() - 1) {
            return fullPath.substring(lastUnderscore + 1);
        } else {
            // Fallback for modded or non-standard naming (KingdomData's switch handles the default)
            return fullPath;
        }
    }


    /**
     * Fired when a chunk is loaded. We use this to check for structures that have just been generated.
     */
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel().isClientSide() || !(event.getChunk() instanceof LevelChunk levelChunk)) {
            return;
        }

        if (levelChunk.getLevel().getServer() == null) {
            return;
        }

        Map<Structure, StructureStart> structureStarts = levelChunk.getAllStarts();

        if (!structureStarts.isEmpty()) {

            // NEW LOGIC: Filter structures based on the path name, generalizing detection to modded villages.
            Optional<ResourceKey<Structure>> villageKey = structureStarts.keySet().stream()
                    .map(structure -> Objects.requireNonNull(levelChunk.getLevel().getServer()).registryAccess()
                            .registryOrThrow(Registries.STRUCTURE)
                            .getResourceKey(structure)
                            .orElse(null))
                    .filter(Objects::nonNull) // Replaced lambda with method reference
                    .filter(key -> {
                        String path = key.location().getPath();
                        // The primary check: does the path contain 'village'?
                        return path.contains("village");
                    })
                    .findFirst();

            if (villageKey.isPresent()) {
                // Get the actual Structure object using the key
                Structure structure = Objects.requireNonNull(levelChunk.getLevel().getServer()).registryAccess()
                        .registryOrThrow(Registries.STRUCTURE)
                        .get(villageKey.get());

                StructureStart start = structureStarts.get(structure);
                if (start == null) return;

                BlockPos villageCenter = start.getBoundingBox().getCenter();
                KingdomData kingdomData = KingdomData.get(Objects.requireNonNull(levelChunk.getLevel().getServer()));

                if (!kingdomData.isVillageTracked(villageCenter)) {

                    // Call the new extracted method
                    String villageType = getVillageTypeFromKey(villageKey.get());

                    // Assign the village to a new or existing kingdom
                    kingdomData.assignVillageToKingdom(villageCenter, villageType);
                }
            }
        }
    }
}
