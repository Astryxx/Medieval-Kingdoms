package com.Astryxx.medievalkingdoms.world;

import com.Astryxx.medievalkingdoms.register.ModProcessors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom structure processor used to replace vanilla village blocks with
 * theme-specific vanilla blocks defined in ModBlockMappings.
 */
public class KingdomStructureProcessor extends StructureProcessor {
    public static final Codec<KingdomStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("kingdom_theme").forGetter(KingdomStructureProcessor::getTheme)
    ).apply(instance, KingdomStructureProcessor::new));

    private final String kingdomTheme;

    public KingdomStructureProcessor(String kingdomTheme) {
        this.kingdomTheme = kingdomTheme;
    }

    public String getTheme() {
        return kingdomTheme;
    }

    // --- StructureProcessor Overrides ---

    @Override
    @NotNull
    public StructureProcessorType<?> getType() {
        return ModProcessors.KINGDOM_PROCESSOR.get();
    }

    /**
     * The core logic for swapping blocks during structure placement.
     */
    @Nullable
    @SuppressWarnings({"unused", "deprecation"})
    public StructureBlockInfo processBlock(
            @NotNull LevelReader levelReader,
            @NotNull BlockPos pos,
            @NotNull BlockPos centerPos,
            @NotNull StructureBlockInfo originalInfo,
            @NotNull StructureBlockInfo structureInfo,
            @NotNull StructurePlaceSettings settings
    ) {
        // Accessors are correct: state()
        Block originalBlock = originalInfo.state().getBlock();
        Block replacementBlock = ModBlockMappings.getReplacement(this.kingdomTheme, originalBlock);

        // If the replacement is the same as the original, don't swap
        if (originalBlock.defaultBlockState().is(replacementBlock.defaultBlockState().getBlock())) {
            return structureInfo;
        }

        // Constructor uses pos(), defaultBlockState(), nbt()
        return new StructureBlockInfo(
                structureInfo.pos(),
                replacementBlock.defaultBlockState(),
                structureInfo.nbt()
        );
    }
}
