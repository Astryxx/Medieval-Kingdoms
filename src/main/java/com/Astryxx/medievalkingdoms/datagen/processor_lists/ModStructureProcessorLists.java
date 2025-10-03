package com.Astryxx.medievalkingdoms.datagen.processor_lists;

import com.Astryxx.medievalkingdoms.MedievalKingdoms;
import com.Astryxx.medievalkingdoms.world.KingdomStructureProcessor;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.core.registries.Registries;

import java.util.List;

/**
 * Utility class defining the ResourceKeys and the bootstrap logic for our themed StructureProcessorLists.
 * This class is now called directly by ModDataGenerators.
 */
public class ModStructureProcessorLists {

    // Resource Keys for our five themed processor lists
    public static final ResourceKey<StructureProcessorList> NORDIC_PROCESSORS =
            createKey("nordic_processors");
    public static final ResourceKey<StructureProcessorList> WESSEX_PROCESSORS =
            createKey("wessex_processors");
    public static final ResourceKey<StructureProcessorList> BYZANTINE_PROCESSORS =
            createKey("byzantine_processors");
    public static final ResourceKey<StructureProcessorList> KIEVAN_PROCESSORS = // KEPT
            createKey("kievan_processors");
    public static final ResourceKey<StructureProcessorList> MOORISH_PROCESSORS = // ADDED
            createKey("moorish_processors");

    private static ResourceKey<StructureProcessorList> createKey(String name) {
        return ResourceKey.create(Registries.PROCESSOR_LIST, ResourceLocation.fromNamespaceAndPath(MedievalKingdoms.MOD_ID, name));
    }

    /**
     * Boostrap method where the actual StructureProcessorLists are defined.
     * This method is now called directly by ModDataGenerators during registration.
     */
    public static void bootstrap(BootstapContext<StructureProcessorList> context) {

        // 1. NORDIC THEME PROCESSOR LIST
        context.register(NORDIC_PROCESSORS, new StructureProcessorList(List.of(
                new KingdomStructureProcessor("Nordic")
        )));

        // 2. WESSEX THEME PROCESSOR LIST
        context.register(WESSEX_PROCESSORS, new StructureProcessorList(List.of(
                new KingdomStructureProcessor("Wessex")
        )));

        // 3. BYZANTINE THEME PROCESSOR LIST
        context.register(BYZANTINE_PROCESSORS, new StructureProcessorList(List.of(
                new KingdomStructureProcessor("Byzantine")
        )));

        // 4. KIEVAN THEME PROCESSOR LIST (Kept)
        context.register(KIEVAN_PROCESSORS, new StructureProcessorList(List.of(
                new KingdomStructureProcessor("Kievan")
        )));

        // 5. MOORISH THEME PROCESSOR LIST (New)
        context.register(MOORISH_PROCESSORS, new StructureProcessorList(List.of(
                new KingdomStructureProcessor("Moorish")
        )));
    }
}