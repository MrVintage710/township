package com.mrvintage.township.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mrvintage.township.Township;
import com.mrvintage.township.profession.Merit;
import com.mrvintage.township.profession.Profession;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.ClientCommandSourceStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MeritPathArgument implements ArgumentType<Merit.Path> {

    private static final Collection<String> EXAMPLES = Arrays.asList("township:fighter/archer/aim_training", "township:fighter/archer/merit");

    @Override
    public Merit.Path parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        var resourceLocation = ResourceLocation.read(reader);
        Merit.Path result = Merit.Path.from(resourceLocation);
        if (result == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Invalid merit path. Must follow the following format: `<namespace>:<profession>/<specialty>/<merit>`");
        }
        return result;
    }
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Map<Merit.Path, Merit> map = new HashMap<>();
        if(context.getSource() instanceof CommandSourceStack stack) {
            map = Profession.allMerits(stack.getLevel());
        } else if(context.getSource() instanceof ClientCommandSourceStack) {
            Township.LOGGER.warn("Cannot Query merits from Client.");
            return Suggestions.empty();
        }

        for(Merit merit : map.values()) {
            Township.LOGGER.info("SUGGESTING {}", merit);
            builder.suggest(merit.toString());
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

//    public class MeritPathArgumentInfo implements ArgumentTypeInfo<MeritPathArgument, MeritPathArgumentInfo.Template> {
//
//        @Override
//        public void serializeToNetwork(Template template, FriendlyByteBuf friendlyByteBuf) {
//
//        }
//
//        @Override
//        public Template deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
//            return null;
//        }
//
//        @Override
//        public void serializeToJson(Template template, JsonObject jsonObject) {
//
//        }
//
//        @Override
//        public Template unpack(MeritPathArgument meritPathArgument) {
//            return null;
//        }
//
//        public class Template implements ArgumentTypeInfo.Template<MeritPathArgumentInfo> {
//
//            public
//
//            @Override
//            public MeritPathArgumentInfo instantiate(CommandBuildContext commandBuildContext) {
//                return null;
//            }
//
//            @Override
//            public ArgumentTypeInfo<MeritPathArgumentInfo, ?> type() {
//                return null;
//            }
//        }
//    }
}
