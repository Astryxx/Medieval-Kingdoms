package com.Astryxx.medievalkingdoms.commands;

import com.Astryxx.medievalkingdoms.world.Kingdom;
import com.Astryxx.medievalkingdoms.world.KingdomData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;

/**
 * Registers all mod commands, including debugging tools for Kingdoms.
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ModCommands {

    // FIX: Removed Commands.CommandSelection selection parameter to match the simplified event signature (two arguments)
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("kingdom")
                .requires(source -> source.hasPermission(2)) // Require Op or Creative mode
                .then(Commands.literal("list")
                        .executes(ModCommands::listAllKingdoms)
                )
                .then(Commands.literal("tp")
                        .then(Commands.argument("kingdom_id", StringArgumentType.string())
                                .executes(ModCommands::teleportToKingdom)
                        )
                )
        );
    }

    private static int listAllKingdoms(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        // FIX: Removed redundant check: source.getServer() is guaranteed non-null in command context
        KingdomData data = KingdomData.get(source.getServer());
        Map<UUID, Kingdom> kingdoms = data.getKingdoms();

        if (kingdoms.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No kingdoms generated yet."), false);
            return 1;
        }

        source.sendSuccess(() -> Component.literal("--- Found " + kingdoms.size() + " Kingdoms ---").withStyle(ChatFormatting.GOLD), false);

        kingdoms.forEach((uuid, kingdom) -> {
            MutableComponent kingdomInfo = Component.literal(kingdom.getName() + " ")
                    .withStyle(ChatFormatting.AQUA)
                    .append(Component.literal("[" + kingdom.getTheme() + "] ")
                            .withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("Center: " + kingdom.getCenterPosition().toShortString())
                            .withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal(" ID: " + kingdom.getId().toString().substring(0, 8))
                            .withStyle(ChatFormatting.DARK_GRAY));
            source.sendSuccess(() -> kingdomInfo, false);
        });

        return kingdoms.size();
    }

    private static int teleportToKingdom(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String idString = StringArgumentType.getString(context, "kingdom_id");
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("This command must be run by a player."));
            return 0;
        }
        // FIX: Removed redundant check: source.getServer() is guaranteed non-null in command context
        KingdomData data = KingdomData.get(source.getServer());
        UUID targetId = null;

        // Simple lookup using the first part of the UUID or full UUID
        for (UUID uuid : data.getKingdoms().keySet()) {
            if (uuid.toString().startsWith(idString)) {
                targetId = uuid;
                break;
            }
        }

        if (targetId != null) {
            Kingdom kingdom = data.getKingdoms().get(targetId);

            // Teleport the player safely to slightly above the center block
            BlockPos center = kingdom.getCenterPosition();
            player.teleportTo(
                    source.getLevel(),
                    center.getX() + 0.5,
                    center.getY() + 3.0,
                    center.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );

            source.sendSuccess(() -> Component.literal("Teleported to Kingdom: " + kingdom.getName()).withStyle(ChatFormatting.GREEN), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Kingdom with ID starting with '" + idString + "' not found."));
            return 0;
        }
    }
}
