package com.mrvintage.township.event;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrvintage.township.Township;
import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.registry.DataAttachments;
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
                .then(Commands.literal("query")
                    .then(Commands.literal("progress")
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("meritPath", Merit.Path.arg())
                                .executes(TownshipCommands::queryMeritProgressCommand)
                            )
                        )
                    )
                )
        );
    }

    private static int queryMeritProgressCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        Merit.Path meritPath = context.getArgument("meritPath", Merit.Path.class);

        var attachments = NeoForgeRegistries.ATTACHMENT_TYPES.stream().toList();

        if (player.hasData(DataAttachments.PROFESSION_PROGRESS)) {
            //Display the Progress
        } else {
            player.sendSystemMessage(Component.literal("There is no progress for the given merit."));
        }

        return 0;
    }
}
