package com.mrvintage.township.profession.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

record DealDamage (
    int minDamage,
    int xp,
    int maxXp
//    Either<ExtraCodecs.TagOrElementLocation, List<ExtraCodecs.TagOrElementLocation>> weapons
) implements Goal {

    public static final MapCodec<DealDamage> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.INT.fieldOf("min_damage").orElse(1).forGetter(DealDamage::minDamage),
            Codec.INT.fieldOf("xp").orElse(1).forGetter(DealDamage::xp),
            Codec.INT.fieldOf("max_xp").orElse(1).forGetter(DealDamage::maxXp)
//            Codec.either(
//                ExtraCodecs.TAG_OR_ELEMENT_ID,
//                ExtraCodecs.TAG_OR_ELEMENT_ID.listOf()
//            ).orElse(Either.right(new ArrayList<>())).fieldOf("with").forGetter(DealDamage::weapons)
        ).apply(instance, DealDamage::new)
    );

    @Override
    public MapCodec<? extends Goal> type() {
        return CODEC;
    }

    @Override
    public int calcXp(Event event) {
        return 10;
    }

    @Override
    public boolean accepts(Event event) {
        return event instanceof LivingDamageEvent.Post;
    }
}
