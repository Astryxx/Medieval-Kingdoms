package com.Astryxx.medievalkingdoms.register;

import com.Astryxx.medievalkingdoms.MedievalKingdoms;
import com.Astryxx.medievalkingdoms.world.KingdomStructureProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers custom Structure Processors used for theme-based block swapping.
 */
public class ModProcessors {
    // DeferredRegister for StructureProcessorType
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSOR_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, MedievalKingdoms.MOD_ID);

    // Our custom processor type
    public static final RegistryObject<StructureProcessorType<KingdomStructureProcessor>> KINGDOM_PROCESSOR =
            PROCESSOR_TYPES.register("kingdom_processor", () -> () -> KingdomStructureProcessor.CODEC);
}
