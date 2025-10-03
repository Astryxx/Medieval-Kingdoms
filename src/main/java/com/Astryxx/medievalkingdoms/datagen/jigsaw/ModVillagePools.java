package com.Astryxx.medievalkingdoms.datagen.jigsaw;

import com.Astryxx.medievalkingdoms.datagen.processor_lists.ModStructureProcessorLists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.core.registries.Registries;

import java.util.List;
import java.util.function.Function;

/**
 * Utility class containing the bootstrap logic for our custom Jigsaw Structure Template Pools.
 * This class is now called directly by ModDataGenerators.
 */
public class ModVillagePools {

    // Vanilla Village Pool Locations (The entry points we overwrite)
    private static final ResourceLocation PLAINS_VILLAGE = ResourceLocation.withDefaultNamespace("village/plains/houses");
    private static final ResourceLocation DESERT_VILLAGE = ResourceLocation.withDefaultNamespace("village/desert/houses");
    private static final ResourceLocation SAVANNA_VILLAGE = ResourceLocation.withDefaultNamespace("village/savanna/houses");
    private static final ResourceLocation SNOWY_VILLAGE = ResourceLocation.withDefaultNamespace("village/snowy/houses");
    private static final ResourceLocation TAIGA_VILLAGE = ResourceLocation.withDefaultNamespace("village/taiga/houses");


    /**
     * Helper to define a new pool entry with a specific processor list applied,
     * returning the required Pair with weight 1 (standard probability).
     */
    private static Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer> addThemedProcessor(String structureName, Holder<StructureProcessorList> processor) {
        return Pair.of(StructurePoolElement.single(structureName, processor), 1);
    }

    /**
     * Overwrites vanilla village pool entries to use our custom processor lists.
     * This method is now called directly by ModDataGenerators during registration.
     */
    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        var processors = context.lookup(Registries.PROCESSOR_LIST);

        // Get the holders for all five themed processor lists
        Holder<StructureProcessorList> nordic = processors.getOrThrow(ModStructureProcessorLists.NORDIC_PROCESSORS);
        Holder<StructureProcessorList> wessex = processors.getOrThrow(ModStructureProcessorLists.WESSEX_PROCESSORS);
        Holder<StructureProcessorList> byzantine = processors.getOrThrow(ModStructureProcessorLists.BYZANTINE_PROCESSORS);
        Holder<StructureProcessorList> kievan = processors.getOrThrow(ModStructureProcessorLists.KIEVAN_PROCESSORS);
        Holder<StructureProcessorList> moorish = processors.getOrThrow(ModStructureProcessorLists.MOORISH_PROCESSORS);

        // Use ResourceKey to access the vanilla terminators pool
        ResourceKey<StructureTemplatePool> plainsTerminatorsKey = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/plains/terminators"));
        ResourceKey<StructureTemplatePool> desertTerminatorsKey = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/desert/terminators"));
        ResourceKey<StructureTemplatePool> savannaTerminatorsKey = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/savanna/terminators"));
        ResourceKey<StructureTemplatePool> snowyTerminatorsKey = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/snowy/terminators"));
        ResourceKey<StructureTemplatePool> taigaTerminatorsKey = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/taiga/terminators"));

        // --- PLAINS VILLAGES ---

        context.register(ResourceKey.create(Registries.TEMPLATE_POOL, PLAINS_VILLAGE),
                new StructureTemplatePool(
                        pools.getOrThrow(plainsTerminatorsKey),
                        List.of(
                                // Nordic Theme Injection (Plains)
                                addThemedProcessor("minecraft:village/plains/houses/plains_armorer", nordic),
                                addThemedProcessor("minecraft:village/plains/houses/plains_butcher", nordic),

                                // Wessex Theme Injection (Plains)
                                addThemedProcessor("minecraft:village/plains/houses/plains_fletcher", wessex),
                                addThemedProcessor("minecraft:village/plains/houses/plains_mason", wessex),

                                // Byzantine Theme Injection (Plains)
                                addThemedProcessor("minecraft:village/plains/houses/plains_shepherd", byzantine),
                                addThemedProcessor("minecraft:village/plains/houses/plains_stable", byzantine),

                                // Kievan Theme Injection (Plains)
                                addThemedProcessor("minecraft:village/plains/houses/plains_tool_smith", kievan),
                                addThemedProcessor("minecraft:village/plains/houses/plains_weaponsmith", kievan),

                                // Moorish Theme Injection (Plains)
                                addThemedProcessor("minecraft:village/plains/houses/plains_medium_house_1", moorish),
                                addThemedProcessor("minecraft:village/plains/houses/plains_medium_house_2", moorish)
                        ),
                        StructureTemplatePool.Projection.RIGID
                )
        );

        // --- DESERT VILLAGES ---

        context.register(ResourceKey.create(Registries.TEMPLATE_POOL, DESERT_VILLAGE),
                new StructureTemplatePool(
                        pools.getOrThrow(desertTerminatorsKey),
                        List.of(
                                // Byzantine Theme Injection (Desert) - Primary Theme
                                addThemedProcessor("minecraft:village/desert/houses/desert_armorer", byzantine),
                                addThemedProcessor("minecraft:village/desert/houses/desert_butcher", byzantine),
                                addThemedProcessor("minecraft:village/desert/houses/desert_fletcher", byzantine),

                                // Moorish Theme Injection (Desert) - Secondary Theme
                                addThemedProcessor("minecraft:village/desert/houses/desert_mason", moorish),
                                addThemedProcessor("minecraft:village/desert/houses/desert_shepherd", moorish),

                                // Wessex as a randomized option
                                addThemedProcessor("minecraft:village/desert/houses/desert_tool_smith", wessex)
                        ),
                        StructureTemplatePool.Projection.RIGID
                )
        );

        // --- SAVANNA VILLAGES ---

        context.register(ResourceKey.create(Registries.TEMPLATE_POOL, SAVANNA_VILLAGE),
                new StructureTemplatePool(
                        pools.getOrThrow(savannaTerminatorsKey),
                        List.of(
                                // Moorish Theme Injection (Savanna) - Primary Theme
                                addThemedProcessor("minecraft:village/savanna/houses/savanna_armorer", moorish),
                                addThemedProcessor("minecraft:village/savanna/houses/savanna_butcher", moorish),
                                addThemedProcessor("minecraft:village/savanna/houses/savanna_fletcher", moorish),

                                // Byzantine Theme Injection (Savanna) - Secondary Theme
                                addThemedProcessor("minecraft:village/savanna/houses/savanna_mason", byzantine),
                                addThemedProcessor("minecraft:village/savanna/houses/savanna_shepherd", byzantine),

                                // Kievan as a randomized option
                                addThemedProcessor("minecraft:village/savanna/houses/savanna_tool_smith", kievan)
                        ),
                        StructureTemplatePool.Projection.RIGID
                )
        );

        // -------------------------------------------------------------------------------------------------------------

        // --- SNOWY VILLAGES (Nordic Primary Theme) ---

        context.register(ResourceKey.create(Registries.TEMPLATE_POOL, SNOWY_VILLAGE),
                new StructureTemplatePool(
                        pools.getOrThrow(snowyTerminatorsKey),
                        List.of(
                                // Nordic Theme Injection (Snowy) - Primary Theme for cold biomes
                                addThemedProcessor("minecraft:village/snowy/houses/snowy_armorer", nordic),
                                addThemedProcessor("minecraft:village/snowy/houses/snowy_butcher", nordic),
                                addThemedProcessor("minecraft:village/snowy/houses/snowy_fletcher", nordic),

                                // Kievan Theme Injection (Snowy) - Secondary Theme (Eastern European cold climate)
                                addThemedProcessor("minecraft:village/snowy/houses/snowy_mason", kievan),
                                addThemedProcessor("minecraft:village/snowy/houses/snowy_shepherd", kievan),

                                // Wessex as a randomized option
                                addThemedProcessor("minecraft:village/snowy/houses/snowy_tool_smith", wessex)
                        ),
                        StructureTemplatePool.Projection.RIGID
                )
        );

        // -------------------------------------------------------------------------------------------------------------

        // --- TAIGA VILLAGES (Nordic Primary Theme) ---

        context.register(ResourceKey.create(Registries.TEMPLATE_POOL, TAIGA_VILLAGE),
                new StructureTemplatePool(
                        pools.getOrThrow(taigaTerminatorsKey),
                        List.of(
                                // Nordic Theme Injection (Taiga) - Primary Theme for cold biomes
                                addThemedProcessor("minecraft:village/taiga/houses/taiga_armorer", nordic),
                                addThemedProcessor("minecraft:village/taiga/houses/taiga_butcher", nordic),
                                addThemedProcessor("minecraft:village/taiga/houses/taiga_fletcher", nordic),

                                // Kievan Theme Injection (Taiga) - Secondary Theme (Eastern European forest/cold climate)
                                addThemedProcessor("minecraft:village/taiga/houses/taiga_mason", kievan),
                                addThemedProcessor("minecraft:village/taiga/houses/taiga_shepherd", kievan),

                                // Wessex as a randomized option
                                addThemedProcessor("minecraft:village/taiga/houses/taiga_tool_smith", wessex)
                        ),
                        StructureTemplatePool.Projection.RIGID
                )
        );
    }
}