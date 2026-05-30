package com.mrvintage.township.proficiency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import java.util.List;

public class Proficiency implements INBTSerializable<CompoundTag> {

    public static final Codec<Proficiency> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceLocation.CODEC.listOf().fieldOf("domain").forGetter(Proficiency::getDomain),
                ResourceLocation.CODEC.fieldOf("icon").forGetter(Proficiency::getIcon),
                Codec.STRING.fieldOf("name").forGetter(Proficiency::getName)
            ).apply(instance, Proficiency::new)
    );

    private final List<ResourceLocation> domain;
    private final ResourceLocation icon;
    private final String name;

    public Proficiency(List<ResourceLocation> domain, ResourceLocation icon, String name) {
        this.domain = domain;
        this.icon = icon;
        this.name = name;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public List<ResourceLocation> getDomain() {
        return domain;
    }

    public String getName() {
        return name;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {

    }
}
