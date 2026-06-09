package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.Township;
import com.mrvintage.township.profession.goal.Goal;
import com.mrvintage.township.registry.DataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record Profession(String name, ResourceLocation icon, Map<String, Specialty> specialties) {

    public static final ResourceKey<Registry<Profession>> REGISTRY_KEY = ResourceKey.createRegistryKey(
        ResourceLocation.fromNamespaceAndPath(Township.MODID, "professions")
    );

    public static final Codec<Profession> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(Profession::name),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(Profession::icon),
            Codec.unboundedMap(Codec.STRING, Specialty.CODEC).fieldOf("specialties").forGetter(Profession::specialties)
        ).apply(instance, Profession::new)
    );

    @Nullable
    public static Merit findMerit(ServerLevel level, Merit.Path path) {
        var registry = level.registryAccess().registry(Profession.REGISTRY_KEY);
        if(registry.isEmpty()) return null;

        var profession = registry.get().get(path.file());
        if(profession == null) return null;

        return profession.getMerit(path);
    }

    @Nullable
    public Merit getMerit(Merit.Path path) {
        if (this.specialties.containsKey(path.speciality())) {
            Specialty specialty = this.specialties.get(path.speciality());
            if (specialty.merits().containsKey(path.merit())) {
                return specialty.merits().get(path.merit());
            }
        }

        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static List<Profession> all() {
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            var registry = connection.registryAccess().registry(Profession.REGISTRY_KEY);
            return registry.map(professions -> professions.stream().toList()).orElseGet(ArrayList::new);
        }

        return new ArrayList<>();
    }

    public static List<Profession> all(ServerLevel level) {
        var registry = level.registryAccess().registry(Profession.REGISTRY_KEY);
        return registry.map(professions -> professions.stream().toList()).orElseGet(ArrayList::new);
    }

    public static Set<Map.Entry<ResourceKey<Profession>, Profession>> entries(ServerLevel level) {
        var registry = level.registryAccess().registry(Profession.REGISTRY_KEY);
        return registry.map(Registry::entrySet).orElse(new HashSet<>());
    }

    @NotNull
    public static Map<Merit.Path, Merit> allMerits(ServerLevel level) {
        var registry = level.registryAccess().registry(Profession.REGISTRY_KEY);
        if (registry.isEmpty()) return new HashMap<>();
        Map<Merit.Path, Merit> merits = new HashMap<>();
        for (var professionEntry : registry.get().entrySet()) {
            for (var specialtyEntry : professionEntry.getValue().specialties().entrySet()) {
                for(var meritEntry : specialtyEntry.getValue().merits().entrySet()) {
                    merits.put(
                        new Merit.Path(professionEntry.getKey().location(), specialtyEntry.getKey(), meritEntry.getKey()),
                        meritEntry.getValue()
                    );
                }
            }
        }

        return merits;
    }

    public record GoalPair(Merit.Path merit, Goal goal) {}

//    public static <E extends Event, T extends Goal<E>> List<GoalPair> allGoalsOfType(ServerLevel level, Class<T> clazz) {
//        return Profession.entries(level).stream().map(profession -> profession.getValue().allGoalsOfType(profession.getKey().location(), clazz)).flatMap(List::stream).toList();
//    }
//
//    public <E extends Event, T extends Goal<E>> List<GoalPair> allGoalsOfType(ResourceLocation location, Class<T> clazz) {
//        return this.specialties.entrySet().stream().map( specialty ->
//            specialty.getValue().merits().entrySet().stream().map(merit ->
//                merit.getValue().goals().stream().filter(clazz::isInstance).map(goal -> new GoalPair(new Merit.Path(location, specialty.getKey(), merit.getKey()), goal)).toList()
//            ).flatMap(List::stream).toList()
//        ).flatMap(List::stream).toList();
//    }
}
