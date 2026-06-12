package com.mrvintage.township.sound;

import com.mrvintage.township.Township;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Sounds {

    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Township.MODID);

    public static final Supplier<SoundEvent> LEVEL_UP = register("level_up");

    public static Supplier<SoundEvent> register(String name) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(Township.MODID, name);
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(resourceLocation));
    }
}
