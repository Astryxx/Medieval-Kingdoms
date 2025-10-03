package com.Astryxx.medievalkingdoms;

import com.Astryxx.medievalkingdoms.register.ModBlocks;
import com.Astryxx.medievalkingdoms.register.ModItems;
import com.Astryxx.medievalkingdoms.register.ModCreativeTabs;
import com.Astryxx.medievalkingdoms.register.ModProcessors;
import com.Astryxx.medievalkingdoms.register.ModBlockEntities;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the MedievalKingdoms mod.
 */
@Mod(MedievalKingdoms.MOD_ID)
public class MedievalKingdoms {
    public static final String MOD_ID = "medievalkingdoms";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @SuppressWarnings("removal")
    public MedievalKingdoms() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register Deferred Registers
        ModProcessors.PROCESSOR_TYPES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus); // NEW REGISTRY

        // Register ourselves with the event bus (for the RegisterCommandsEvent)
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Listener for command registration (MinecraftForge.EVENT_BUS)
    // The command registration method (ModCommands.register) is called directly
    // in the event handler, so the imports for ModCommands and RegisterCommandsEvent are no longer necessary in this class.
    @SubscribeEvent
    public void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent event) {
        com.Astryxx.medievalkingdoms.commands.ModCommands.register(event.getDispatcher(), event.getBuildContext());
    }
}