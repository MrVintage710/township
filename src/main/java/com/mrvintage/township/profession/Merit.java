package com.mrvintage.township.profession;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.event.ServerModEvents;
import com.mrvintage.township.event.TownshipCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public record Merit(String name, int tier, ResourceLocation icon, List<Goal<?>> goals) {
    public static final Codec<Merit> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(Merit::name),
            Codec.INT.fieldOf("tier").forGetter(Merit::tier),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(Merit::icon),
            Goal.CODEC.listOf().orElse(new ArrayList<>()).fieldOf("goals").forGetter(Merit::goals)
        ).apply(instance, Merit::new)
    );

    public record Path(ResourceLocation file, String speciality, String merit) {

        public static final Codec<Merit.Path> CODEC = ResourceLocation.CODEC.comapFlatMap(
            location -> {
                Merit.Path result = Merit.Path.from(location);
                if(result == null) {
                    return DataResult.error(() -> location + " is not a valid merit path. Must follow the following format: `<namespace>:<profession>/<specialty>/<merit>`");
                } else {
                    return DataResult.success(result);
                }
            },
            Merit.Path::toResourceLocation
        );

        public static final class Argument implements ArgumentType<Merit.Path> {
            @Override
            public Path parse(StringReader reader) throws CommandSyntaxException {
                var resourceLocation = ResourceLocation.read(reader);
                Merit.Path result = Merit.Path.from(resourceLocation);
                if (result == null) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Invalid merit path. Must follow the following format: `<namespace>:<profession>/<specialty>/<merit>`");
                }
                return  result;
            }

            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
                return builder.suggest("Test").buildFuture();
            }
        }

        public static final class ArgumentTemplate implements ArgumentTypeInfo.Template<Merit.Path.Argument> {

            @Override
            @NotNull
            public Argument instantiate(@NotNull CommandBuildContext commandBuildContext) {
                return new Merit.Path.Argument();
            }

            @Override
            public ArgumentTypeInfo<Argument, ?> type() {
                return new Merit.Path.ArgumentInfo();
            }
        }

        public static final class ArgumentInfo implements ArgumentTypeInfo<Merit.Path.Argument, ArgumentTemplate> {

            @Override
            public void serializeToNetwork(ArgumentTemplate argumentTemplate, FriendlyByteBuf friendlyByteBuf) {

            }

            @Override
            public ArgumentTemplate deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
                return new ArgumentTemplate();
            }

            @Override
            public void serializeToJson(ArgumentTemplate argumentTemplate, JsonObject jsonObject) {

            }

            @Override
            public ArgumentTemplate unpack(Argument argument) {
                return new ArgumentTemplate();
            }
        }

        public static Argument arg() {
            return new Argument();
        }

        @Nullable
        @OnlyIn(Dist.DEDICATED_SERVER)
        public Merit getMeritServer(ServerLevel level) {
            var registry = level.registryAccess().registry(Profession.REGISTRY_KEY);
            if (registry.isPresent()) {
                var proficiencies = registry.get().get(this.file);
                if (proficiencies != null) {
                    return proficiencies.getMerit(this);
                }
            }

            return null;
        }

        @Nullable
        @OnlyIn(Dist.CLIENT)
        public Merit getMeritClient() {
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                var registry = connection.registryAccess().registry(Profession.REGISTRY_KEY);
                if (registry.isPresent()) {
                    var proficiencies = registry.get().get(this.file);
                    if (proficiencies != null) {
                        return proficiencies.getMerit(this);
                    }
                }
            }

            return null;
        }

        @Nullable
        public static Merit.Path from(ResourceLocation location) {
            String namespace = location.getNamespace();
            String path = location.getPath();
            String[] split = path.split("/");
            if(split.length < 3) {
                return null;
            }
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(namespace, split[0]);
            return new Merit.Path(key, split[1], split[2]);
        }

        @Nullable
        public static Merit.Path from(@Nullable String location) {
            if(location == null) return null;
            ResourceLocation resourceLocation = ResourceLocation.tryParse(location);
            if (resourceLocation != null) return Merit.Path.from(resourceLocation);
            return null;
        }

        public ResourceLocation toResourceLocation() {
            return ResourceLocation.fromNamespaceAndPath(this.file.getNamespace(), this.file.getPath() + "/" + this.speciality + "/" + this.merit);
        }

        @Override
        @NotNull
        public String toString() {
            return this.file + "/" + this.speciality + "/" + this.merit;
        }
    }
}
