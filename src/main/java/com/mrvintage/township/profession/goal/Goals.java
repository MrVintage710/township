package com.mrvintage.township.profession.goal;

import com.mojang.serialization.MapCodec;
import com.mrvintage.township.Township;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class Goals {

    public static final ResourceKey<Registry<MapCodec<? extends Goal>>> DISPATCH_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Township.MODID, "goals"));
    public static final Registry<MapCodec<? extends Goal>> DISPATCH = new RegistryBuilder<>(DISPATCH_KEY)
        .create();


    public static final DeferredRegister<MapCodec<? extends Goal>> DEFERRED_DISPATCH = DeferredRegister.create(DISPATCH, Township.MODID);
    public static final Supplier<MapCodec<? extends Goal>> DEAL_DAMAGE_CODEC = DEFERRED_DISPATCH.register("deal_damage", () -> DealDamage.CODEC);

}
