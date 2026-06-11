package com.mrvintage.township.profession.reward;

import com.mojang.serialization.MapCodec;
import com.mrvintage.township.Township;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class Rewards {

    public static final ResourceKey<Registry<MapCodec<? extends Reward>>> DISPATCH_KEY =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Township.MODID, "rewards"));
    public static final Registry<MapCodec<? extends Reward>> DISPATCH = new RegistryBuilder<>(DISPATCH_KEY)
        .create();


    public static final DeferredRegister<MapCodec<? extends Reward>> DEFERRED_DISPATCH = DeferredRegister.create(DISPATCH, Township.MODID);

    public static final Supplier<MapCodec<? extends Reward>> COMMAND_REWARD = DEFERRED_DISPATCH.register("command", () -> CommandReward.CODEC);
}
