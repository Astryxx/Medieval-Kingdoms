package com.Astryxx.medievalkingdoms.world;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a single Medieval Kingdom entity in the world.
 * Stores its unique ID, name, central position, and theme.
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class Kingdom {
    public static final String DEFAULT_THEME = "Wessex";
    public static final String DEFAULT_COLOR = "Red";

    private final UUID id;
    private String name;
    private BlockPos centerPosition;
    private String theme;
    private final String bannerColor; // Color for banners/accents
    private UUID rulerId; // The UUID of the player ruler
    private final Map<UUID, String> nobility; // Map of Noble UUIDs to their title (e.g., "Baron")
    private final List<BlockPos> villagePositions; // Positions of villages belonging to this kingdom

    public Kingdom(UUID id, String name, BlockPos centerPosition, String theme, String bannerColor) {
        this.id = id;
        this.name = name;
        this.centerPosition = centerPosition;
        this.theme = theme;
        this.bannerColor = bannerColor;
        this.rulerId = null; // Default: no ruler assigned on creation
        this.nobility = new HashMap<>();
        this.villagePositions = new ArrayList<>();
    }

    // --- Serialization ---

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", this.id);
        tag.putString("Name", this.name);
        tag.putLong("CenterPosition", this.centerPosition.asLong());
        tag.putString("Theme", this.theme);
        tag.putString("BannerColor", this.bannerColor);

        // SAVE: Ruler ID (Optional)
        if (this.rulerId != null) {
            tag.putUUID("RulerId", this.rulerId);
        }

        // SAVE: Nobility Map (UUID to String)
        ListTag nobilityTag = new ListTag();
        this.nobility.forEach((uuid, title) -> {
            CompoundTag nobleEntry = new CompoundTag();
            nobleEntry.putUUID("UUID", uuid);
            nobleEntry.putString("Title", title);
            nobilityTag.add(nobleEntry);
        });
        tag.put("Nobility", nobilityTag);

        // Serialize village positions as a list of Longs (BlockPos as long)
        ListTag villagesTag = new ListTag();
        for (BlockPos pos : this.villagePositions) {
            villagesTag.add(LongTag.valueOf(pos.asLong()));
        }
        tag.put("Villages", villagesTag);

        return tag;
    }

    public static Kingdom load(CompoundTag tag) {
        UUID id = tag.getUUID("Id");
        String name = tag.getString("Name");
        BlockPos centerPos = BlockPos.of(tag.getLong("CenterPosition"));
        String theme = tag.getString("Theme");
        String bannerColor = tag.getString("BannerColor");

        Kingdom kingdom = new Kingdom(id, name, centerPos, theme, bannerColor);

        // LOAD: Ruler ID
        if (tag.contains("RulerId")) {
            kingdom.rulerId = tag.getUUID("RulerId");
        }

        // LOAD: Nobility Map
        ListTag nobilityTag = tag.getList("Nobility", Tag.TAG_COMPOUND);
        for (Tag nobleEntryTag : nobilityTag) {
            CompoundTag nobleEntry = (CompoundTag) nobleEntryTag;
            UUID nobleUuid = nobleEntry.getUUID("UUID");
            String title = nobleEntry.getString("Title");
            kingdom.nobility.put(nobleUuid, title);
        }

        // Deserialize village positions
        ListTag villagesTag = tag.getList("Villages", Tag.TAG_LONG);
        for (Tag posTag : villagesTag) {
            if (posTag instanceof LongTag longTag) {
                kingdom.villagePositions.add(BlockPos.of(longTag.getAsLong()));
            }
        }

        return kingdom;
    }

    // --- Getters and Setters (Used for future commands/UIs) ---

    public UUID getId() { return id; }
    public String getName() { return name; }
    public BlockPos getCenterPosition() { return centerPosition; }
    public String getTheme() { return theme; }
    public String getBannerColor() { return bannerColor; }
    public UUID getRulerId() { return rulerId; }
    public Map<UUID, String> getNobility() { return nobility; }
    public List<BlockPos> getVillagePositions() { return villagePositions; }

    public void addVillage(BlockPos pos) {
        if (!this.villagePositions.contains(pos)) {
            this.villagePositions.add(pos);
        }
    }

    public void setName(String name) { this.name = name; }
    public void setCenterPosition(BlockPos centerPosition) { this.centerPosition = centerPosition; }
    public void setTheme(String theme) { this.theme = theme; }
    public void setRulerId(UUID rulerId) { this.rulerId = rulerId; }
}
