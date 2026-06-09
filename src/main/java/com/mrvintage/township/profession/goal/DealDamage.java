package com.mrvintage.township.profession.goal;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.profession.Merit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

record DealDamage (
    int minDamage,
    int xp,
    int maxXp,
    Either<ExtraCodecs.TagOrElementLocation, List<ExtraCodecs.TagOrElementLocation>> weapons
) implements Goal<LivingDamageEvent.Post> {

    public static final MapCodec<DealDamage> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.INT.fieldOf("min_damage").orElse(1).forGetter(DealDamage::minDamage),
            Codec.INT.fieldOf("xp").orElse(1).forGetter(DealDamage::xp),
            Codec.INT.fieldOf("max_xp").orElse(1).forGetter(DealDamage::maxXp),
            Codec.either(
                ExtraCodecs.TAG_OR_ELEMENT_ID,
                ExtraCodecs.TAG_OR_ELEMENT_ID.listOf()
            ).orElse(Either.right(new ArrayList<>())).fieldOf("with").forGetter(DealDamage::weapons)
        ).apply(instance, DealDamage::new)
    );

    public List<ExtraCodecs.TagOrElementLocation> getWeapons() {
        return this.weapons.map(Lists::newArrayList, Function.identity());
    }

    public boolean weaponsMatch(ItemStack stack) {
        for(ExtraCodecs.TagOrElementLocation item : getWeapons()) {
            if(item.tag()) {
                var tag = ItemTags.create(item.id());
                boolean result = stack.is(tag);
                if(result) return result;
            } else {
                Item i = BuiltInRegistries.ITEM.get(item.id());
                boolean result = stack.is(i);
                if(result) return result ;
            }
        }

        return false;
    }

    @Override
    public MapCodec<? extends Goal<?>> type() {
        return CODEC;
    }

    @Override
    public int calcXp(LivingDamageEvent.Post event, Merit merit) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return 0;
        if (!weaponsMatch(player.getWeaponItem())) return 0;
        if (event.getNewDamage() < this.minDamage) return 0;
        
        return 10;
    }

    @Override
    public boolean accepts(Event event) {
        return event instanceof LivingDamageEvent.Post;
    }
}
