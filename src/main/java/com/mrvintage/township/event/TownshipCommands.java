package com.mrvintage.township.event;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrvintage.township.Township;
import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.MeritProgress;
import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.profession.ProfessionProgress;
import com.mrvintage.township.registry.DataAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber(modid = Township.MODID, value = Dist.DEDICATED_SERVER)
public class TownshipCommands {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Township.MODID);

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal(Township.MODID)
                .then(Commands.literal("progress")
                    .then(Commands.literal("query")
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("meritPath", Merit.Path.arg())
                                .executes(TownshipCommands::queryMeritProgressCommand)
                            ).executes(TownshipCommands::queryAllMeritProgressCommand)
                        )
                    )
                    .then(Commands.literal("clear")
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("meritPath", Merit.Path.arg())
                                .executes(TownshipCommands::queryMeritProgressCommand)
                            ).executes(TownshipCommands::queryAllMeritProgressCommand)
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
        Merit.Path meritPath = context.getArgument("meritPath", Merit.Path.class);

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

    private static void clearAllMeritProgressCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");


    }
}
