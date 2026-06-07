package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record Specialty(String name, ResourceLocation icon, Map<String, Merit> merits) {

    public static final Codec<Specialty> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(Specialty::name),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(Specialty::icon),
            Codec.unboundedMap(Codec.STRING, Merit.CODEC).fieldOf("merits").forGetter(Specialty::merits)
        ).apply(instance, Specialty::new)
    );

    public record Path(ResourceLocation file, String speciality) { }
}
