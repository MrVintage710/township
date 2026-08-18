package com.mrvintage.township.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrvintage.township.Township;
import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.profession.ProfessionProgress;
import com.mrvintage.township.registry.DataAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Township.MODID, value = Dist.DEDICATED_SERVER)
public class ModCommands {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal(Township.MODID)
                .then(Commands.literal("progress")
                    .then(Commands.literal("query")
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("meritPath", ResourceLocationArgument.id())
                                .executes(ModCommands::queryMeritProgressCommand)
                            )
                            .executes(ModCommands::queryAllMeritProgressCommand)
                        )
                    )
                    .then(Commands.literal("clear")
                        .executes(ModCommands::clearAllMeritProgressFromSelfCommand)
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("meritPath", ResourceLocationArgument.id())
                                .executes(ModCommands::clearMeritProgressFromPlayerCommand)
                            )
                            .executes(ModCommands::clearAllMeritProgressFromPlayerCommand)
                        )
                    )
                    .then(Commands.literal("grant")
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("meritPath", ResourceLocationArgument.id())
                                .then(Commands.argument("isFake", BoolArgumentType.bool())
                                    .executes(ModCommands::grantMeritToPlayerFake)
                                ).executes(ModCommands::grantMeritToPlayer)
                            )
                        )
                    )
                )
        );
    }

    private static int queryAllMeritProgressCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");

        for(Merit.Path path : ProfessionProgress.progressOf(player).all()) {
            printProgress(player, path);
        }

        return 0;
    }

    private static int queryMeritProgressCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        ResourceLocation resourceLocation = context.getArgument("meritPath", ResourceLocation.class);
        Merit.Path meritPath = Merit.Path.from(resourceLocation);

        if (player.hasData(DataAttachments.PROFESSION_PROGRESS)) {
            printProgress(player, meritPath);
        } else {
            player.sendSystemMessage(Component.literal("There is no progress for the given merit."));
        }

        return 0;
    }

    private static void printProgress(ServerPlayer player, Merit.Path path) {
        Merit merit = Profession.findMerit(player.serverLevel(), path);
        ProfessionProgress progress = ProfessionProgress.progressOf(player);

        if(progress.isInProgress(path) && merit != null) {
            var meritProgress = progress.getInProgress(path);
            player.sendSystemMessage(
                Component.literal(path.merit() +": ").withColor(0x314878).withStyle(ChatFormatting.BOLD)
                    .append(meritProgress.getXp() + "/" + merit.xp())
            );
        }

        else if (progress.isDone(path)) {
            player.sendSystemMessage(
                Component.literal(path.merit() +": ").withColor(0x314878).withStyle(ChatFormatting.BOLD)
                    .append("Done!").withColor(0x92d923)
            );
        }

        else {
            player.sendSystemMessage(
                Component.literal(path.merit() +": ").withColor(0x314878).withStyle(ChatFormatting.BOLD)
                    .append("Not Started.").withColor(0xc41d2a)
            );
        }

    }

    private static int clearAllMeritProgressFromSelfCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if(!(context.getSource().getEntity() instanceof ServerPlayer player)) return 1;
        ProfessionProgress.progressOf(player).clearAllMeritProgress();
        ProfessionProgress.refreshPlayerMerits(player);
        return 0;
    }

    private static int clearAllMeritProgressFromPlayerCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        ProfessionProgress.progressOf(player).clearAllMeritProgress();
        ProfessionProgress.refreshPlayerMerits(player);
        return 0;
    }

    private static int clearMeritProgressFromPlayerCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        ResourceLocation resourceLocation = context.getArgument("meritPath", ResourceLocation.class);
        Merit.Path meritPath = Merit.Path.from(resourceLocation);
        ProfessionProgress.progressOf(player).clearMeritProgress(meritPath);
        ProfessionProgress.refreshPlayerMerits(player);
        return 0;
    }

    private static int grantMeritToPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        ResourceLocation resourceLocation = context.getArgument("meritPath", ResourceLocation.class);
        Merit.Path path = Merit.Path.from(resourceLocation);
        if(path == null) return 1;
        if(context.getSource().getEntity() instanceof ServerPlayer caller && !caller.is(player)) {
            caller.sendSystemMessage(Component.literal(player.getDisplayName().getString() + " has been awarded `" + path + "`"));
        }
        innerGrantMeritToPlayer(path, player, false);
        return 0;
    }

    private static int grantMeritToPlayerFake(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        ResourceLocation resourceLocation = context.getArgument("meritPath", ResourceLocation.class);
        boolean isFake = context.getArgument("isFake", boolean.class);
        Merit.Path path = Merit.Path.from(resourceLocation);
        if(path == null) return 1;
        if(context.getSource().getEntity() instanceof ServerPlayer caller && !caller.is(player)) {
            caller.sendSystemMessage(Component.literal(player.getDisplayName().getString() + " has been awarded `" + path + "`"));
        }
        innerGrantMeritToPlayer(path, player, isFake);
        return 0;
    }

    private static void innerGrantMeritToPlayer(Merit.Path merit, ServerPlayer player, boolean isFake) {
        ProfessionProgress progress = ProfessionProgress.progressOf(player);
        progress.awardMerit(merit, player, isFake);
    }
}
