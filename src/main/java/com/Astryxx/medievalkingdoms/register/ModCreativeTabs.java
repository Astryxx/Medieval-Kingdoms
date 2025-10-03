package com.Astryxx.medievalkingdoms.register;

import com.Astryxx.medievalkingdoms.MedievalKingdoms;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers the custom Creative Mode Tabs for the mod.
 */
public class ModCreativeTabs {
    // DeferredRegister for CreativeModeTab
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MedievalKingdoms.MOD_ID);

    // Register the main "Medieval Kingdoms" tab
    @SuppressWarnings("unused")
    public static final RegistryObject<CreativeModeTab> MEDIEVAL_TAB = CREATIVE_MODE_TABS.register("medieval_tab", () ->
            CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.ROYAL_THRONE.get()))
                    .title(Component.translatable("itemGroup." + MedievalKingdoms.MOD_ID + ".medieval_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // --- Add All Custom Block Items Here (UPDATED) ---
                        output.accept(ModItems.ROYAL_THRONE.get());
                        output.accept(ModItems.LAW_BOARD.get());
                        output.accept(ModItems.BANKER_DESK.get());
                        output.accept(ModItems.ELECTION_BOOTH.get()); // NEW
                        output.accept(ModItems.FORGERY_BOARD.get()); // NEW
                    })
                    .build()
    );
}