package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.Township;
import com.mrvintage.township.event.ServerModEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record Profession(String name, String id, ResourceLocation icon, Map<String, Specialty> specialties) {

    public static final ResourceKey<Registry<Profession>> REGISTRY_KEY = ResourceKey.createRegistryKey(
        ResourceLocation.fromNamespaceAndPath(Township.MODID, "professions")
    );

    public static final Codec<Profession> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(Profession::name),
            Codec.STRING.fieldOf("id").forGetter(Profession::id),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(Profession::icon),
            Codec.unboundedMap(Codec.STRING, Specialty.CODEC).fieldOf("specialties").forGetter(Profession::specialties)
        ).apply(instance, Profession::new)
    );

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

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static List<Profession> all(ServerLevel level) {
        var registry = level.registryAccess().registry(Profession.REGISTRY_KEY);
        return registry.map(professions -> professions.stream().toList()).orElseGet(ArrayList::new);
    }
}
