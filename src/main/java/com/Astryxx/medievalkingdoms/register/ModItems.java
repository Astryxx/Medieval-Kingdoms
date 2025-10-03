package com.Astryxx.medievalkingdoms.register;

import com.Astryxx.medievalkingdoms.MedievalKingdoms;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers all custom items for the Medieval Kingdoms mod.
 */
public class ModItems {
    // DeferredRegister for Items
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MedievalKingdoms.MOD_ID);

    // Helper method to register standard Block Items
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // --- Block Items ---

    public static final RegistryObject<Item> ROYAL_THRONE = registerBlockItem("royal_throne", ModBlocks.ROYAL_THRONE);

    public static final RegistryObject<Item> LAW_BOARD = registerBlockItem("law_board", ModBlocks.LAW_BOARD);

    public static final RegistryObject<Item> BANKER_DESK = registerBlockItem("banker_desk", ModBlocks.BANKER_DESK);

    public static final RegistryObject<Item> ELECTION_BOOTH = registerBlockItem("election_booth", ModBlocks.ELECTION_BOOTH);

    public static final RegistryObject<Item> FORGERY_TABLE = registerBlockItem("forgery_table", ModBlocks.FORGERY_TABLE);
}