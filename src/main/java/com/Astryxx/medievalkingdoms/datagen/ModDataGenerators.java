package com.Astryxx.medievalkingdoms.datagen;

import com.Astryxx.medievalkingdoms.MedievalKingdoms;
import com.Astryxx.medievalkingdoms.datagen.jigsaw.ModVillagePools;
import com.Astryxx.medievalkingdoms.datagen.processor_lists.ModStructureProcessorLists;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

/**
 * Main entry point for all data generation. This class registers all custom
 * data providers (Processor Lists and Jigsaw Pools) in a single consolidated provider.
 */
@Mod.EventBusSubscriber(modid = MedievalKingdoms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerators {

    /**
     * Subscribe to the event bus to start data generation.
     */
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        // FIX: Consolidate all data generation into a single DatapackBuiltinEntriesProvider instance.
        // This prevents the "Duplicate provider: Registries" error that occurs when multiple
        // providers attempt to register built-in registries separately on Forge 1.20.1.
        generator.addProvider(event.includeServer(),
                new DatapackBuiltinEntriesProvider(
                        packOutput,
                        event.getLookupProvider(),
                        new RegistrySetBuilder()
                                // 1. Add Structure Processor Lists (using the bootstrap method from the utility class)
                                .add(Registries.PROCESSOR_LIST, ModStructureProcessorLists::bootstrap)
                                // 2. Add Jigsaw Template Pools (using the bootstrap method from the utility class)
                                .add(Registries.TEMPLATE_POOL, ModVillagePools::bootstrap),
                        Set.of(MedievalKingdoms.MOD_ID)
                )
        );

        // NOTE: The lines:
        // generator.addProvider(event.includeServer(), new ModStructureProcessorLists(...));
        // generator.addProvider(event.includeServer(), new ModVillagePools(...));
        // have been removed/replaced by the single consolidated call above.

        MedievalKingdoms.LOGGER.info("Registered all custom data generators (Processors and Jigsaw Pools) in a consolidated provider.");
    }
}
