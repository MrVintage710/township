package com.mrvintage.township.profession;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.Township;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Goal<T extends Event> {

    ResourceKey<Registry<MapCodec<? extends Goal<?>>>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Township.MODID, "goals"));
    Registry<MapCodec<? extends Goal<?>>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY)
        .create();
    Codec<Goal<?>> CODEC = REGISTRY.byNameCodec().dispatch(Goal::type, Function.identity());

    DeferredRegister<MapCodec<? extends Goal<?>>> DEFERRED_REGISTRY = DeferredRegister.create(REGISTRY, Township.MODID);

    MapCodec<? extends Goal<?>> type();

    boolean check(T event);

    record DealDamage (
        int requiredSuccesses,
        int minDamage,
        Either<ExtraCodecs.TagOrElementLocation, List<ExtraCodecs.TagOrElementLocation>> weapons
    ) implements Goal<LivingDamageEvent.Post> {

        private static final MapCodec<DealDamage> CODEC_DEF = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Codec.INT.fieldOf("required_successes").orElse(1).forGetter(DealDamage::requiredSuccesses),
                Codec.INT.fieldOf("minDamage").orElse(1).forGetter(DealDamage::minDamage),
                Codec.either(
                    ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("with").codec(),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().fieldOf("with").codec()
                ).fieldOf("with").forGetter(DealDamage::weapons)
            ).apply(instance, DealDamage::new)
        );

        public static final Supplier<MapCodec<DealDamage>> CODEC = DEFERRED_REGISTRY.register("dealDamage", () -> DealDamage.CODEC_DEF);

        @Override
        public MapCodec<? extends Goal<?>> type() {
            return CODEC.get();
        }

        @Override
        public boolean check(LivingDamageEvent.Post event) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {

            }
            return false;
        }
    }
}
